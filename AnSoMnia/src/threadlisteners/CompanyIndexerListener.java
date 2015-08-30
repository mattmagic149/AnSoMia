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
package threadlisteners;

import general.MainApplication;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.SchedulerException;

/**
 * The listener interface for receiving companyIndexer events.
 * The class that is interested in processing a companyIndexer
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addCompanyIndexerListener<code> method. When
 * the companyIndexer event occurs, that object's appropriate
 * method is invoked.
 *
 * @see CompanyIndexerEvent
 */
public class CompanyIndexerListener implements JobListener {

	/** The name. */
	private String name;
	
	/**
	 * Instantiates a new company indexer listener.
	 *
	 * @param name the name
	 */
	public CompanyIndexerListener(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see org.quartz.JobListener#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
	 */
	@Override
	public void jobExecutionVetoed(JobExecutionContext arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobListener#jobToBeExecuted(org.quartz.JobExecutionContext)
	 */
	@Override
	public void jobToBeExecuted(JobExecutionContext arg0) {
		// TODO Auto-generated method stub
		System.out.println("this job is now executed...");
		
	}

	/* (non-Javadoc)
	 * @see org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext, org.quartz.JobExecutionException)
	 */
	@Override
	public void jobWasExecuted(JobExecutionContext arg0, JobExecutionException arg1) {
		System.out.println("this job has been executed!!!!");
		
		try {
			MainApplication.getInstance().getScheduler()
						.triggerJob(MainApplication
									.getInstance()
									.getIsinMutexMapCreationJob()
									.getKey());
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
	}

}
