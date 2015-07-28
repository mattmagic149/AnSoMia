package General;
import java.util.Date;
import java.util.TimeZone;

import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler; 
import org.quartz.SchedulerException; 
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory; 

import static org.quartz.JobBuilder.*; 
import static org.quartz.TriggerBuilder.*; 
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.DateBuilder.*;

import org.quartz.*;

public class MainApplication {

	public static void main(String[] args) throws SchedulerException {

	  SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory(); 
	  Scheduler sched = schedFact.getScheduler(); 
	  sched.start(); 
	  // define the job and tie it to our HelloJob class 
	  JobDetail job1 = newJob(TestJobA.class) 
	      .withIdentity("myJob1", "group1") 
	      .build(); 
	  
	  JobDetail job2 = newJob(TestJobA.class) 
		      .withIdentity("myJob2", "group1") 
		      .build();

	  
	  /*
	   * 
	   */
	  Trigger daily_trigger_morning = newTrigger()
			    .withIdentity("daily_trigger_morning", "market_values_trigger")
			    .withSchedule(cronSchedule("0 27 17 ? * MON-FRI"))
			    .build();
	  
	  /*
	   * 
	   */
	  Trigger daily_trigger_night = newTrigger()
			    .withIdentity("daily_trigger_night", "market_values_trigger")
			    .withSchedule(cronSchedule("0 28 17 ? * MON-FRI"))
			    .build();
	  
	  /*
	   * 
	   */
	  Trigger weekly_trigger = newTrigger()
			  .withIdentity("daily_trigger_morning", "weekly_trigger")
			    .withSchedule(cronSchedule("0 27 17 ? * SAT"))
			    .build();
	  
	  // Tell quartz to schedule the job using our trigger 
	  sched.scheduleJob(job1, daily_trigger_morning);
	  sched.scheduleJob(job2, daily_trigger_night);


	}

}


