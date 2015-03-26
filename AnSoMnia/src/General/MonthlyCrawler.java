package General;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.*;

import org.w3c.dom.Document;

import KPIToCrawl.*;
import Support.HibernateSupport;

public class MonthlyCrawler
{
	private String url = "https://de.finance.yahoo.com/";
	private String query_page_ks = "q/ks?s=";
	private String company_ticker = "";
	private Date date = new Date();
	private List<SingleCompany> company_not_crawled = new ArrayList<SingleCompany>();
	private static String[] business_year_kpis = {"Geschäftsjahresende", "Letztes Quartal"};
	private static String[] profit_ratio_kpis = {"Gewinnspanne", "Operative Marge"};
	private static String[] management_effectivity_kpis = {"Kapitalrentabilität", "Eigenkapitalrendite"};
	private static String[] income_statement_kpis = {"EBITDA", "Umsatz (ttm)", "Umsatz pro Aktie (ttm)", "Jahresüberschuss"};
	private static String[] financial_statement_kpis = {"Cash (gesamt)", "Gesamt-Cash", "Schulden (gesamt)", "Schulden/Equity"};
	private static String[] cash_flow_kpis = {"Cash Flow aus betrieblichen Tätigkeiten", "Levered Free Cash Flow"};
	
  public static void main( String[] args ) throws Exception
  {
	  MonthlyCrawler crawler = new MonthlyCrawler();
	  
	  /*List<Criterion>  criterions = new ArrayList<Criterion>();
	  List<SingleCompany> companies = HibernateSupport.readMoreObjects(SingleCompany.class, criterions);

	  int companies_size = companies.size();
	  int timeout_counter = 0;
	  boolean success = true;*/

	  String finance_highlights_query_string = ".yfnc_datamodoutline1";
	  crawler.company_ticker = "APC";	  
	  org.jsoup.nodes.Document doc = Jsoup.connect(crawler.url + crawler.query_page_ks + crawler.company_ticker + ".DE").get();
	  Elements finance_highlights = doc.select(finance_highlights_query_string);
	  
	  for(int i = 0; i < finance_highlights.size(); i++) {
		  findSubHeadlinesInTable(finance_highlights.get(i));
	  }
	  	  	 
  }
  
  private static void findSubHeadlinesInTable(Element element) {
	  Elements table_rows = element.select("tr tr");
	  
	  if(element.html().contains("Geschäftsjahr")) {
		  findKPIs(table_rows, business_year_kpis);
	  } else if(element.html().contains("Rentabilität")) {
		  findKPIs(table_rows, profit_ratio_kpis);
	  } else if(element.html().contains("Managementeffektivität")) {
		  findKPIs(table_rows, management_effectivity_kpis);
	  } else if(element.html().contains("GuV")) {
		  findKPIs(table_rows, income_statement_kpis);
	  } else if(element.html().contains("Bilanz")) {
		  findKPIs(table_rows, financial_statement_kpis);
	  } else if(element.html().contains("Cash Flow")) {
		  findKPIs(table_rows, cash_flow_kpis);
	  }
  }
  
  private static void findKPIs(Elements elements, String[] kpis_to_find) {
	  for(int i = 0; i < elements.size(); i++) {
		  for(int j = 0; j < kpis_to_find.length; j++) {
			  extractSpecificKPI(elements.get(i), kpis_to_find[j]);
		  }  
	  }
  }
  
  private static void extractSpecificKPI(Element element, String kpi_to_find) {
	  int length = kpi_to_find.length();
	  String spaces = "";
	  for(int i = 0; i < (50 - length); i++) {
		  spaces = spaces + " ";
	  }
	  
	  if(element.html().contains(kpi_to_find)) {
		  String ebitda_string = element.child(1).html();
		  System.out.println(kpi_to_find + spaces + ebitda_string);
	  }
  }
  
  private static long parseStringToLong(String param) throws ParseException {
	  String[] tmp_string = param.split(",");
	  int number_of_zeros = 0;
	  
	  if(tmp_string.length != 2) {
		  throw new ParseException("KPI can't be parsed", 0);
	  }
	  
	  if(tmp_string[1].contains("Mrd")) {
		  tmp_string[1] = tmp_string[1].replace("Mrd.", "");
		  number_of_zeros = 9 - tmp_string[1].length();
	  } else if(tmp_string[1].contains("Mio")) {
		  tmp_string[1] = tmp_string[1].replace("Mio.", "");
		  number_of_zeros = 6 - tmp_string[1].length();
	  } else if(tmp_string[1].contains("T")) {
		  tmp_string[1] = tmp_string[1].replace("T.", "");
		  number_of_zeros = 3 - tmp_string[1].length();
	  }
	  	  
	  return (long) (Long.parseLong(tmp_string[0] + tmp_string[1]) * Math.pow(10, number_of_zeros));
  }
  
}