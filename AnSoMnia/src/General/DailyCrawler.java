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

public class DailyCrawler
{
	private String url = "https://de.finance.yahoo.com/";
	private String query_page_ks = "ks?s=";
	private String query_page_q = "q?s=";
	private String company_ticker = "";
	private String query_result = "&ql=1";
	private Date date = new Date();
	private List<SingleCompany> company_not_crawled = new ArrayList<SingleCompany>();
	
  public static void main( String[] args ) throws Exception
  {
	  DailyCrawler crawler = new DailyCrawler();
	  
	  List<Criterion>  criterions = new ArrayList<Criterion>();
	  List<SingleCompany> companies = HibernateSupport.readMoreObjects(SingleCompany.class, criterions);

	  int companies_size = companies.size();
	  int timeout_counter = 0;
	  boolean success = true;

	  for(int i = 0; i < companies_size; i++) {
		  System.out.println("Crawling Company: " + companies.get(i).getCompanyName() + ", "
				  + companies.get(i).getIsin() + ", " + companies.get(i).getTicker());
		  try {
			  crawler.crawlKPIs(companies.get(i));
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
			  crawler.company_not_crawled.add(companies.get(i));
		  }
		  
		  if(success == true) {
			  timeout_counter = 0;
		  }
		  success = true;
	  	  System.out.print("Crawled ");
	  	  System.out.printf("%.2f", ((i + 1)/(float)companies_size) * 100);
	  	  System.out.println(" % - " + (crawler.company_not_crawled.size() + 1) + " not crawled");
		  //Thread.sleep(100);
	  }
	  
	  for(int i = 0; i < crawler.company_not_crawled.size(); i++) {
		  System.out.println("COMPANY: " + crawler.company_not_crawled.get(i).getCompanyName() + " " + 
				  			crawler.company_not_crawled.get(i).getIsin() + " " +
				  			crawler.company_not_crawled.get(i).getTicker());
	  }
	  	 
  }
  
  private boolean crawlKPIs(SingleCompany company) throws IOException {	  
	  if(company != null) {

		  this.company_ticker = company.getTicker();
		  org.jsoup.nodes.Document doc = Jsoup.connect(this.url + this.query_page_q + this.company_ticker + this.query_result).get();
		  
		  Elements buy_price_query = doc.select("#yfi_quote_summary_data");
		  if(buy_price_query.size() <= 0) {
			  this.company_not_crawled.add(company);
			  return false;
		  }
		  Element table = buy_price_query.get(0).child(0).child(0);

		  try {
			  this.crawlBuyPrice(table, company);
		  } catch (NumberFormatException e) {
			  System.out.println(e);
		  }
		  try {
			  this.crawlSellPrice(table, company);
		  } catch (NumberFormatException e) {
			  System.out.println(e);
		  }
		  try {
			  this.crawlStockPrice(doc, company);
		  } catch (NumberFormatException e) {
			  System.out.println(e);
		  }
		 
	  
		  HibernateSupport.beginTransaction();
		  company.saveToDB();
		  HibernateSupport.commitTransaction();
		  
		  
	  }
	  
	  return true;
  }
  
  private boolean crawlBuyPrice(Element table, SingleCompany company) {	  
	  float buy_price = -1;
	  int table_size = table.childNodeSize();
	  for(int i = 0; i < table_size; i++) {
		  if(table.child(i).child(0).html().equals("Briefkurs:") && 
				  !table.child(i).child(1).html().equals("n.v.")) {
			  buy_price = Float.parseFloat(table.child(i).child(1).child(0).html().replace(".", "").replace(',', '.'));
		  }
	  }

	  if(buy_price > 0) {
		  BuyPrice buy_price_obj = new BuyPrice(buy_price, company, this.date);
		  company.addBuyPrice(buy_price_obj);
		  return true;
	  } else {
		  return false;
	  }
  }
  
  private boolean crawlSellPrice(Element table, SingleCompany company) {
	  float sell_price = -1;
	  int table_size = table.childNodeSize();
	  for(int i = 0; i < table_size; i++) {
		  if(table.child(i).child(0).html().equals("Geld:") && 
				  !table.child(i).child(1).html().equals("n.v.")) {
			  sell_price = Float.parseFloat(table.child(i).child(1).child(0).html().replace(".", "").replace(',', '.'));
		  }
	  }

	  if(sell_price > 0) {
		  SellPrice buy_price_obj = new SellPrice(sell_price, company, this.date);
		  company.addSellPrice(buy_price_obj);
		  return true;
	  } else {
		  return false;
	  }
  }
  
  private void crawlStockPrice(org.jsoup.nodes.Document doc, SingleCompany company) {
	  Elements stock_price_query = doc.select(".yfi_rt_quote_summary_rt_top span");
	  float stock_price = Float.parseFloat(stock_price_query.get(0).child(0).html().replace(".", "").replace(',', '.'));
  	
	  StockPrice stock_price_obj = new StockPrice(stock_price, company, this.date);
	  company.addStockPrice(stock_price_obj);
  }
  
}