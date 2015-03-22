package General;
import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

import javax.xml.parsers.*;

import org.w3c.dom.Document;

public class IndexCrawler
{
  public static void main( String[] args ) throws Exception
  {
    /*DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse( new File("test/google.html") );
    System.out.println( document.getFirstChild().getTextContent() );*/
	  
	  String url = "https://de.finance.yahoo.com/";
	  String query_page_ks = "ks?s=";
	  String query_page_q = "q?s=";
	  String company_ticker = "RKET";
	  String query_result = "&ql=1";
	  
	  org.jsoup.nodes.Document doc = Jsoup.connect(url + query_page_q + company_ticker + query_result).get();
	  Elements stock_price_query = doc.select(".yfi_rt_quote_summary_rt_top span");
	  
	  float stock_price = Float.parseFloat(stock_price_query.get(0).child(0).html());
	  
	  /*for(int i = 0; i < key_data.size(); i++) {
		  System.out.println(key_data.get(i));
	  }*/
  }
  
}