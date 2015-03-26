package General;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.javatuples.Pair;
import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.*;

import org.w3c.dom.Document;

import KPIToCalc.*;
import KPIToCrawl.*;
import Support.HibernateSupport;

public class TestCrawler
{
	
  public static void main( String[] args ) throws Exception
  {
	 
	  org.jsoup.nodes.Document doc = Jsoup.connect("http://www.wallstreet-online.de/suche/?suche=&q=US40425X2099").get();
	  Elements finance_highlights = doc.select(".ui-widget");
	  
	 System.out.println(finance_highlights.get(0));
	  	  	 
  }
  
  
}