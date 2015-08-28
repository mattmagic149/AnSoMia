package mining;

import org.hibernate.criterion.Criterion;
import org.jsoup.nodes.Element;

import general.*;
import io.sensium.ExtractionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import utils.HibernateSupport;
import utils.HttpRequestManager;
import utils.HttpRequester;
import database.Company;

public class YahooNewsCrawler extends Crawler implements Job
{
	private String yahoo_url = "http://finance.yahoo.com/q/h?s=";
	//private Sensium sensium;
	//private ExtractionRequest req;
	ExtractionResponse resp;
	private HttpRequestManager http_req_manager;
	
	public YahooNewsCrawler() {
		http_req_manager = HttpRequestManager.getInstance();
		this.name = "yahoo_news_crawler";
		//this.sensium = new Sensium("e16c27a8-e309-47aa-838d-cc2e6ffc5007");
		//this.req = new ExtractionRequest();
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
		
		HttpRequester http_req = this.http_req_manager.getCorrespondingHttpRequester(yahoo_url);
		Element response = http_req.getHtmlContent(company.getTicker() + "+Headlines");
		if(response == null) {
			return false;
		}

		boolean success = true;
	

		return success;
		
	}
	
}
