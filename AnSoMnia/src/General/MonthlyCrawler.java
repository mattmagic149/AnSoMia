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
	
  public static void main( String[] args ) throws Exception
  {
	  MonthlyCrawler crawler = new MonthlyCrawler();
	  
	  /*List<Criterion>  criterions = new ArrayList<Criterion>();
	  List<SingleCompany> companies = HibernateSupport.readMoreObjects(SingleCompany.class, criterions);

	  int companies_size = companies.size();
	  int timeout_counter = 0;
	  boolean success = true;*/

	  String kpi_tables_query_string = ".yfnc_datamodoutline1";
	  crawler.company_ticker = "APC";	  
	  org.jsoup.nodes.Document doc = Jsoup.connect(crawler.url + crawler.query_page_ks + crawler.company_ticker + ".DE").get();
	  Elements kpi_tables = doc.select(kpi_tables_query_string);
	  
	  for(int i = 0; i < kpi_tables.size(); i++) {
		  findSubHeadlinesInTable(kpi_tables.get(i));
	  }
	  	  	 
  }
  
  private static void findSubHeadlinesInTable(Element element) {
	  Elements table_rows = element.select("tr tr");
	  
	  if(element.html().contains("Geschäftsjahr")) {
		  String[] kpis_to_find = {"Geschäftsjahresende", "Letztes Quartal"};
		  findKPIs(table_rows, kpis_to_find);
	  } else if(element.html().contains("Rentabilität")) {
		  String[] kpis_to_find = {"Gewinnspanne", "Operative Marge"};
		  findKPIs(table_rows, kpis_to_find);
	  } else if(element.html().contains("Managementeffektivität")) {
		  String[] kpis_to_find = {"Kapitalrentabilität", "Eigenkapitalrendite"};
		  findKPIs(table_rows, kpis_to_find);
	  } else if(element.html().contains("GuV")) {
		  String[] kpis_to_find = {"EBITDA", "Umsatz (ttm)", "Umsatz pro Aktie (ttm)", "Jahresüberschuss"};
		  findKPIs(table_rows, kpis_to_find);
	  } else if(element.html().contains("Bilanz")) {
		  String[] kpis_to_find = {"Cash (gesamt)", "Gesamt-Cash", "Schulden (gesamt)", "Schulden/Equity"};
		  findKPIs(table_rows, kpis_to_find);
	  } else if(element.html().contains("Cash Flow")) {
		  String[] kpis_to_find = {"Cash Flow aus betrieblichen Tätigkeiten", "Levered Free Cash Flow"};
		  findKPIs(table_rows, kpis_to_find);
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