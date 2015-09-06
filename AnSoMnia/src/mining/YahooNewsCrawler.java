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

import io.sensium.ExtractionResponse;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import utils.HttpRequestManager;
import utils.HttpRequester;
import database.Company;

// TODO: Auto-generated Javadoc
/**
 * The Class YahooNewsCrawler.
 */
public class YahooNewsCrawler extends Crawler implements Job
{
	
	/** The yahoo_url. */
	private String yahoo_url = "http://finance.yahoo.com/q/h?s=";
	//private Sensium sensium;
	/** The resp. */
	//private ExtractionRequest req;
	ExtractionResponse resp;
	
	/** The http_req_manager. */
	private HttpRequestManager http_req_manager;
	
	/**
	 * Instantiates a new yahoo news crawler.
	 */
	public YahooNewsCrawler() {
		http_req_manager = HttpRequestManager.getInstance();
		this.name = "yahoo_news_crawler";
		//this.sensium = new Sensium("e16c27a8-e309-47aa-838d-cc2e6ffc5007");
		//this.req = new ExtractionRequest();
		System.out.println("YahooNewsCrawler ctor called");
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
