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

import org.javatuples.Pair;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.sensium.ExtractionRequest;
import io.sensium.ExtractionResponse;
import io.sensium.Sensium;
import io.sensium.SensiumException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import utils.HibernateSupport;
import utils.HttpRequestManager;
import utils.HttpRequester;
import database.*;

/**
 * The Class FinanzenNewsCrawler.
 */
public class WallStreetOnlineNewsCrawler extends Crawler implements Job
{
	
	/** The finance_url. */
	private String wall_street_url = "http://www.wallstreet-online.de";
	
	/** The sensium. */
	private Sensium sensium;
	
	/** The req. */
	private ExtractionRequest req;
	
	/** The resp. */
	private ExtractionResponse resp;
	
	/** The http_req_manager. */
	private HttpRequestManager http_req_manager;
	
	/**
	 * Instantiates a new finanzen news crawler.
	 */
	public WallStreetOnlineNewsCrawler() {
		this.name = "finance_news_crawler";
		this.http_req_manager = HttpRequestManager.getInstance();
		this.sensium = new Sensium("e16c27a8-e309-47aa-838d-cc2e6ffc5007");
		this.req = new ExtractionRequest();
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

		if(company == null || company.getWallstreetQueryString() == null || company.getWallstreetQueryString() == "") {
			System.out.println("Company is NULL...");
			return false;
		}
		
		ArrayList<Pair<String, Date>> links_plus_dates = new ArrayList<Pair<String, Date>>();
		ArrayList<Pair<String, Date>> tmp = new ArrayList<Pair<String, Date>>();

		
		for(int i = 1; i <= 5; i++) {
			tmp = getLinksFromOnePage(company, i);
			if(tmp != null) {
				links_plus_dates.addAll(tmp);
			}
		}

		System.out.println(links_plus_dates.size());
		
		crawlSingleNews(company, links_plus_dates);

		return true;
		
	}
	
	/**
	 * Crawl single news.
	 *
	 * @param company the company
	 * @param links_plus_dates the links_plus_dates
	 */
	private void crawlSingleNews(Company company, ArrayList<Pair<String, Date>> links_plus_dates) {
		Date date = new Date();
		News news;
		String content;
		String link;
		String language;
		long hash = 0;
		HttpRequester hr;
		Element response;

		for(int i = 0; i < links_plus_dates.size(); i++) {
			System.out.println("Next news...");
			link = links_plus_dates.get(i).getValue0();
			date = links_plus_dates.get(i).getValue1();
			//System.out.println("link = " + link);
			
			if(company.checkUrlAlreadyAddedToNews(link)) {
				///TODO: log
				System.out.println("skipped these news, because url is already added.");
				continue;
			}
			
			hr = this.http_req_manager.getCorrespondingHttpRequester(link);
			response = hr.getHtmlContentWithCompleteUrl(link);
			if(response == null) {
				System.out.println("response == null.");
				return;
			}
			
			try {
				
				if(response.select(".postingText").size() == 1) {
					System.out.println("using text from response.");
					this.req.text = response.select(".postingText").text();
					this.req.url = "";
				} else {
					System.out.println("using text from sensium.");
					this.req.text = "";
					this.req.url = link;
				}
				
				resp = sensium.extract(req);
				content = resp.text;
				language = resp.language;

			} catch (SensiumException e) {
				//e.printStackTrace();
				///TODO: log
				System.out.println("sensium.extract(req) threw an exception!!!");
				continue;
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
				news = new News(hash, link, "wallstreet-online.de", date, content, "translated_content", language);
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
	
	/**
	 * Gets the links from one page.
	 *
	 * @param company the company
	 * @param page_number the page_number
	 * @return the links from one page
	 */
	private ArrayList<Pair<String, Date>> getLinksFromOnePage(Company company, int page_number) {
		ArrayList<Pair<String, Date>> result = new ArrayList<Pair<String, Date>>();
		
		
		HttpRequester hr = http_req_manager.getCorrespondingHttpRequester(wall_street_url);
		
		Element response = hr.getHtmlContent("/aktien/" + 
												company.getWallstreetQueryString() + 
												"/nachrichten?page=" + 
												page_number);
		
		if(response == null) {
			System.out.println("NULL!!!!!");
			return null;
		}
				
		Elements t_datas = response.select(".module .t-data");
		if(t_datas.size() == 0) {
			return null;
		}
		int max = 0;
		int index = 0;
		Elements ankers;
		for(int i = 0; i < t_datas.size(); i++) {
			ankers = t_datas.get(i).select("a");
			if(ankers.size() > max) {
				max = ankers.size();
				index = i;
			}
		}
		
		SimpleDateFormat formater = new SimpleDateFormat("dd.MM.");
		
		Date reference_date = new Date();
		Calendar reference_calendar = Calendar.getInstance();
		reference_calendar.setTime(reference_date);
		
		reference_calendar.set(Calendar.HOUR, 0);
		reference_calendar.set(Calendar.MINUTE, 0);
		reference_calendar.set(Calendar.SECOND, 0);
		
		Calendar current_calendar = Calendar.getInstance();

		Elements rows = t_datas.get(index).select("tbody tr");
		if(rows.size() == 0) {
			return null;
		}
		String url;
		String date_string;
		
		for(int i = 0; i < rows.size(); i++) {
			url = rows.get(i).select("a").first().attr("abs:href");
			date_string = rows.get(i).select("span").html();
			try {
				current_calendar.setTime(formater.parse(date_string));
				current_calendar.set(Calendar.YEAR, reference_calendar.get(Calendar.YEAR));
				
				while(current_calendar.after(reference_calendar)) {
					current_calendar.set(Calendar.YEAR, current_calendar.get(Calendar.YEAR) - 1);
				}
				
				result.add(new Pair<String, Date>(url, current_calendar.getTime()));

			} catch (ParseException e) {
				continue;
			}
		}
		
		
		return result;
	}
	
}
