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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.hibernate.criterion.Criterion;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import utils.HibernateSupport;
import database.Company;

// TODO: Auto-generated Javadoc
/**
 * The Class IsinMutexMapCreator.
 */
public class IsinMutexMapCreator implements Job {

	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 * 
	 * Creates a map with isin and mutexes
	 * 
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println(MainApplication.getInstance().getIsinMutexMap().size());
		MainApplication.getInstance().getIsinMutexMap().clear();
		Map<String, ReentrantLock> isin_mutex_map = new LinkedHashMap<String, ReentrantLock>();

		List<Company> companies = HibernateSupport.readMoreObjects(Company.class, new ArrayList<Criterion>());
		
		for(int i = 0; i < companies.size(); i++) {
			isin_mutex_map.put(companies.get(i).getIsin(), new ReentrantLock(true));
		}
		
		MainApplication.getInstance().setIsinMutexMap(isin_mutex_map);
		
		System.out.println(isin_mutex_map.size());
	}
}
