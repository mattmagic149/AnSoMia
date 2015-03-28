package General;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.javatuples.Pair;
import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.*;

import org.w3c.dom.Document;

import Support.HibernateSupport;

public class MarketValuesCrawler
{
	private static String wall_street_url = "http://www.wallstreet-online.de";
	private static String share_string = "/aktien/";
	private static String market_value_string = "/kurse";
	private static List<SingleCompany> company_not_crawled = new ArrayList<SingleCompany>();

	
	public static void main( String[] args ) throws Exception
	{
	 
		

		List<Criterion>  criterions = new ArrayList<Criterion>();
		List<SingleCompany> companies = HibernateSupport.readMoreObjects(SingleCompany.class, criterions);

		int companies_size = companies.size();
		int timeout_counter = 0;
		boolean success = true;

		for(int i = 0; i < companies_size; i++) {
			System.out.println("Crawling Company: " + companies.get(i).getCompanyName() + ", "
					  + companies.get(i).getIsin() + ", " + companies.get(i).getTicker());
			try {
				crawlSingleCompany(companies.get(i));
			} catch(SocketTimeoutException e) {
				success = false;
				if(timeout_counter < 3) {
					i--;
					timeout_counter++;
				} else {
					success = true;
				}
			} catch(IOException e) {
				System.out.println(e);
				company_not_crawled.add(companies.get(i));
			}
			  
			if(success == true) {
				timeout_counter = 0;
			}
			success = true;
		  	System.out.print("Crawled ");
		  	System.out.printf("%.2f", ((i + 1)/(float)companies_size) * 100);
		  	System.out.println(" % - " + (company_not_crawled.size()) + " not crawled");
			//Thread.sleep(100);
		  }
		
		for(int i = 0; i < company_not_crawled.size(); i++) {
			System.out.println("COMPANY: " + company_not_crawled.get(i).getCompanyName() + " " + 
					  			company_not_crawled.get(i).getIsin() + " " +
					  			company_not_crawled.get(i).getTicker());
		}
	  
	  
	  //Elements finance_highlights = doc.select(".ui-widget");
	  
	 //System.out.println(finance_highlights.get(0));
	  	  	 
	}
	
	private static void crawlSingleCompany(SingleCompany company) throws IOException {
		if(company == null) {
			return;
		} else if(company.getWallstreetQueryString() == null || company.getWallstreetQueryString() == "") {
			String isin = company.getIsin();
			Connection.Response response = Jsoup.connect(wall_street_url + "/suche/?suche=&q=" + isin).execute();
			String[] url_split = response.url().toString().split("/");
			company.setWallstreetQueryString(url_split[url_split.length - 1]);
			HibernateSupport.beginTransaction();
			company.saveToDB();
			HibernateSupport.commitTransaction();
			System.out.println("Company has no queryString...");
		}
		
		Element response = Jsoup.connect(wall_street_url + share_string + 
				company.getWallstreetQueryString() + market_value_string).get();
		
		///TODO check http status!!!
	  
		//System.out.println(response.select(".module").size());
		
		Elements modules = response.select(".module");
		int length = modules.size();
		
		for(int i = 0; i < length; i++) {
			if(modules.get(i).html().contains("Deutsche Handelsplätze")) {
				ArrayList<Integer> column_indexes = getColumnIndexes(modules.get(i).select("tr th"));
				int row_index = getRowIndex(modules.get(i).select("tbody tr"));
				float stock_price = -1;
				float bid_price = -1;
				float ask_price = -1;
				String market_place = modules.get(i).select("tbody tr").get(row_index).child(0).child(0).child(0).child(0).child(0).html();
								
				if(!modules.get(i).select("tbody tr").get(row_index).child(column_indexes.get(0)).html().contains("-")) {
					stock_price = Float.parseFloat(modules.get(i).select("tbody tr").get(row_index).child(column_indexes.get(0)).child(0).child(0).html().replace(".", "").replace(",", "."));
				}
				if(!modules.get(i).select("tbody tr").get(row_index).child(column_indexes.get(1)).html().contains("-")) {
					bid_price = Float.parseFloat(modules.get(i).select("tbody tr").get(row_index).child(column_indexes.get(1)).child(0).child(0).html().replace(".", "").replace(",", "."));
				}
				if(!modules.get(i).select("tbody tr").get(row_index).child(column_indexes.get(2)).html().contains("-")) {
					ask_price = Float.parseFloat(modules.get(i).select("tbody tr").get(row_index).child(column_indexes.get(2)).child(0).child(0).html().replace(".", "").replace(",", "."));
				}
				
				//System.out.println("StockPrice = " + stock_price + " BidPrice = " + bid_price + " AskPrice = " + ask_price);
				//System.out.println(market_place);
				MarketValues market_values = new MarketValues(company, market_place, stock_price, bid_price, ask_price);
				company.addMarketValues(market_values);
				
				HibernateSupport.beginTransaction();
				company.saveToDB();
				HibernateSupport.commitTransaction();

			}
			
		}
	}
	
	private static int getRowIndex(Elements elements) {
		int result = -1;
		//System.out.println(elements.size());
		
		int length = elements.size();
		for(int i = 0; i < length; i++) {
			if(elements.get(i).html().contains("Xetra")) {
				result = i;
			}
		}
		
		if(result == -1) {
			for(int i = 0; i < length; i++) {
				if(elements.get(i).html().contains("Frankfurt")) {
					result = i;
				}
			}
		}
		
		if(result == -1) {
			for(int i = 0; i < length; i++) {
				if(elements.get(i).html().contains("Berlin")) {
					result = i;
				}
			}
		}
		
		if(result == -1) {
			///TODO throw exception
		}
		
		return result;
	}
	
	private static ArrayList<Integer> getColumnIndexes(Elements elements) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		int length = elements.size();
		for(int i = 0; i < length; i++) {
			if(elements.get(i).html().contains("Kurs") && !elements.get(i).html().contains("Kurs Stück")) {
				result.add(i);
			}
		}
		
		for(int i = 0; i < length; i++) {
			if(elements.get(i).html().contains("Bid")) {
				result.add(i);
			}
		}
		
		for(int i = 0; i < length; i++) {
			if(elements.get(i).html().contains("Ask")) {
				result.add(i);
			}
		}
		
		if(result.size() != 3) {
			///TODO throw exception
		}
		
		return result;
	}
  
  
}