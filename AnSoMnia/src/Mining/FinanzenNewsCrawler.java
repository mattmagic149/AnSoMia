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
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import DatabaseClasses.CompanyNews;
import General.*;
import Support.HibernateSupport;

public class FinanzenNewsCrawler extends Crawler implements Job
{
	private String finance_url = "http://www.finanzen.at";
	private Sensium sensium;
	private ExtractionRequest req;
	ExtractionResponse resp;
	
	public FinanzenNewsCrawler() {
		this.name = "finance_news_crawler";
		this.sensium = new Sensium("e16c27a8-e309-47aa-838d-cc2e6ffc5007");
		this.req = new ExtractionRequest();
		System.out.println("FinanzenNewsCrawler ctor called");
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
		
		//SingleCompany company = HibernateSupport.readOneObjectByStringId(SingleCompany.class, "US46118H1041");
		//MainApplication.isin_mutex_map.put(company.getIsin(), new ReentrantLock(true));
		
		System.out.println(MainApplication.isin_mutex_map.size());
		
		
		
		FinanzenNewsCrawler fc = new FinanzenNewsCrawler();
		try {
			System.out.println("FinanzenNewsCrawler startCrawling");

			fc.startCrawling();
		} catch (Exception e) {
			//e.printStackTrace();
			///TODO: log
			System.out.println("WHY THE FUCK AM I HERE?!?!");
		}
	}
	
	protected boolean crawlInfos(Company company) {
		System.out.println("FinanzenNewsCrawler crawlInfos");

		if(company == null || company.getFinanceQueryString() == null || company.getFinanceQueryString() == "") {
			System.out.println("Company is NULL...");
			return false;
		}
		
		Response response_abs;
		try {
			response_abs = Jsoup.connect(finance_url + "/aktien/" + 
					company.getFinanceQueryString() + "-Aktie").execute();
		} catch(SocketTimeoutException e) {
			return false;
		} catch(IOException e) {
			return false;
		}

		Element response;
		Elements news_tables_entries;
		Elements link_elements;
		boolean success = true;
		ArrayList<String> links = new ArrayList<String>();
		ArrayList<String> dates = new ArrayList<String>();
		
		if(response_abs.url().toString().equals(finance_url + "/aktien/" + 
				company.getFinanceQueryString() + "-Aktie")) {

			try {
				response = response_abs.parse();
			} catch (IOException e) {
				return false;
			}
			news_tables_entries = response.select("#detail-news-table tbody tr");
			link_elements = news_tables_entries.select("a");

			for(int i = 0; i < link_elements.size(); i++) {
				if(link_elements.get(i).attr("abs:href").contains(finance_url + "/nachrichten")) {
					//System.out.println(link_elements.get(i).attr("abs:href"));
					links.add(link_elements.get(i).attr("abs:href"));
					dates.add((link_elements.get(i).parent().siblingElements().first().html()));
				}
			}
			
			crawlSingleNews(company, links, dates);

			
		} else {
			success = false;
		}	

		return success;
		
	}
	
	private void crawlSingleNews(Company company, ArrayList<String> links, ArrayList<String> dates) {
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("d.M.y");
		CompanyNews news;
		Response response_abs;
		Element response;
		Elements main_content;
		String content;
		String link;
		String language;
		long hash = 0;

		for(int i = 0; i < links.size(); i++) {
			System.out.println("Next news...");
			link = links.get(i);
			//System.out.println("link = " + link);
			
			if(company.checkUrlAlreadyAdded(link)) {
				///TODO: log
				System.out.println("skipped these news, because url is already added.");
				continue;
			}
			
			System.out.println("First request...");
			try {
				response_abs = Jsoup.connect(link).execute();
				response = response_abs.parse();
			} catch(IOException e) {
				System.out.println("request/parsing exception!");
				///TODO: log
				//e.printStackTrace();
				continue;
			}

			main_content = response.select(".news_text");
			if(main_content.toString().isEmpty()) {
				System.out.println("main_content is NULL");
				///TODO: log
				continue;
			}
			
			content = main_content.html();
			if(main_content.html().contains("Weiter zum vollständigen Artikel bei")) {
				link = main_content.select("a").last().attr("abs:href");
				
				if(company.checkUrlAlreadyAdded(link)) {
					///TODO: log
					System.out.println("skipped these news, because url is already added.");
					continue;
				} else {
					//System.out.println("link = " + link);
				}
				
				System.out.println("Second request...");

				try {
					this.req.url = link;
					resp = sensium.extract(req);
					content = resp.text;
					language = resp.language;
				} catch (SensiumException e) {
					//e.printStackTrace();
					///TODO: log
					System.out.println("sensium.extract(req) threw an exception!!!");
					System.out.println("url: " + link);
					continue;
				}
			} else {
				try {
					this.req.url = link;
					resp = sensium.extract(req);
					content = resp.text;
					language = resp.language;

				} catch (SensiumException e) {
					//e.printStackTrace();
					///TODO: log
					System.out.println("sensium.extract(req) threw an exception!!!");
					continue;
				}
			}
			
			if(content.length() < 100) {
				System.out.println("skipped this news, because length is smaller than 100.");
			}
						
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				//e.printStackTrace();
				///TODO: log
				System.out.println("MD5 - NoSuchAlgorithm!!!");
				assert(false);
			}
							
			byte[] data = content.getBytes(); 
			md.update(data,0,data.length);
			hash = new BigInteger(1,md.digest()).longValue();
			
			news = HibernateSupport.readOneObjectByID(CompanyNews.class, hash);
			
			if(news == null && !company.checkNewsAlreadyAdded(hash)) {
				
				try {
					date = format.parse(dates.get(i));
				} catch (ParseException e) {
					//e.printStackTrace();
					///TODO: log
					continue;
				}
				
				news = new CompanyNews(hash, link, "finanzen.net", date, content, "translated_content", language);
				HibernateSupport.beginTransaction();
				company.addNews(news);
				System.out.println("news don't exist.");
				HibernateSupport.commitTransaction();
			} else if(news != null && !company.checkNewsAlreadyAdded(hash)) {
				HibernateSupport.beginTransaction();
				company.addNews(news);
				HibernateSupport.commitTransaction();
				System.out.println("news exist, but NOT within this company.");
			} else {
				System.out.println(hash);
				System.out.println("news exist within this company.");
			}
			
		}
	}
	
}
