package General;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
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
	
  public static void main( String[] args ) throws Exception
  {
    /*DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse( new File("test/google.html") );
    System.out.println( document.getFirstChild().getTextContent() );*/
	  

	  
	  DailyCrawler crawler = new DailyCrawler();
	  
	  List<Criterion>  criterions = new ArrayList<Criterion>();
	  List<SingleCompany> companies = HibernateSupport.readMoreObjects(SingleCompany.class, criterions);

	  int companies_size = companies.size();

	  for(int i = 0; i < companies_size; i++) {
		  try {
			  crawler.crawlKPIs(companies.get(i));
		  } catch(IOException e) {
			  System.out.println(e);
		  }
	  	  System.out.println("Crawled " + (companies_size + 1) + " companies");
		  Thread.sleep(300);
	  }
	  	 
  }
  
  private boolean crawlKPIs(SingleCompany company) throws IOException {
	  boolean success = false;
	  
	  if(company != null) {

		  this.company_ticker = company.getTicker();
		  
		  org.jsoup.nodes.Document doc = Jsoup.connect(this.url + this.query_page_q + this.company_ticker + this.query_result).get();
		  
		  Elements buy_price_query = doc.select("#yfi_quote_summary_data");
		  Element table = buy_price_query.get(0).child(0).child(0);

		  try {
			  this.crawlBuyPrice(table, company);
			  this.crawlSellPrice(table, company);
			  this.crawlStockPrice(doc, company);
		  } catch (NumberFormatException e) {
			  System.out.println(e);
		  }
	  
		  HibernateSupport.beginTransaction();
		  company.saveToDB();
		  HibernateSupport.commitTransaction();
		  
		  
	  }
	  
	  return success;
  }
  
  private boolean crawlBuyPrice(Element table, SingleCompany company) {	  
	  float buy_price = -1;
	  int table_size = table.childNodeSize();
	  for(int i = 0; i < table_size; i++) {
		  if(table.child(i).child(0).html().equals("Briefkurs:")) {
			  buy_price = Float.parseFloat(table.child(i).child(1).child(0).html().replace(',', '.'));
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
		  if(table.child(i).child(0).html().equals("Geld:")) {
			  sell_price = Float.parseFloat(table.child(i).child(1).child(0).html().replace(',', '.'));
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
	  float stock_price = Float.parseFloat(stock_price_query.get(0).child(0).html().replace(',', '.'));
  	
	  StockPrice stock_price_obj = new StockPrice(stock_price, company, this.date);
	  company.addStockPrice(stock_price_obj);
  }
  
}