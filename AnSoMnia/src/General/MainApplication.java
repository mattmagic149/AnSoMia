package General;

import java.util.LinkedHashMap;
import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.Scheduler; 
import org.quartz.SchedulerException; 
import org.quartz.SchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.quartz.JobBuilder.*; 
import static org.quartz.TriggerBuilder.*; 
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.DateBuilder.*;
import static org.quartz.JobKey.*;
import static org.quartz.impl.matchers.KeyMatcher.*;
import static org.quartz.impl.matchers.GroupMatcher.*;
import static org.quartz.impl.matchers.AndMatcher.*;
import static org.quartz.impl.matchers.OrMatcher.*;
import static org.quartz.impl.matchers.EverythingMatcher.*;

import org.quartz.*;

import Mining.FinanzenCrawler;
import Mining.WallStreetOnlineCrawler;

public class MainApplication {
	
	public static Lock mutex = new ReentrantLock(true);
	public static Map<String, ReentrantLock> isin_mutex_map = new LinkedHashMap<String, ReentrantLock>();

	public static SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
	public static Scheduler scheduler;
	
	public static JobDetail isin_mutex_map_creation_job;
	public static JobDetail finance_crawler_job;
	public static JobDetail wallstreet_crawler_job;

	
	public static void main(String[] args) throws SchedulerException {

	  scheduler = schedFact.getScheduler(); 
	  scheduler.start();
	  
	  isin_mutex_map_creation_job = newJob(IsinMutexMapCreator.class) 
	      .withIdentity("Isin Mutex Map Creator", "Database")
	      .storeDurably()
	      .build();
	  
	  wallstreet_crawler_job = newJob(WallStreetOnlineCrawler.class) 
			  .withIdentity("wallstreet-online.de Crawler", "KPICrawlers")
			  .storeDurably()
			  .build();
	  
	  finance_crawler_job = newJob(FinanzenCrawler.class) 
		  .withIdentity("finanzen.net Crawler", "KPICrawlers")
		  .storeDurably()
		  .build();

	  scheduler.addJob(isin_mutex_map_creation_job, false);
	  scheduler.addJob(wallstreet_crawler_job, false);
	  scheduler.addJob(finance_crawler_job, false);

	  IsinMutexMapListener isin_mutex_map_listener = new IsinMutexMapListener("myJobListener");
	  scheduler.getListenerManager().addJobListener(isin_mutex_map_listener, KeyMatcher.keyEquals(isin_mutex_map_creation_job.getKey()));

	  scheduler.triggerJob(isin_mutex_map_creation_job.getKey());
	  
	  /*
	   * 
	   */
	  /*Trigger daily_trigger_night = newTrigger()
			    .withIdentity("daily_trigger_night", "market_values_trigger")
			    .withSchedule(cronSchedule("0 14 19 ? * MON-FRI"))
			    .build();*/
	  
	  /*
	   * 
	   */
	  /*Trigger weekly_trigger = newTrigger()
			  .withIdentity("daily_trigger_morning", "weekly_trigger")
			    .withSchedule(cronSchedule("0 27 17 ? * SAT"))
			    .build();*/
	  
	  // Tell quartz to schedule the job using our trigger 
	  //scheduler.scheduleJob(job1, daily_trigger_morning);
	  //scheduler.scheduleJob(job2, daily_trigger_night);
	  
	  


	}

}


