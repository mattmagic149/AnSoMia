package Mining;
import org.hibernate.criterion.Criterion;
import org.javatuples.Pair;
import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import DatabaseClasses.Company;
import DatabaseClasses.MarketValue;
import General.MainApplication;
import Support.HibernateSupport;

public class HistoryMarketValuesCrawler extends Crawler implements Job
{
	private String wall_street_url = "http://www.wallstreet-online.de";
	private String request_url = "/_plain/instrument/default/module/quotehistory/";
	private ArrayList<Pair<Integer, String>> market_places;
	
	public HistoryMarketValuesCrawler() {
		this.name = "HistoryMarketValuesCrawler";
		market_places = new ArrayList<Pair<Integer, String>>();
		market_places.add(Pair.with(1, "Xetra"));
		market_places.add(Pair.with(2, "Frankfurt"));
		market_places.add(Pair.with(3, "Berlin"));
		market_places.add(Pair.with(4, "Düsseldorf"));
		market_places.add(Pair.with(5, "Hamburg"));
		market_places.add(Pair.with(6, "München"));
		market_places.add(Pair.with(21, "Tradegate"));
		market_places.add(Pair.with(22, "Lang & Schwarz"));
		//market_places.add(Pair.with(41, "Deutsche Bank"));
		//market_places.add(Pair.with(45, "Swiss Exchange"));
	}
	
	@Override	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println(MainApplication.isin_mutex_map.size());
		MainApplication.isin_mutex_map.clear();
		List<Criterion>  criterions = new ArrayList<Criterion>();
		List<Company> companies = new ArrayList<Company>();
		companies = HibernateSupport.readMoreObjects(Company.class, criterions);
		//Company company = HibernateSupport.readOneObjectByStringId(Company.class, "US3789671035");
		//companies.add(company);
		for(int i = 0; i < companies.size(); i++) {
			MainApplication.isin_mutex_map.put(companies.get(i).getIsin(), new ReentrantLock(true));
		}
		
		System.out.println(MainApplication.isin_mutex_map.size());
		
		HistoryMarketValuesCrawler mvc = new HistoryMarketValuesCrawler();
		try {
			mvc.startCrawling();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected boolean crawlInfos(Company company) {
		if(company == null || company.getWallstreetQueryString() == null || 
				company.getWallstreetQueryString() == "" || 
				company.getWallstreetQueryString().equals("null") ||
				company.getWallstreetQueryString().equals("NULL")) {
			System.out.println("Company is NULL...");
			return false;
		}
		
		
		
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		
		Date current_month;
		Calendar current_calendar = Calendar.getInstance();
		int number_of_added_values = 0;
		
		int market_place_counter = 0;
		Pair<Integer, String> market_place = this.market_places.get(market_place_counter++);
		int market_places_size = this.market_places.size();
		Element response;
		
		int connection_attempts = 0;
		
		while(true) {
			current_calendar.clear();
			current_calendar.set(Calendar.MONTH, month - 1);
			current_calendar.set(Calendar.YEAR, year);
			current_month = current_calendar.getTime();
			
			//don't crawl market values if, there already exists 10 or more values.
			number_of_added_values = company.getNumberOfAddedDatesOfParticularMonthAndYear(current_month);
			System.out.println(current_month);
			if(number_of_added_values >= 10) {
				System.out.println("getNumberOfAddedDatesOfParticularMonthAndYear = " + number_of_added_values);
				if(--month == 0) {
					month = 12;
					year--;
				}
				break; //TODO: for better experience use continue.
			}	
			
			try {
				response = Jsoup.connect(wall_street_url + request_url + 
						company.getWallstreetId() + "/" + market_place.getValue0() +
						"/" + year + "/" + month).execute().parse();
			} catch(IOException e) {
				//tries to connect 3 times in a row, if it fails, it returns
				if(++connection_attempts == 3) {
					System.out.println("returning after 3 connection attempt.");
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					
					return false;
				} else {
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					
					continue;
				}
			}
			
			connection_attempts = 0;
						
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(response.toString().contains("Es wurden keine Daten gefunden")) {
				if(market_place_counter >= market_places_size) {
					break;
				} else {
					market_place = market_places.get(market_place_counter++);
				}
			} else {
				//decrement
				if(--month == 0) {
					month = 12;
					year--;
				}
				
				this.addDatesToCompany(company, response, market_place.getValue1());
			}
				
		}

		return true;
	}
	
	private boolean addDatesToCompany(Company company, Element response, String market_place) {
		
		MarketValue market_value;
		Elements table_rows = response.select("table tbody tr");
		Element table_row;
		
		Elements table_cells;
		float open, high, low, close, performance, volume, revenue;
		Date date;
		DateFormat date_format = new SimpleDateFormat("dd.MM.yy");	
		Elements table_header = response.select("table thead tr th");
		Element header_cell;
		
		int open_index = -1, high_index = -1, low_index = -1, close_index = -1,
				performance_index = -1, volume_index = -1, revenue_index = -1, date_index = -1;
		
		for(int i = 0; i < table_header.size(); i++) {
			header_cell = table_header.get(i);
			
			if(header_cell.html().contains("Datum")) {date_index = i;}
			if(header_cell.html().contains("Open")) {open_index = i;}
			if(header_cell.html().contains("High")) {high_index = i;}
			if(header_cell.html().contains("Low")) {low_index = i;}
			if(header_cell.html().contains("Close")) {close_index = i;}
			if(header_cell.html().contains("Perf.")) {performance_index = i;}
			if(header_cell.html().contains("Volumen")) {volume_index = i;}
			if(header_cell.html().contains("Umsatz")) {revenue_index = i;}

		}
		
		HibernateSupport.beginTransaction();

		for(int i = 0; i < table_rows.size(); i++) {
			table_row = table_rows.get(i);
			table_cells = table_row.select("td");
			
			try {
				date = table_header.html().contains("Datum") ? 
						date_format.parse(table_cells.get(date_index).child(0).html()) : new Date();
			} catch (ParseException e1) {
				e1.printStackTrace();
				continue;
			}
			
			open = table_header.html().contains("Open") ? 
					this.parseFloat(table_cells.get(open_index).child(0).html(), 0) : 0;
										
			high = table_header.html().contains("High") ? 
					this.parseFloat(table_cells.get(high_index).child(0).html(), 0) : 0;
					
			low = table_header.html().contains("Low") ? 
					this.parseFloat(table_cells.get(low_index).child(0).html(), 0) : 0;
			
			close = table_header.html().contains("Close") ? 
					this.parseFloat(table_cells.get(close_index).child(0).html(), 0) : 0;
			
			performance = table_header.html().contains("Perf.") ? 
					this.parseFloat(table_cells.get(performance_index).child(0).select("font").html()
											.replace("&nbsp;%", ""), 0) : 0;
			
			volume = table_header.html().contains("Volumen") ? 
					this.parseFloat(table_cells.get(volume_index).child(0).html(), 0) : 0;
					
			revenue = table_header.html().contains("Umsatz") ? 
					this.parseFloat(table_cells.get(revenue_index).child(0).html(), 0) : 0;
			
			
			market_value = new MarketValue(company, date, open, high, low, close, performance,
						volume, revenue, "EUR", market_place);
			
			company.addMarketValue(market_value);

		}
		
		HibernateSupport.commitTransaction();
		
		return true;
	}
  
}