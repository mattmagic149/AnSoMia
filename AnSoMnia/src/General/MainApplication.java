package General;

import java.util.LinkedHashMap;
import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.Scheduler; 
import org.quartz.SchedulerException; 
import org.quartz.SchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;

import java.util.concurrent.locks.ReentrantLock;

import static org.quartz.JobBuilder.*;

import Mining.*;
import ThreadListener.*;

public class MainApplication {
	
	public static MainApplication app;
	public static Map<String, ReentrantLock> isin_mutex_map = new LinkedHashMap<String, ReentrantLock>();

	private SchedulerFactory sched_fact;
	private Scheduler scheduler;
	
	private JobDetail company_indexer_job;
	private JobDetail isin_mutex_map_creation_job;
	private JobDetail finance_crawler_job;
	private JobDetail wallstreet_crawler_job;
	private JobDetail market_values_crawler_job;
	private JobDetail finance_news_crawler_job;
	private JobDetail yahoo_news_crawler_job;
	private JobDetail history_market_values_crawler_job;

	public static void main(String[] args) throws SchedulerException {
		System.out.println("Starting MainApplication...");
		app = new MainApplication();
		System.out.println("Starting Scheduler...");

		app.sched_fact = new org.quartz.impl.StdSchedulerFactory();
		app.scheduler = app.sched_fact.getScheduler(); 
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
		
		//app.getScheduler().triggerJob(MainApplication.app.getFinanceNewsCrawlerJob().getKey());
		app.getScheduler().triggerJob(MainApplication.app.history_market_values_crawler_job.getKey());
		//app.getScheduler().triggerJob(MainApplication.app.market_values_crawler_job.getKey());


	}
	
	private boolean addJobsToScheduler() {
		try {
			//app.scheduler.addJob(app.company_indexer_job, false);
			app.scheduler.addJob(app.isin_mutex_map_creation_job, false);
			app.scheduler.addJob(app.wallstreet_crawler_job, false);
			app.scheduler.addJob(app.finance_crawler_job, false);
			app.scheduler.addJob(app.finance_news_crawler_job, false);
			app.scheduler.addJob(app.yahoo_news_crawler_job, false);		
			app.scheduler.addJob(app.history_market_values_crawler_job, false);		
			//app.scheduler.addJob(app.market_values_crawler_job, false);

		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
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
		
		app.market_values_crawler_job = newJob(MarketValuesCrawler.class) 
				.withIdentity("MarketValuesCrawler", "DailyCrawlers")
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
		
		app.history_market_values_crawler_job = newJob(HistoryMarketValuesCrawler.class)
				.withIdentity("wallstreet-online.de history market values crawler", "Indexer")
				.storeDurably()
				.build();
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public JobDetail getCompanyIndexerJob() {
		return company_indexer_job;
	}

	public JobDetail getIsinMutexMapCreationJob() {
		return isin_mutex_map_creation_job;
	}

	public JobDetail getFinanceCrawlerJob() {
		return finance_crawler_job;
	}

	public JobDetail getWallstreetCrawlerJob() {
		return wallstreet_crawler_job;
	}
	
	public JobDetail getMarketValuesCrawlerJob() {
		return market_values_crawler_job;
	}
	
	public JobDetail getFinanceNewsCrawlerJob() {
		return finance_news_crawler_job;
	}
	
	public JobDetail getYahooNewsCrawlerJob() {
		return yahoo_news_crawler_job;
	}

	public JobDetail getHistoryMarketValuesCrawlerJob() {
		return history_market_values_crawler_job;
	}

}


