package General;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.javatuples.Pair;
import org.javatuples.Tuple;
import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.*;

import org.w3c.dom.Document;

import Support.HibernateSupport;

public class KPICrawler
{
	private static String wall_street_url = "http://www.wallstreet-online.de";
	private static String share_string = "/aktien/";
	private static String balance_sheet_string = "/bilanz";
	private static String company_profile = "/unternehmensprofil";
	private static List<SingleCompany> company_not_crawled = new ArrayList<SingleCompany>();
	private static String[] column_values = {"Handelsplatz", "Kurs", "Währung", "Bid", "Ask"};
	private static String[] market_place_strings = {"Xetra", "Tradegate", "Frankfurt", "Berlin"};

	
	public static void main( String[] args ) throws Exception
	{
		
		//List<Criterion>  criterions = new ArrayList<Criterion>();
		//List<SingleCompany> companies = HibernateSupport.readMoreObjects(SingleCompany.class, criterions);
		SingleCompany cmp = HibernateSupport.readOneObjectByStringId(SingleCompany.class, "AT000000STR1");
		
		crawlKpis(cmp);
	  	  	 
	}
	
	private static void crawlKpis(SingleCompany company) throws IOException {
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
				company.getWallstreetQueryString() + balance_sheet_string).get();
		
		///TODO check http status!!!

		
		Elements data_tables = response.select("#main_content .t-data");
		Elements table_header = data_tables.first().children().first().children().first().children();
		
		int year = 0;
		for(int i = 0; i < table_header.size(); i++) {
			year = extractYearFromString(table_header.get(i).html());
			if(year != -1) {
				//calendar.set(year, 0, 0);
				Pair<Integer, Integer> year_pair = new Pair<Integer,Integer>(i, year);
				updateBalanceSheetValues(company, data_tables, year_pair);

			}

		}
		
		response = Jsoup.connect(wall_street_url + share_string + 
				company.getWallstreetQueryString() + company_profile).get();
		
		data_tables = response.select("#main_content .t-data");
		table_header = data_tables.first().children().first().children().first().children();
		
		year = 0;
		for(int i = 0; i < table_header.size(); i++) {
			year = extractYearFromString(table_header.get(i).html());
			if(year != -1) {
				//calendar.set(year, 0, 0);
				Pair<Integer, Integer> year_pair = new Pair<Integer,Integer>(i, year);
				updateProfileKpis(company, data_tables, year_pair);

			}

		}
		
		
	}
	
	static public boolean updateProfileKpis(SingleCompany company, Elements tables, Pair<Integer, Integer> year_pair) {
		
		boolean existing_indicators = true;
		KeyPerformanceIndicators indicators = company.getKpisCorrespondingToYear(year_pair.getValue1());
		
		if(indicators == null){
			indicators = new KeyPerformanceIndicators(year_pair.getValue1(), company);
			existing_indicators = false;
		}
		

		float dividend = -1, equity_ratio = -1, liquidity_1 = -1, liquidity_2 = -1,
		liquidity_3 = -1;
		long working_capital = -1, number_shares = -1;
		int number_employee = -1;
		Elements tmp = null;
		String row_description = null;
		for(int i = 0; i < tables.size(); i++) {
			tmp = tables.get(i).select("tbody tr");
			for(int j = 0; j < tmp.size(); j++) {
				//System.out.println(tmp.get(j).child(0).html());
				row_description = tmp.get(j).child(0).html();

				if(row_description.contains("Umsatz je Aktie")) {
					dividend = parseFloat(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("dividend " + year_pair.getValue1() + " = " + dividend);
				} else if(row_description.contains("Working Capital")) {
					///TODO: what if it is NOT in Mio?!?!?!
					working_capital = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("working_capital " + year_pair.getValue1() + " = " + working_capital);
				} else if(row_description.contains("Eigenkapitalquote")) {
					equity_ratio = parseFloat(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("equity_ratio " + year_pair.getValue1() + " = " + equity_ratio);
				} else if(row_description.contains("Anzahl Mitarbeiter")) {
					number_employee = (int)parseFloat(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("number_employee " + year_pair.getValue1() + " = " + number_employee);
				} else if(row_description.contains("‎Liquidität 1. Grades")) {
					liquidity_1 = parseFloat(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("liquidity_1 " + year_pair.getValue1() + " = " + liquidity_1);
				} else if(row_description.contains("‎Liquidität 2. Grades")) {
					liquidity_2 = parseFloat(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("liquidity_2 " + year_pair.getValue1() + " = " + liquidity_2);
				} else if(row_description.contains("‎Liquidität 3. Grades")) {
					liquidity_3 = parseFloat(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("liquidity_3 " + year_pair.getValue1() + " = " + liquidity_3);
				} else if(row_description.contains("‎Aktien im Umlauf in Mio.")) {
					///TODO: what if it is not in Mio?!?!?!
					number_shares = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("number_shares " + year_pair.getValue1() + " = " + number_shares);
				}

			}
		}
		
		indicators.setProfileValues(dividend, equity_ratio, liquidity_1, liquidity_2,
				liquidity_3, number_employee, number_shares, working_capital);
				
		
		if(!existing_indicators) {
			company.addKPIs(indicators);
		}
		
		HibernateSupport.beginTransaction();
		indicators.saveToDB();
		HibernateSupport.commitTransaction();
		
		return true;
	}
	
	static public boolean updateBalanceSheetValues(SingleCompany company, Elements tables, Pair<Integer, Integer> year_pair) {
		
		boolean existing_indicators = true;
		KeyPerformanceIndicators indicators = company.getKpisCorrespondingToYear(year_pair.getValue1());
		
		if(indicators == null){
			indicators = new KeyPerformanceIndicators(year_pair.getValue1(), company);
			existing_indicators = false;
		}
		

		long revenue = -1, operating_income = -1, net_income = -1, cash_flow = -1, equity = -1, debt = -1,
		balance_sheet_total = -1, gross_profit = -1;
		Elements tmp = null;
		String row_description = null;
		for(int i = 0; i < tables.size(); i++) {
			tmp = tables.get(i).select("tbody tr");
			for(int j = 0; j < tmp.size(); j++) {
				row_description = tmp.get(j).child(0).child(0).html();

				if(row_description.contains("Umsatz")) {
					revenue = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("revenue " + year_pair.getValue1() + " = " + revenue);
				} else if(row_description.contains("EBIT")) {
					operating_income = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("operating_income " + year_pair.getValue1() + " = " + operating_income);
				} else if(row_description.contains("Ergebnis nach Steuern")) {
					net_income = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("net_income " + year_pair.getValue1() + " = " + net_income);
				} else if(row_description.contains("Cashflow")) {
					cash_flow = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("cash_flow " + year_pair.getValue1() + " = " + cash_flow);
				} else if(row_description.contains("Bilanzielles Eigenkapital")) {
					equity = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("equity " + year_pair.getValue1() + " = " + equity);
				} else if(row_description.contains("Summe Fremdkapital")) {
					debt = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("debt " + year_pair.getValue1() + " = " + debt);
				} else if(row_description.contains("Passiva")) {
					balance_sheet_total = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("balance_sheet_total " + year_pair.getValue1() + " = " + balance_sheet_total);
				} else if(row_description.contains("Bruttoergebnis vom Umsatz")) {
					gross_profit = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html());
					System.out.println("gross_profit " + year_pair.getValue1() + " = " + gross_profit);
				}

			}
		}
		
		
		indicators.setBalanceSheetValues(revenue, operating_income, net_income,
				cash_flow, equity, debt, balance_sheet_total, gross_profit);
		
				
		
		if(!existing_indicators) {
			company.addKPIs(indicators);
		}
		
		HibernateSupport.beginTransaction();
		indicators.saveToDB();
		HibernateSupport.commitTransaction();
		
		return true;
	}
	
	public static float parseFloat(String s) {
		float ret = -1;
		String tmp = s.split(" ")[0];
		tmp = tmp.replace(".", "");
		tmp = tmp.replace(",", ".");

		try { 
			ret = Float.parseFloat(tmp); 
		} catch(NumberFormatException e) { 
			return -1; 
		} catch(NullPointerException e) {
			return -1;
		}
	
		return ret;
	}

	public static long parseStringToLong(String s) {
		long ret = -1;
		int decimal_place = 6;
		
		if(s.contains("-")) {
			return -1;
		}
		
		s = s.replace(".", "");
		String tmp[] = s.split(",");

		if(tmp.length == 2) {
			decimal_place -= tmp[1].length();
		}
		
		s = s.replace(",", "");
		
		for(int i = 0; i < decimal_place; i++) {
			s += '0';
		}

		try { 
			ret = Long.parseLong(s); 
		} catch(NumberFormatException e) { 
			return -1; 
		} catch(NullPointerException e) {
			return -1;
		}
	
		return ret;
	}
	
	public static int extractYearFromString(String s) {
		int ret = -1;
		
		String tmp = null;
		if(s.length() < 4)
			return -1;
		
		tmp = s.substring(0, 4);
		
		try { 
			ret = Integer.parseInt(tmp); 
		} catch(NumberFormatException e) { 
			return -1; 
		} catch(NullPointerException e) {
			return -1;
		}
		
		return ret;
	}
	
}