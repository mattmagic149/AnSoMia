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
package general;

import java.util.LinkedHashMap;
import java.util.Map;

import mining.*;

import org.quartz.JobDetail;
import org.quartz.Scheduler; 
import org.quartz.SchedulerException; 
import org.quartz.SchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;

import threadlisteners.*;
import java.util.concurrent.locks.ReentrantLock;
import static org.quartz.JobBuilder.*;

/**
 * The Class MainApplication.
 */
public class MainApplication {
	
	/** The Constant app. */
	private static final MainApplication app = new MainApplication();
	
	/** The Constant isin_mutex_map. */
	private Map<String, ReentrantLock> isin_mutex_map ;

	/** The sched_fact. */
	private SchedulerFactory sched_fact;
	
	/** The scheduler. */
	private Scheduler scheduler;
	
	/** The company_indexer_job. */
	private JobDetail company_indexer_job;
	
	/** The isin_mutex_map_creation_job. */
	private JobDetail isin_mutex_map_creation_job;
	
	/** The finance_crawler_job. */
	private JobDetail finance_crawler_job;
	
	/** The wallstreet_crawler_job. */
	private JobDetail wallstreet_crawler_job;
	
	/** The finance_news_crawler_job. */
	private JobDetail finance_news_crawler_job;
	
	/** The yahoo_news_crawler_job. */
	private JobDetail yahoo_news_crawler_job;
	
	/** The market_values_crawler_job. */
	private JobDetail market_values_crawler_job;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws SchedulerException the scheduler exception
	 */
	public static void main(String[] args) throws SchedulerException {
		System.out.println("Starting MainApplication...");
		System.out.println("Starting Scheduler...");

		app.sched_fact = new org.quartz.impl.StdSchedulerFactory();
		app.scheduler = app.sched_fact.getScheduler();
		app.setIsinMutexMap(new LinkedHashMap<String, ReentrantLock>());
		app.scheduler.start();
		
		System.out.println("Creating Jobs...");

		app.createJobs();
		System.out.println("Adding Jobs...");

		if(!app.addJobsToScheduler()) {
			System.out.println("Adding Jobs to Scheduler failed.");
			return;
		}
		
		System.out.println("Creating Listeners...");
	  
		CompanyIndexerListener company_index_listener = new CompanyIndexerListener("company_index_listener");
		app.scheduler.getListenerManager().addJobListener(company_index_listener, KeyMatcher.keyEquals(app.company_indexer_job.getKey()));

		IsinMutexMapListener isin_mutex_map_listener = new IsinMutexMapListener("isin_mutex_map_listener");
		app.scheduler.getListenerManager().addJobListener(isin_mutex_map_listener, KeyMatcher.keyEquals(app.isin_mutex_map_creation_job.getKey()));

		System.out.println("Creating Triggers...");

		/*Trigger daily_trigger = newTrigger()
			    .withIdentity("Daily Trigger", "Crawler Triggers")
			    .withSchedule(cronSchedule("0 25 17 ? * MON-FRI"))
			    .build();
		
		Trigger weekly_trigger = newTrigger()
				.withIdentity("Weekly Trigger", "Crawler Triggers")
				.withSchedule(cronSchedule("0 00 15 ? * SAT-SUN"))
				.build();
		
		app.scheduler.scheduleJob(app.market_values_crawler_job, daily_trigger);
		app.scheduler.scheduleJob(app.company_indexer_job, weekly_trigger);*/
		
		MainApplication.app.getMarketValuesCrawlerJob().getJobDataMap().put("to_crawl", MarketValuesCrawler.ToCrawl.MONTH.ordinal());
		//app.getScheduler().triggerJob(MainApplication.app.company_indexer_job.getKey());
		app.getScheduler().triggerJob(MainApplication.app.getIsinMutexMapCreationJob().getKey());
		//app.getScheduler().triggerJob(MainApplication.app.history_market_values_crawler_job.getKey());
		//app.getScheduler().triggerJob(MainApplication.app.market_values_crawler_job.getKey());



	}
	
	/**
	 * Adds the jobs to scheduler.
	 *
	 * @return true, if successful
	 */
	private boolean addJobsToScheduler() {
		try {
			app.scheduler.addJob(app.company_indexer_job, false);
			app.scheduler.addJob(app.isin_mutex_map_creation_job, false);
			app.scheduler.addJob(app.wallstreet_crawler_job, false);
			app.scheduler.addJob(app.finance_crawler_job, false);
			app.scheduler.addJob(app.finance_news_crawler_job, false);
			app.scheduler.addJob(app.yahoo_news_crawler_job, false);		
			app.scheduler.addJob(app.market_values_crawler_job, false);		
			//app.scheduler.addJob(app.market_values_crawler_job, false);

		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Creates the jobs.
	 */
	private void createJobs() {
		app.company_indexer_job = newJob(CompanyIndexIndustryCrawler.class) 
				.withIdentity("CompanyIndexer", "Indexer")
				.storeDurably()
				.build();
		  
		app.isin_mutex_map_creation_job = newJob(IsinMutexMapCreator.class) 
				.withIdentity("Isin Mutex Map Creator", "Database")
		      	.storeDurably()
		      	.build();
		  
		app.wallstreet_crawler_job = newJob(WallStreetOnlineCrawler.class) 
				.withIdentity("wallstreet-online.de Crawler", "KPICrawlers")
				.storeDurably()
				.build();
		  
		app.finance_crawler_job = newJob(FinanzenCrawler.class) 
				.withIdentity("finanzen.net Crawler", "KPICrawlers")
				.storeDurably()
				.build();
		
		app.finance_news_crawler_job = newJob(FinanzenNewsCrawler.class)
				.withIdentity("finanzen.net NewsCrawler", "DailyCrawlers")
				.storeDurably()
				.build();
		
		app.yahoo_news_crawler_job = newJob(YahooNewsCrawler.class)
				.withIdentity("finance.yahoo.com NewsCrawler", "DailyCrawlers")
				.storeDurably()
				.build();
		
		app.market_values_crawler_job = newJob(MarketValuesCrawler.class)
				.withIdentity("wallstreet-online.de history market values crawler", "Indexer")
				.storeDurably()
				.build();
	}

	/**
	 * Gets the scheduler.
	 *
	 * @return the scheduler
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * Gets the company indexer job.
	 *
	 * @return the company indexer job
	 */
	public JobDetail getCompanyIndexerJob() {
		return company_indexer_job;
	}

	/**
	 * Gets the isin mutex map creation job.
	 *
	 * @return the isin mutex map creation job
	 */
	public JobDetail getIsinMutexMapCreationJob() {
		return isin_mutex_map_creation_job;
	}

	/**
	 * Gets the finance crawler job.
	 *
	 * @return the finance crawler job
	 */
	public JobDetail getFinanceCrawlerJob() {
		return finance_crawler_job;
	}

	/**
	 * Gets the wallstreet crawler job.
	 *
	 * @return the wallstreet crawler job
	 */
	public JobDetail getWallstreetCrawlerJob() {
		return wallstreet_crawler_job;
	}
	
	/**
	 * Gets the finance news crawler job.
	 *
	 * @return the finance news crawler job
	 */
	public JobDetail getFinanceNewsCrawlerJob() {
		return finance_news_crawler_job;
	}
	
	/**
	 * Gets the yahoo news crawler job.
	 *
	 * @return the yahoo news crawler job
	 */
	public JobDetail getYahooNewsCrawlerJob() {
		return yahoo_news_crawler_job;
	}

	/**
	 * Gets the market values crawler job.
	 *
	 * @return the market values crawler job
	 */
	public JobDetail getMarketValuesCrawlerJob() {
		return market_values_crawler_job;
	}
	
	/**
	 * Gets the single instance of MainApplication.
	 *
	 * @return single instance of MainApplication
	 */
	public static MainApplication getInstance() { 
		return app; 
    }

	public Map<String, ReentrantLock> getIsinMutexMap() {
		return isin_mutex_map;
	}

	public void setIsinMutexMap(Map<String, ReentrantLock> isin_mutex_map) {
		this.isin_mutex_map = isin_mutex_map;
	}

}


