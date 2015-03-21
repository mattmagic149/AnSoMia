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
	  
	  String url = "http://www.deutsche-boerse-cash-market.com/dbcm-de/instrumente-statistiken/"
	  		+ "alle-handelbaren-instrumente/handelsplaetze/1560090!search?"
	  		+ "state=H4sIAAAAAAAAADWNwWrCQBRFf0XuehYmlhbmA1wpBAxuxMU0udHBMbHvvaEE8d-bCrM8cDjniT4YtzLd4ceckntzOxUaQkdT-"
	  		+ "CfiqCb5ztHa-UH4E6q63tRfHzg7mIQ-fCfug9xoB17-veJUn2ucX0sritqOZpSSv0bThtKEy1Ks1m65dCn3PESjFmka09z0A_"
	  		+ "wQktLhJ1NmeMBBqDnZMfK3yDrJMoa20RJXQTu8_gABN2E35AAAAA&sort=sTitle+asc";
	  
	  String hits_per_page_query_string = "&hitsPerPage=" + 50;
	  String page_number_query_string = "&pageNum=" + 0;
	  
	  org.jsoup.nodes.Document doc = Jsoup.connect(url + hits_per_page_query_string + page_number_query_string).get();
	  Elements navigation = doc.select("#yfi_quote_summary_data tr");
	  
	  
	  
	  /*for(int i = 0; i < key_data.size(); i++) {
		  System.out.println(key_data.get(i));
	  }*/
  }
}