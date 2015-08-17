package Support;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import DatabaseClasses.Company;
import DatabaseClasses.CompanyNews;
import DatabaseClasses.Index;
import DatabaseClasses.IndustrySector;
import DatabaseClasses.KeyPerformanceIndicator;
import DatabaseClasses.MarketValue;
import General.*;

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
		configuration.addAnnotatedClass(Index.class);
		configuration.addAnnotatedClass(Company.class);
		configuration.addAnnotatedClass(MarketValue.class);
		configuration.addAnnotatedClass(KeyPerformanceIndicator.class);
		configuration.addAnnotatedClass(CompanyNews.class);
		configuration.addAnnotatedClass(IndustrySector.class);
		
		configuration.configure("hibernate.cfg.xml");

		new SchemaExport(configuration).create(true, true);
		
		SaveLoadDatabase sldb = new SaveLoadDatabase();
		sldb.loadDataBase();
		
		System.out.println("Finished");
	}

}
