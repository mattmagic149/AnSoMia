package General;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
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
	private String query_result = "&ql=1";
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
	  crawler.company_ticker = "HAL";
	  
	  System.out.println(crawler.url + crawler.query_page_ks + crawler.company_ticker + ".DE");
	  org.jsoup.nodes.Document doc = Jsoup.connect(crawler.url + crawler.query_page_ks + crawler.company_ticker + ".DE").get();
	  Elements kpi_tables = doc.select(kpi_tables_query_string);
	  
	  for(int i = 0; i < kpi_tables.size(); i++) {
		  
		  if(kpi_tables.get(i).html().contains("GuV")) {
			  System.out.println("Table-index " + i + " contains GuV");
			  Elements table_rows = kpi_tables.get(i).select("tr tr");//.getElementsByTag("tr");
			  System.out.println("It has " + table_rows.size() + " rows");
			  
			  for(int j = 0; j < table_rows.size(); j++) {
				  if(table_rows.get(j).html().contains("EBITDA")) {
					  System.out.println("Row-index " + j + " contains EBITDA");
					  //System.out.println(table_rows.get(j).html());
					  String ebitda_string = table_rows.get(j).child(1).html();
					  System.out.println(ebitda_string.replace("Mrd.", "0000000").replace(",", ""));
				  }
			  }
			  
		  }
	  }
	  	  	 
  }
  
}