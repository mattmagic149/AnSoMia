/*
 * @Author: Matthias Ivantsits
 * Supported by TU-Graz (KTI)
 * 
 * Tool, to gather market information, in quantitative and qualitative manner.
 * Copyright (C) 2015  Matthias Ivantsits
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mining;

import org.javatuples.Pair;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import utils.HibernateSupport;
import utils.HttpRequestManager;
import utils.HttpRequester;
import database.Company;
import database.KeyPerformanceIndicator;

/**
 * The Class FinanzenCrawler.
 */
public class FinanzenCrawler extends Crawler implements Job
{
	
	/** The finance_url. */
	private String finance_url = "http://www.finanzen.at";
	
	/** The profit_and_loss_string. */
	private String profit_and_loss_string = "/bilanz_guv/";
	
	/** The http_req_manager. */
	private HttpRequestManager http_req_manager;
	
	/**
	 * Instantiates a new finanzen crawler.
	 */
	public FinanzenCrawler() {
		this.name = "finance_crawler";
		this.http_req_manager = HttpRequestManager.getInstance();
		System.out.println("FinanzenCrawler ctor called");
	}
	
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		this.startCrawling();
	}
	
	/* (non-Javadoc)
	 * @see mining.Crawler#crawlInfos(database.Company)
	 */
	protected boolean crawlInfos(Company company) {
		if(company == null || company.getFinanceQueryString() == null || company.getFinanceQueryString() == "") {
			System.out.println("Company is NULL...");
			return false;
		}
		
		HttpRequester http_req = http_req_manager.getCorrespondingHttpRequester(finance_url);
		Element response = http_req.getHtmlContent(profit_and_loss_string + company.getFinanceQueryString());
		if(response == null) {
			return false;
		}
		
		Elements data_tables;
		Elements table_header;
		int year;
		boolean success = true;

		data_tables = response.select("#site .main .content_box").not(".depot_add").not(".state_info")
				.not(".state_success").not(".state_error").not(".infobox");
		if(data_tables.size() >= 1) {
			//System.out.println(data_tables);
			table_header = data_tables.last().select("table tr").select("th");
			year = 0;
			for(int i = 0; i < table_header.size(); i++) {
				year = this.extractYearFromString(table_header.get(i).html());
				if(year != -1) {
					Pair<Integer, Integer> year_pair = new Pair<Integer,Integer>(i, year);
					updateBalanceSheetValues(company, data_tables, year_pair);
				}

			}
			
		} else {
			success = false;
		}

		return success;
		
	}
	
	/**
	 * Update balance sheet values.
	 *
	 * @param company the company
	 * @param tables the tables
	 * @param year_pair the year_pair
	 * @return true, if successful
	 */
	private boolean updateBalanceSheetValues(Company company, Elements tables, Pair<Integer, Integer> year_pair) {
		
		boolean existing_indicators = true;
		KeyPerformanceIndicator indicators = company.getKpisCorrespondingToYear(year_pair.getValue1());
		
		if(indicators == null){
			indicators = new KeyPerformanceIndicator(year_pair.getValue1(), company);
			existing_indicators = false;
		}
		
		long revenue = Long.MIN_VALUE, operating_income = Long.MIN_VALUE, earnings_after_taxes = Long.MIN_VALUE,
				earnings_before_taxes = Long.MIN_VALUE, equity = Long.MIN_VALUE, debt = Long.MIN_VALUE, 
				balance_sheet_total = Long.MIN_VALUE, gross_profit = Long.MIN_VALUE, number_of_employees = Long.MIN_VALUE;
		float dividend = Float.MIN_VALUE, earnings_per_share = Float.MIN_VALUE;
		Elements tmp = null;
		String row_description = null;
		for(int i = 0; i < tables.size(); i++) {
			tmp = tables.get(i).select("tbody tr");
			for(int j = 0; j < tmp.size(); j++) {
				if(tmp.get(j).children().size() < 1) {
					continue;
				}
				row_description = tmp.get(j).child(0).html();

				if(row_description.contains("Umsatzerlöse")) {
					revenue = this.parseStringToLong(tmp.get(j).child(year_pair.getValue0()).html(), Decimals.MILLION);
					//System.out.println("revenue " + year_pair.getValue1() + " = " + revenue);
				} else if(row_description.contains("Anzahl Mitarbeiter") && !row_description.contains("Veränderung Anzahl Mitarbeiter")) {
					number_of_employees = this.parseStringToLong(tmp.get(j).child(year_pair.getValue0()).html(), Decimals.NONE);
					//System.out.println("number_of_employees " + year_pair.getValue1() + " = " + number_of_employees);
				} else if(row_description.contains("Operatives Ergebnis") && !row_description.contains("Veränderung Operatives Ergebnis")) {
					operating_income = this.parseStringToLong(tmp.get(j).child(year_pair.getValue0()).html(), Decimals.MILLION);
					//System.out.println("operating_income " + year_pair.getValue1() + " = " + operating_income);
				} else if(row_description.contains("Ergebnis vor Steuern") && !row_description.contains("Veränderung Ergebnis vor Steuern")) {
					earnings_before_taxes = this.parseStringToLong(tmp.get(j).child(year_pair.getValue0()).html(), Decimals.MILLION);
					//System.out.println("earnings_before_taxes " + year_pair.getValue1() + " = " + earnings_before_taxes);
				} else if(row_description.contains("Ergebnis nach Steuer") && !row_description.contains("Veränderung Ergebnis nach Steuer")) {
					earnings_after_taxes = this.parseStringToLong(tmp.get(j).child(year_pair.getValue0()).html(), Decimals.MILLION);
					//System.out.println("earnings_after_taxes " + year_pair.getValue1() + " = " + earnings_after_taxes);
				} else if(row_description.contains("Bilanzsumme") && !row_description.contains("Veränderung Bilanzsumme")) {
					balance_sheet_total = this.parseStringToLong(tmp.get(j).child(year_pair.getValue0()).html(), Decimals.MILLION);
					//System.out.println("balance_sheet_total " + year_pair.getValue1() + " = " + balance_sheet_total);
				} else if(row_description.contains("Eigenkapital") && !row_description.contains("Veränderung Eigenkapital")
						&& !row_description.contains("Eigenkapitalquote")) {
					equity = this.parseStringToLong(tmp.get(j).child(year_pair.getValue0()).html(), Decimals.MILLION);
					//System.out.println("equity " + year_pair.getValue1() + " = " + equity);
				} else if(row_description.contains("Dividende pro Aktie")) {
					dividend = this.parseFloat(tmp.get(j).child(year_pair.getValue0()).html(), Float.MIN_VALUE);
					//System.out.println("dividend " + year_pair.getValue1() + " = " + dividend);
				} else if(row_description.contains("Gewinn je Aktie (unverwässert)")) {
					earnings_per_share = this.parseFloat(tmp.get(j).child(year_pair.getValue0()).html(), Float.MIN_VALUE);
					//System.out.println("earnings_per_share " + year_pair.getValue1() + " = " + earnings_per_share);
				} else if(row_description.contains("Bruttoergebnis vom Umsatz")) {
					gross_profit = this.parseStringToLong(tmp.get(j).child(year_pair.getValue0()).html(), Decimals.MILLION);
					//System.out.println("gross_profit " + year_pair.getValue1() + " = " + gross_profit);
				}

			}
		}
		
		if(equity != Long.MIN_VALUE && balance_sheet_total != Long.MIN_VALUE) {
			debt = balance_sheet_total - equity;
			//System.out.println("debt " + year_pair.getValue1() + " = " + debt);
		}
		
		indicators.setFinanceValues(revenue, operating_income, earnings_after_taxes, earnings_before_taxes, equity,
		debt, balance_sheet_total, gross_profit, number_of_employees, dividend, earnings_per_share);
		
		if(!existing_indicators) {
			HibernateSupport.beginTransaction();
			company.addKPI(indicators);
			HibernateSupport.commitTransaction();
		}
		
		HibernateSupport.beginTransaction();
		indicators.saveToDB();
		HibernateSupport.commitTransaction();
		
		return true;
	}	
	
}
