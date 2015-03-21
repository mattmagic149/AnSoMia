package Support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import General.*;
import KeyPerformanceIndicators.*;


/**
 * This Class is for Constructing the Database
 * 
 * @author Senkl/Ivantsits
 *
 */

public class DatabaseConstruction {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Creating database-structure for AnSoMia");
		
		Configuration configuration = new Configuration();
		
		//add all classes you want to annotate
		configuration.addAnnotatedClass(SingleCompany.class);
		configuration.addAnnotatedClass(KeyPerformanceIndicator.class);
		
		configuration.addAnnotatedClass(PriceEarningsRatio.class);
		configuration.addAnnotatedClass(EarningsPerShare.class);
		configuration.addAnnotatedClass(PriceEarningsToGrowthRatio.class);
		
		configuration.configure("hibernate.cfg.xml");

		new SchemaExport(configuration).create(true, true);
		
		System.out.println("adding one SingleCompany");
		List<Criterion>  criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("isin", "DE0001218063"));
		SingleCompany company = HibernateSupport.readOneObject(SingleCompany.class, criterions);

		if(company == null){
			company = new SingleCompany("DE0001218063","FINLAB AG NA O.N.");
			PriceEarningsRatio per = new PriceEarningsRatio(company, 333.33, new Date());
			company.addPriceEarningsRatio(per);
			
			per = new PriceEarningsRatio(company, 222.22, new Date());
			company.addPriceEarningsRatio(per);
			
			
			EarningsPerShare eps = new EarningsPerShare(company, 333.33, new Date());
			company.addEarningsPerShare(eps);
			
			PriceEarningsToGrowthRatio peg = new PriceEarningsToGrowthRatio(company, 333.33, new Date());
			company.addPriceEarningsToGrowthRatio(peg);
			
			HibernateSupport.beginTransaction();
			company.saveToDB();
			HibernateSupport.commitTransaction();
		}
		
		System.out.println("Finished");
	}

}
