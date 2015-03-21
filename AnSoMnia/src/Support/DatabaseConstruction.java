package Support;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import General.*;


/**
 * This Class is for Constructing the Database
 * 
 * @author Stettinger
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
		configuration.configure("hibernate.cfg.xml");

		new SchemaExport(configuration).create(true, true);
		
		System.out.println("adding one SingleCompany");
		List<Criterion>  criterions = new ArrayList<Criterion>();
		/*criterions.add(Restrictions.eq("firstname", "user"));
		criterions.add(Restrictions.eq("surname", "user"));*/
		SingleCompany company = HibernateSupport.readOneObject(SingleCompany.class, criterions);

		if(company == null){
			//company = new company("user","user","user@gmail.net","0123456");
			HibernateSupport.beginTransaction();
			company.saveToDB();
			HibernateSupport.commitTransaction();
		}
		
		System.out.println("Finished");
	}

}
