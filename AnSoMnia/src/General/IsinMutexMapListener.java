package General;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.SchedulerException;

public class IsinMutexMapListener implements JobListener {

	private String name;
	
	public IsinMutexMapListener(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext arg0) {
		// TODO Auto-generated method stub
		System.out.println("this job is now executed...");
		
	}

	@Override
	public void jobWasExecuted(JobExecutionContext arg0,
			JobExecutionException arg1) {
		System.out.println("this job has been executed!!!!");
		
		try {
			MainApplication.scheduler.triggerJob(MainApplication.wallstreet_crawler_job.getKey());
			MainApplication.scheduler.triggerJob(MainApplication.finance_crawler_job.getKey());
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
