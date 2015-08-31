package test;
import mining.Crawler;

import org.jsoup.*;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import utils.HibernateSupport;
import database.Company;
import database.MarketValue;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;

public class MarketValuesCrawler extends Crawler implements Job
{
	private String wall_street_url = "http://www.wallstreet-online.de";
	private String share_string = "/aktien/";
	private String market_value_string = "/kurse";
	private String[] column_values = {"Handelsplatz", "Kurs", "Währung", "Bid", "Ask"};
	private String[] market_place_strings = {"Xetra", "Tradegate", "Frankfurt", "Berlin"};
	private Date date;

	
	@Override	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		MarketValuesCrawler mvc = new MarketValuesCrawler();
		mvc.startCrawling();
	}
	
	protected boolean crawlInfos(Company company) {
		if(company == null || company.getWallstreetQueryString() == null || company.getWallstreetQueryString() == "") {
			System.out.println("Company is NULL...");
			return false;
		}
		
		System.out.println("crawlKpis marketvalues");
		
		Response response_abs;
		Element response;

		try {
			response_abs = Jsoup.connect(wall_street_url + share_string + 
					company.getWallstreetQueryString() + market_value_string).execute();
		} catch(SocketTimeoutException e) {
			return false;
		} catch(IOException e) {
			return false;
		}
				
		if(response_abs.url().toString().equals(wall_street_url + share_string + 
				company.getWallstreetQueryString() + market_value_string)) {
			

			try {
				response = response_abs.parse();
			} catch (IOException e) {
				return false;
			}
				
			Elements market_places = response.select("#main_content .container");
			
			if(market_places.size() < 1) {
				System.out.println("no #main_content .container available...");
				return false;
			}
			
			
			Element market_place = market_places.get(1);
			int[] table_column_indecis = getTableColumnIndecis(market_place);
			Element market_place_row = getMarketPlaceColumn(market_place);
			if(market_place_row == null) {
				System.out.println("no #main_content .container available...");
				return false;
			}
			
			String market_place_string = market_place_row.child(table_column_indecis[0]).child(0).child(0).child(0).child(0).html();
			String currency = market_place_row.child(table_column_indecis[2]).child(0).html();
			float stock_price = -1;
			float bid_price = -1;
			float ask_price = -1;
					
			if(!market_place_row.child(table_column_indecis[1]).child(0).html().contains("-")) {
				stock_price = Float.parseFloat(market_place_row.child(table_column_indecis[1]).child(0).child(0).html().replace(".", "").replace(",", "."));
				System.out.println("stock_price = " + stock_price);
			}
			
			if(!market_place_row.child(table_column_indecis[3]).child(0).html().contains("-")) {
				bid_price = Float.parseFloat(market_place_row.child(table_column_indecis[3]).child(0).child(0).html().replace(".", "").replace(",", "."));
				System.out.println("bid_price = " + bid_price);
			}
			
			if(!market_place_row.child(table_column_indecis[4]).child(0).html().contains("-")) {
				ask_price = Float.parseFloat(market_place_row.child(table_column_indecis[4]).child(0).child(0).html().replace(".", "").replace(",", "."));
				System.out.println("ask_price = " + ask_price);
			}
			
			MarketValue market_values = new MarketValue(company, market_place_string, stock_price, bid_price, ask_price, currency, date);
			
			HibernateSupport.beginTransaction();
			company.addMarketValue(market_values);		
			company.saveToDB();
			HibernateSupport.commitTransaction();
		}
		
		return true;
	}
	
	private int[] getTableColumnIndecis(Element market_place) {
		int[] ret = {-1,-1,-1,-1,-1};
		
		Elements table_header_columns = market_place.select(".module table").get(0).select("thead tr th");
		
		for(int i = 0; i < column_values.length; i++) {
			for(int j = 0; j < table_header_columns.size(); j++) {
				if(table_header_columns.get(j).html().equals(column_values[i])) {
					ret[i] = j;
				}
			}
		}
		
		return ret;
	}
	
	private Element getMarketPlaceColumn(Element market_place) {
		
		Elements tables = market_place.select("table");
		for(int i = 0; i < market_place_strings.length; i++) {
			
			if(tables.select("div:contains(" + market_place_strings[i] + ")").size() != 0) {
				return tables.select("div:contains(" + market_place_strings[i] + ")").first().parent().parent();
			}
		}
		
		return null;
	}  
  
}