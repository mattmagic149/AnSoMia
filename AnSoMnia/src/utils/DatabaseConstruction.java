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
package utils;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import database.*;

/**
 * This Class is for Constructing the Database.
 *
 */

public class DatabaseConstruction {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.out.println("Creating database-structure for AnSoMia");
		
		Configuration configuration = new Configuration();
		
		//add all classes you want to annotate
		configuration.addAnnotatedClass(Index.class);
		configuration.addAnnotatedClass(Company.class);
		configuration.addAnnotatedClass(MarketValue.class);
		configuration.addAnnotatedClass(KeyPerformanceIndicator.class);
		configuration.addAnnotatedClass(News.class);
		configuration.addAnnotatedClass(NewsDetail.class);
		configuration.addAnnotatedClass(SentenceInformation.class);
		configuration.addAnnotatedClass(IndustrySector.class);
		
		configuration.configure("hibernate.cfg.xml");

		new SchemaExport(configuration).create(true, true);
		
		SaveLoadDatabase sldb = new SaveLoadDatabase();
		sldb.loadDataBase();
		
		System.out.println("Finished");
	}

}
