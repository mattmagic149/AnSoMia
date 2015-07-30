package General;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.criterion.Criterion;

import Support.HibernateSupport;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class IsinMutexMapCreator implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println(MainApplication.isin_mutex_map.size());
		MainApplication.isin_mutex_map.clear();
		List<Criterion>  criterions = new ArrayList<Criterion>();
		List<SingleCompany> companies = HibernateSupport.readMoreObjects(SingleCompany.class, criterions);
		
		for(int i = 0; i < companies.size(); i++) {
			MainApplication.isin_mutex_map.put(companies.get(i).getIsin(), new ReentrantLock(true));
		}
		
		System.out.println(MainApplication.isin_mutex_map.size());
	}
}
