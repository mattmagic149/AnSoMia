package Mining;

import org.javatuples.Pair;
import org.jsoup.*;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Job;

import java.io.IOException;
import java.net.SocketTimeoutException;

import DatabaseClasses.Company;
import DatabaseClasses.KeyPerformanceIndicator;
import Support.HibernateSupport;

public class WallStreetOnlineCrawler extends Crawler implements Job
{
	private static String wall_street_url = "http://www.wallstreet-online.de";
	private static String share_string = "/aktien/";
	private static String balance_sheet_string = "/bilanz";
	private static String company_profile = "/unternehmensprofil";
	
	public WallStreetOnlineCrawler() {
		this.name = "wallstreet_crawler";
		System.out.println("WallStreetOnlineCrawler ctor called");
	}
	
	@Override	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		WallStreetOnlineCrawler wsc = new WallStreetOnlineCrawler();
		try {
			wsc.startCrawling();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected boolean crawlInfos(Company company) {
		if(company == null || company.getWallstreetQueryString() == null || company.getWallstreetQueryString() == "") {
			System.out.println("Company is NULL...");
			return false;
		}
		
		Response response_abs;
		try {
			response_abs = Jsoup.connect(wall_street_url + share_string + 
					company.getWallstreetQueryString() + balance_sheet_string).execute();
		} catch(SocketTimeoutException e) {
			return false;
		} catch(IOException e) {
			return false;
		}
			
		Element response;
		Elements data_tables;
		Elements table_header;
		int year;
		boolean success = true;
		
		if(response_abs.url().toString().equals(wall_street_url + share_string + 
				company.getWallstreetQueryString() + balance_sheet_string)) {
			

			try {
				response = response_abs.parse();
			} catch (IOException e) {
				return false;
			}
			data_tables = response.select("#main_content .t-data");
			if(data_tables.size() >= 1) {
				
				table_header = data_tables.first().children().first().children().first().children();
				
				year = 0;
				for(int i = 0; i < table_header.size(); i++) {
					year = extractYearFromString(table_header.get(i).html());
					if(year != -1) {
						//calendar.set(year, 0, 0);
						Pair<Integer, Integer> year_pair = new Pair<Integer,Integer>(i, year);
						updateBalanceSheetValues(company, data_tables, year_pair);

					}

				}
				
			} else {
				success = false;
			}
			
		} else {
			success = false;
		}

		
		
		try {
			response_abs = Jsoup.connect(wall_street_url + share_string + 
					company.getWallstreetQueryString() + company_profile).execute();
		} catch(SocketTimeoutException e) {
			return false;
		} catch(IOException e) {
			return false;
		}
		
		
		if(response_abs.url().toString().equals(wall_street_url + share_string + 
				company.getWallstreetQueryString() + company_profile)) {
			

			try {
				response = response_abs.parse();
			} catch (IOException e) {
				return false;
			}
			
			data_tables = response.select("#main_content .t-data");
			if(data_tables.size() >= 1) {
				
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
				
			} else {
				success = false;
			}
				
		} else {
			success = false;
		}
		
		company.updateKpisToCalculate();
		return success;
		
	}
	
	private boolean updateProfileKpis(Company company, Elements tables, Pair<Integer, Integer> year_pair) {
		
		boolean existing_indicators = true;
		KeyPerformanceIndicator indicators = company.getKpisCorrespondingToYear(year_pair.getValue1());
		
		if(indicators == null){
			indicators = new KeyPerformanceIndicator(year_pair.getValue1(), company);
			existing_indicators = false;
		}
		

		float dividend = Float.MIN_VALUE, equity_ratio = Float.MIN_VALUE, liquidity_1 = Float.MIN_VALUE,
				liquidity_2 = Float.MIN_VALUE, liquidity_3 = Float.MIN_VALUE;
		long working_capital = Long.MIN_VALUE, number_shares = Long.MIN_VALUE, number_employee = Long.MIN_VALUE;
		Elements tmp = null;
		String row_description = null;
		
		for(int i = 0; i < tables.size(); i++) {
			tmp = tables.get(i).select("tbody tr");
			for(int j = 0; j < tmp.size(); j++) {
				//System.out.println(tmp.get(j).child(0).html());
				row_description = tmp.get(j).child(0).html();

				if(row_description.contains("Umsatz je Aktie")) {
					dividend = parseFloat(tmp.get(j).child(year_pair.getValue0()).child(0).html(), Float.MIN_VALUE);
					//System.out.println("dividend " + year_pair.getValue1() + " = " + dividend);
				} else if(row_description.contains("Working Capital") && !row_description.contains("Partners")) {
					///TODO: what if it is NOT in Mio?!?!?!
					working_capital = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html(), DECIMALS.MILLION);
					//System.out.println("working_capital " + year_pair.getValue1() + " = " + working_capital);
				} else if(row_description.contains("Eigenkapitalquote")) {
					equity_ratio = parseFloat(tmp.get(j).child(year_pair.getValue0()).child(0).html(), Float.MIN_VALUE);
					//System.out.println("equity_ratio " + year_pair.getValue1() + " = " + equity_ratio);
				} else if(row_description.contains("Anzahl Mitarbeiter") && tmp.get(j).children().size() > year_pair.getValue0()) {
					number_employee = (long)parseFloat(tmp.get(j).child(year_pair.getValue0()).child(0).html(), Float.MIN_VALUE);
					//System.out.println("number_employee " + year_pair.getValue1() + " = " + number_employee);
				} else if(row_description.contains("‎Liquidität 1. Grades")) {
					liquidity_1 = parseFloat(tmp.get(j).child(year_pair.getValue0()).child(0).html(), Float.MIN_VALUE);
					//System.out.println("liquidity_1 " + year_pair.getValue1() + " = " + liquidity_1);
				} else if(row_description.contains("‎Liquidität 2. Grades")) {
					liquidity_2 = parseFloat(tmp.get(j).child(year_pair.getValue0()).child(0).html(), Float.MIN_VALUE);
					//System.out.println("liquidity_2 " + year_pair.getValue1() + " = " + liquidity_2);
				} else if(row_description.contains("‎Liquidität 3. Grades")) {
					liquidity_3 = parseFloat(tmp.get(j).child(year_pair.getValue0()).child(0).html(), Float.MIN_VALUE);
					//System.out.println("liquidity_3 " + year_pair.getValue1() + " = " + liquidity_3);
				} else if(row_description.contains("‎Aktien im Umlauf in Mio.")) {
					///TODO: what if it is not in Mio?!?!?!
					number_shares = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html(), DECIMALS.MILLION);
					//System.out.println("number_shares " + year_pair.getValue1() + " = " + number_shares);
				}

			}
		}
		
		indicators.setProfileValues(dividend, equity_ratio, liquidity_1, liquidity_2,
				liquidity_3, number_employee, number_shares, working_capital);
				
		
		if(!existing_indicators) {
			HibernateSupport.beginTransaction();
			company.addKPIs(indicators);
			HibernateSupport.commitTransaction();
		}
		
		HibernateSupport.beginTransaction();
		indicators.saveToDB();
		HibernateSupport.commitTransaction();
		
		return true;
	}
	
	private boolean updateBalanceSheetValues(Company company, Elements tables, Pair<Integer, Integer> year_pair) {
		
		boolean existing_indicators = true;
		KeyPerformanceIndicator indicators = company.getKpisCorrespondingToYear(year_pair.getValue1());
		
		if(indicators == null){
			indicators = new KeyPerformanceIndicator(year_pair.getValue1(), company);
			existing_indicators = false;
		}
		
		long revenue = Long.MIN_VALUE, operating_income = Long.MIN_VALUE, earings_after_taxes = Long.MIN_VALUE,
				cash_flow = Long.MIN_VALUE, equity = Long.MIN_VALUE, debt = Long.MIN_VALUE, 
				balance_sheet_total = Long.MIN_VALUE, gross_profit = Long.MIN_VALUE;
		Elements tmp = null;
		String row_description = null;
		for(int i = 0; i < tables.size(); i++) {
			tmp = tables.get(i).select("tbody tr");
			for(int j = 0; j < tmp.size(); j++) {
				//System.out.println(tmp.get(j));
				
				row_description = tmp.get(j).child(0).html();

				if(row_description.contains("Umsatz")) {
					revenue = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html(), DECIMALS.MILLION);
					//System.out.println("revenue " + year_pair.getValue1() + " = " + revenue);
				} else if(row_description.contains("EBIT")) {
					operating_income = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html(), DECIMALS.MILLION);
					//System.out.println("operating_income " + year_pair.getValue1() + " = " + operating_income);
				} else if(row_description.contains("Ergebnis nach Steuern")) {
					earings_after_taxes = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html(), DECIMALS.MILLION);
					//System.out.println("net_income " + year_pair.getValue1() + " = " + net_income);
				} else if(row_description.contains("Cashflow")) {
					cash_flow = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html(), DECIMALS.MILLION);
					//System.out.println("cash_flow " + year_pair.getValue1() + " = " + cash_flow);
				} else if(row_description.contains("Bilanzielles Eigenkapital")) {
					equity = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html(), DECIMALS.MILLION);
					//System.out.println("equity " + year_pair.getValue1() + " = " + equity);
				} else if(row_description.contains("Summe Fremdkapital")) {
					debt = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html(), DECIMALS.MILLION);
					//System.out.println("debt " + year_pair.getValue1() + " = " + debt);
				} else if(row_description.contains("Passiva")) {
					balance_sheet_total = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html(), DECIMALS.MILLION);
					//System.out.println("balance_sheet_total " + year_pair.getValue1() + " = " + balance_sheet_total);
				} else if(row_description.contains("Bruttoergebnis vom Umsatz")) {
					gross_profit = parseStringToLong(tmp.get(j).child(year_pair.getValue0()).child(0).html(), DECIMALS.MILLION);
					//System.out.println("gross_profit " + year_pair.getValue1() + " = " + gross_profit);
				}

			}
		}
		
		
		indicators.setBalanceSheetValues(revenue, operating_income, earings_after_taxes,
				cash_flow, equity, debt, balance_sheet_total, gross_profit);
		
				
		
		if(!existing_indicators) {
			HibernateSupport.beginTransaction();
			company.addKPIs(indicators);
			HibernateSupport.commitTransaction();
		}
		
		HibernateSupport.beginTransaction();
		indicators.saveToDB();
		HibernateSupport.commitTransaction();
		
		return true;
	}
	
}
