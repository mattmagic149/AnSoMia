import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

import javax.xml.parsers.*;

import org.w3c.dom.Document;

public class DOMParty
{
  public static void main( String[] args ) throws Exception
  {
    /*DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse( new File("test/google.html") );
    System.out.println( document.getFirstChild().getTextContent() );*/
	  
	  org.jsoup.nodes.Document doc = Jsoup.connect("https://de.finance.yahoo.com/q?s=GOOGL.BA&ql=0").get();
	  Elements key_data = doc.select("#yfi_quote_summary_data tr");
	  
	  for(int i = 0; i < key_data.size(); i++) {
		  System.out.println(key_data.get(i));
	  }
  }
}