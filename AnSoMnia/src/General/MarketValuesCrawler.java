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
	private static String[] column_values = {"Handelsplatz", "Kurs", "Währung", "Bid", "Ask"};
	private static String[] market_place_strings = {"Xetra", "Tradegate", "Frankfurt", "Berlin"};

	
	public static void main( String[] args ) throws Exception
	{
		
		List<Criterion>  criterions = new ArrayList<Criterion>();
		List<SingleCompany> companies = HibernateSupport.readMoreObjects(SingleCompany.class, criterions);
		//SingleCompany cmp = HibernateSupport.readOneObjectByStringId(SingleCompany.class, "AT000000STR1");

	
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
			System.out.println("Company is NULL...");
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
		
		Elements market_places = response.select("#main_content .container");
		
		if(market_places.size() < 1) {
			throw new IOException("no #main_content .container available...");
		}
		
		
		Element market_place = market_places.get(1);
		int[] table_column_indecis = getTableColumnIndecis(market_place);
		Element market_place_row = getMarketPlaceColumn(market_place);
		if(market_place_row == null) {
			throw new IOException("no #main_content .container available...");
		}
		
		String market_place_string = market_place_row.child(table_column_indecis[0]).child(0).child(0).child(0).child(0).html();
		String currency = market_place_row.child(table_column_indecis[2]).child(0).html();
		float stock_price = -1;
		float bid_price = -1;
		float ask_price = -1;
				
		if(!market_place_row.child(table_column_indecis[1]).child(0).html().contains("-")) {
			stock_price = Float.parseFloat(market_place_row.child(table_column_indecis[1]).child(0).child(0).html().replace(".", "").replace(",", "."));
		}
		
		if(!market_place_row.child(table_column_indecis[3]).child(0).html().contains("-")) {
			bid_price = Float.parseFloat(market_place_row.child(table_column_indecis[3]).child(0).child(0).html().replace(".", "").replace(",", "."));
		}
		
		if(!market_place_row.child(table_column_indecis[4]).child(0).html().contains("-")) {
			ask_price = Float.parseFloat(market_place_row.child(table_column_indecis[4]).child(0).child(0).html().replace(".", "").replace(",", "."));
		}
		
		MarketValues market_values = new MarketValues(company, market_place_string, stock_price, bid_price, ask_price, currency);
		company.addMarketValues(market_values);
		
		HibernateSupport.beginTransaction();
		company.saveToDB();
		HibernateSupport.commitTransaction();
		
	}
	
	private static int[] getTableColumnIndecis(Element market_place) {
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
	
	private static Element getMarketPlaceColumn(Element market_place) {
		
		Elements tables = market_place.select("table");
		for(int i = 0; i < market_place_strings.length; i++) {
			
			if(tables.select("div:contains(" + market_place_strings[i] + ")").size() != 0) {
				return tables.select("div:contains(" + market_place_strings[i] + ")").first().parent().parent();
			}
		}
		
		return null;
	}
  
  
}