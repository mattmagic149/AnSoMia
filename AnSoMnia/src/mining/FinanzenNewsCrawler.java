/*
 * @Author: Matthias Ivantsits
 * Supported by TU-Graz (KTI)
 * 
 * Tool, to gather market information, in quantitative and qualitative manner.
 * Copyright (C) 2015  Matthias Ivantsits
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mining;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.sensium.ExtractionRequest;
import io.sensium.ExtractionResponse;
import io.sensium.Sensium;
import io.sensium.SensiumException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import utils.HibernateSupport;
import utils.HttpRequestManager;
import utils.HttpRequester;
import database.*;

// TODO: Auto-generated Javadoc
/**
 * The Class FinanzenNewsCrawler.
 */
public class FinanzenNewsCrawler extends Crawler implements Job
{
	
	/** The finance_url. */
	private String finance_url = "http://www.finanzen.at";
	
	/** The sensium. */
	private Sensium sensium;
	
	/** The req. */
	private ExtractionRequest req;
	
	/** The resp. */
	private ExtractionResponse resp;
	
	/** The http_req_manager. */
	private HttpRequestManager http_req_manager;
	
	/** The date_added. */
	private Date date_added;
	
	/**
	 * Instantiates a new finanzen news crawler.
	 */
	public FinanzenNewsCrawler() {
		this.name = "finance_news_crawler";
		this.http_req_manager = HttpRequestManager.getInstance();
		this.sensium = new Sensium("e16c27a8-e309-47aa-838d-cc2e6ffc5007");
		this.req = new ExtractionRequest();
		this.date_added = new Date();
		System.out.println("FinanzenNewsCrawler ctor called");
	}
	
	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		this.startCrawling();
	}
	
	/* (non-Javadoc)
	 * @see mining.Crawler#crawlInfos(database.Company)
	 */
	protected boolean crawlInfos(Company company) {
		System.out.println("FinanzenNewsCrawler crawlInfos");

		if(company == null || company.getFinanceQueryString() == null || company.getFinanceQueryString() == "") {
			System.out.println("Company is NULL...");
			return false;
		}
		
		HttpRequester http_req = this.http_req_manager.getCorrespondingHttpRequester(finance_url);
		Element response = http_req.getHtmlContent("/aktien/" + company.getFinanceQueryString() + "-Aktie");

		if(response == null) {
			return false;
		}
		
		Elements news_tables_entries;
		Elements link_elements;
		ArrayList<String> links = new ArrayList<String>();
		ArrayList<String> dates = new ArrayList<String>();
		
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

		return true;
		
	}
	
	/**
	 * Crawl single news.
	 *
	 * @param company the company
	 * @param links the links
	 * @param dates the dates
	 */
	private void crawlSingleNews(Company company, ArrayList<String> links, ArrayList<String> dates) {
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("d.M.y");
		News news;
		Element response;
		Elements main_content;
		String content;
		String link;
		String language;
		long hash = 0;
		HttpRequester http_req;

		for(int i = 0; i < links.size(); i++) {
			System.out.println("Next news...");
			link = links.get(i);
			//System.out.println("link = " + link);
			
			if(company.checkUrlAlreadyAddedToNews(link)) {
				///TODO: log
				System.out.println("skipped these news, because url is already added.");
				continue;
			}
			
			
			System.out.println("First request...");
			http_req = this.http_req_manager.getCorrespondingHttpRequester(link);
			response = http_req.getHtmlContentWithCompleteUrl(link);
			if(response == null) {
				continue;
			}
			
			main_content = response.select(".news_text");
			if(main_content.toString().isEmpty()) {
				System.out.println("main_content is NULL");
				///TODO: log
				continue;
			}
			
			content = main_content.text();
			if(content.contains("Weiter zum vollständigen Artikel bei")) {
				link = main_content.select("a").last().attr("abs:href");
				
				if(company.checkUrlAlreadyAddedToNews(link)) {
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
			
			news = HibernateSupport.readOneObjectByID(News.class, hash);
			
			if(news == null && !company.checkNewsAlreadyAdded(hash)) {
				
				try {
					date = format.parse(dates.get(i));
				} catch (ParseException e) {
					//e.printStackTrace();
					///TODO: log
					continue;
				}
				
				news = new News(hash, link, "finanzen.net", 
								date, content, "translated_content", 
								language, this.date_added);
				
				HibernateSupport.beginTransaction();
				news.saveToDB();
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
