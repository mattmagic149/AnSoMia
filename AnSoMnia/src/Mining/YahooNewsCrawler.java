package Mining;

import org.hibernate.criterion.Criterion;
import org.jsoup.*;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.sensium.ExtractionRequest;
import io.sensium.ExtractionResponse;
import io.sensium.Sensium;
import io.sensium.SensiumException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import DatabaseClasses.Company;
import General.*;
import Support.HibernateSupport;

public class YahooNewsCrawler extends Crawler implements Job
{
	private String yahoo_url = "http://finance.yahoo.com/q/h?s=";
	private Sensium sensium;
	private ExtractionRequest req;
	ExtractionResponse resp;
	
	public YahooNewsCrawler() {
		this.name = "yahoo_news_crawler";
		this.sensium = new Sensium("e16c27a8-e309-47aa-838d-cc2e6ffc5007");
		this.req = new ExtractionRequest();
		System.out.println("YahooNewsCrawler ctor called");
	}
	
	@Override	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println(MainApplication.isin_mutex_map.size());
		MainApplication.isin_mutex_map.clear();
		List<Criterion>  criterions = new ArrayList<Criterion>();
		List<Company> companies = HibernateSupport.readMoreObjects(Company.class, criterions);
		
		for(int i = 0; i < companies.size(); i++) {
			MainApplication.isin_mutex_map.put(companies.get(i).getIsin(), new ReentrantLock(true));
		}
		
		System.out.println(MainApplication.isin_mutex_map.size());
		
		YahooNewsCrawler fc = new YahooNewsCrawler();
		try {
			fc.startCrawling();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected boolean crawlInfos(Company company) {
		if(company == null || company.getTicker() == null || company.getTicker() == "") {
			System.out.println("Company is NULL...");
			return false;
		}

		
		Response response_abs;
		try {
			response_abs = Jsoup.connect(yahoo_url + company.getTicker() + "+Headlines").execute();
		} catch(SocketTimeoutException e) {
			return false;
		} catch(IOException e) {
			return false;
		}

		Element response;
		boolean success = true;
		
		if(response_abs.url().toString().equals(yahoo_url + company.getTicker() + "+Headlines")) {

			try {
				response = response_abs.parse();
			} catch (IOException e) {
				return false;
			}

			
		} else {
			success = false;
		}	

		return success;
		
	}
	
}
