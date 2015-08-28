package threadlisteners;

import general.MainApplication;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.SchedulerException;

public class CompanyIndexerListener implements JobListener {

	private String name;
	
	public CompanyIndexerListener(String name) {
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
			MainApplication.getInstance().getScheduler().triggerJob(MainApplication.getInstance().getIsinMutexMapCreationJob().getKey());
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
	}

}
