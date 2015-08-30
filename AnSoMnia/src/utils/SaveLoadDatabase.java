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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;

import database.*;

/**
 * The Class SaveLoadDatabase.
 */
public class SaveLoadDatabase {

	/** The company_string. */
	private String company_string = "company_backup";
	
	/** The kpi_string. */
	private String kpi_string = "kpi_backup";
	
	/** The index_string. */
	private String index_string = "index_backup";
	
	/** The index_to_company_string. */
	private String index_to_company_string = "index_to_company_backup";
	
	/** The market_values_string. */
	private String market_values_string = "market_values_backup";
	
	/** The industry_sectors_string. */
	private String industry_sectors_string = "industry_sector_backup";
	
	/** The industry_sector_to_company_string. */
	private String industry_sector_to_company_string = "industry_sector_to_company_backup";
	
	/** The news_string. */
	private String news_string = "news_backup";
	
	/** The news_details_string. */
	private String news_details_string = "news_details_backup";
	
	/** The sentence_information_string. */
	private String sentence_information_string = "sentence_information_backup";
	
	/** The news_to_company_string. */
	private String news_to_company_string = "news_to_company_backup";
	
	/** The file_extension. */
	private String file_extension = ".txt";
	
	/** The directory. */
	private String directory = "data/backups/";
	
	/** The writer. */
	private PrintWriter writer;
	
	/** The writer2. */
	private PrintWriter writer2;
	
	/** The writer3. */
	private PrintWriter writer3;
	
	/** The writer4. */
	private PrintWriter writer4;

	/**
	 * Instantiates a new save load database.
	 */
	public SaveLoadDatabase() {
		
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SaveLoadDatabase sldb = new SaveLoadDatabase();		
		//sldb.storeDatabase();
		sldb.loadDataBase();
	}
	
	/**
	 * Store database.
	 *
	 * @return true, if successful
	 */
	public boolean storeDatabase() {
		boolean ret = true;
		
		System.out.println("Starting to store database!");

		if(!this.storeCompanies()) {
			System.out.println("ERROR: Couldn't write companies to file!");
			ret = false;
		}
		
		if(!this.storeKPIs()) {
			System.out.println("ERROR: Couldn't write KPIs to file!");
			ret = false;
		}
		
		if(!this.storeIndexes()) {
			System.out.println("ERROR: Couldn't write Indexes to file!");
			ret = false;
		}
		
		if(!this.storeIndustrySectors()) {
			System.out.println("ERROR: Couldn't write industry sectors to file!");
			ret = false;
		}
		
		if(!this.storeCompanyNews()) {
			System.out.println("ERROR: Couldn't write industry sectors to file!");
			ret = false;
		}
		
		if(!this.storeMarketValues()) {
			System.out.println("ERROR: Couldn't write market values to file!");
			ret = false;
		}
		
		System.out.println("Finished storing database!");
		
		return ret;
	}
	
	/**
	 * Store company news.
	 *
	 * @return true, if successful
	 */
	private boolean storeCompanyNews() {
		System.out.println("Starting to store company news!");

		try {
			writer = new PrintWriter(directory + news_string + file_extension, "UTF-8");
			writer2 = new PrintWriter(directory + news_to_company_string + file_extension, "UTF-8");
			writer3 = new PrintWriter(directory + news_details_string + file_extension, "UTF-8");
			writer4 = new PrintWriter(directory + sentence_information_string + file_extension, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
		
		List<News> company_news = HibernateSupport.readMoreObjects(News.class, new ArrayList<Criterion>());
		List<Company> companies;
		News single_company_news;
		List<NewsDetail> details;
		NewsDetail detail;
		List<SentenceInformation> info;
		SentenceInformation single_info;
		
		for(int i = 0; i < company_news.size(); i++) {
			single_company_news = company_news.get(i);
			writer.println(single_company_news.serialize());
			companies = single_company_news.getCompanies();
			for(int j = 0; j < companies.size(); j++) {
				writer2.println(single_company_news.getHash() + "\t" + companies.get(j).getIsin());
			}
			
			details = single_company_news.getNewsDetails();
			for(int j = 0; j < details.size(); j++) {
				detail = details.get(j);
				writer3.println(detail.serialize());
				info = detail.getSentenceInformation();
				for(int k = 0; k < info.size(); k++) {
					single_info = info.get(k);
					writer4.println(single_info.serialize());
				}
			}
		}
		writer.close();
		writer2.close();
		writer3.close();
		writer4.close();
		System.out.println("Finished storing company news!");
		
		return true;
	}
	
	/**
	 * Store industry sectors.
	 *
	 * @return true, if successful
	 */
	private boolean storeIndustrySectors() {
		System.out.println("Starting to store industry sectors!");

		try {
			writer = new PrintWriter(directory + industry_sectors_string + file_extension, "UTF-8");
			writer2 = new PrintWriter(directory + industry_sector_to_company_string + file_extension, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
		
		List<IndustrySector> industry_sectors = HibernateSupport.readMoreObjects(IndustrySector.class, new ArrayList<Criterion>());
		List<Company> companies;
		IndustrySector industry_sector;
		
		for(int i = 0; i < industry_sectors.size(); i++) {
			industry_sector = industry_sectors.get(i);
			writer.println(industry_sector.serialize());
			companies = industry_sector.getCompanies();
			for(int j = 0; j < companies.size(); j++) {
				writer2.println(industry_sector.getName() + "\t" + companies.get(j).getIsin());
			}
		}
		writer.close();
		writer2.close();
		System.out.println("Finished storing industry sectors!");
		
		return true;
	}
	
	/**
	 * Store indexes.
	 *
	 * @return true, if successful
	 */
	private boolean storeIndexes() {
		System.out.println("Starting to store Indexes!");

		try {
			writer = new PrintWriter(directory + index_string + file_extension, "UTF-8");
			writer2 = new PrintWriter(directory + index_to_company_string + file_extension, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
		
		List<Index> indexes = HibernateSupport.readMoreObjects(Index.class, new ArrayList<Criterion>());
		List<Company> companies;
		Index index;
		
		for(int i = 0; i < indexes.size(); i++) {
			index = indexes.get(i);
			writer.println(index.serialize());
			companies = index.getCompanies();
			for(int j = 0; j < companies.size(); j++) {
				writer2.println(index.getIsin() + "\t" + companies.get(j).getIsin());
			}
		}
		writer.close();
		writer2.close();
		System.out.println("Finished storing Indexes!");
		
		return true;
	}
	
	/**
	 * Store kp is.
	 *
	 * @return true, if successful
	 */
	private boolean storeKPIs() {
		System.out.println("Starting to store KPIs!");

		try {
			writer = new PrintWriter(directory + kpi_string + file_extension, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
		
		List<KeyPerformanceIndicator> kpis = HibernateSupport.readMoreObjects(KeyPerformanceIndicator.class, new ArrayList<Criterion>());

		for(int i = 0; i < kpis.size(); i++) {
			writer.println(kpis.get(i).serialize());
		}
		writer.close();
		System.out.println("Finished storing KPIs!");
		
		return true;
	}
	
	/**
	 * Store companies.
	 *
	 * @return true, if successful
	 */
	private boolean storeCompanies() {
		System.out.println("Starting to store companies!");

		try {
			writer = new PrintWriter(directory + company_string + file_extension, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
		
		List<Company> companies = HibernateSupport.readMoreObjects(Company.class, new ArrayList<Criterion>());

		for(int i = 0; i < companies.size(); i++) {
			writer.println(companies.get(i).serialize());
		}
		writer.close();
		
		/*HibernateSupport.beginTransaction();
		HibernateSupport.getCurrentSession().createSQLQuery("select * from Company into outfile :file FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n'")
								.setString("file", directory + company_string + file_extension);

		HibernateSupport.commitTransaction();*/

		System.out.println("Finished storing companies!");
		
		return true;
	}
	
	/**
	 * Store market values.
	 *
	 * @return true, if successful
	 */
	private boolean storeMarketValues() {
		System.out.println("Starting to store market values!");

		try {
			writer = new PrintWriter(directory + market_values_string + file_extension, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
		
		HibernateSupport.beginTransaction();
		int market_values_size = ((Long)HibernateSupport.getCurrentSession()
									.createQuery("select count(*) from MarketValue")
									.uniqueResult()).intValue();
		HibernateSupport.commitTransaction();
		System.out.println(market_values_size);
		List<MarketValue> market_values = new ArrayList<MarketValue>();
		int increment_by = 30000;
		for(int offset = 0; offset < market_values_size; offset += increment_by) {
			market_values.clear();
			System.gc();
			market_values = HibernateSupport.readMoreObjects(MarketValue.class, new ArrayList<Criterion>(), offset, increment_by);
			System.out.println(offset + " market values, of " + market_values_size + " stored to file.");

			for(int i = 0; i < market_values.size(); i++) {
				writer.println(market_values.get(i).serialize());
			}
		}
		writer.close();
		System.out.println("Finished storing market values!");

		return true;
	}
	
	/**
	 * Load data base.
	 *
	 * @return true, if successful
	 */
	public boolean loadDataBase() {
		System.out.println("Starting to load database!");
		
		Map<String, Company> isin_company_map = loadCompanies();
		if(isin_company_map == null) {
			System.out.println("ERROR: Couldn't load companies!");
			return false;
		}
		
		if(!loadKPIs(isin_company_map)) {
			System.out.println("ERROR: Couldn't load KPIs!");
			return false;
		}
		
		if(!loadIndexes(isin_company_map)) {
			System.out.println("ERROR: Couldn't load Indexes!");
			return false;
		}
		
		if(!loadIndustrySectors(isin_company_map)) {
			System.out.println("ERROR: Couldn't load industry sectors!");
			return false;
		}
		
		if(!loadCompanyNews(isin_company_map)) {
			System.out.println("ERROR: Couldn't load news!");
			return false;
		}
		
		if(!loadMarketValues(isin_company_map)) {
			System.out.println("ERROR: Couldn't load market values!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Load company news.
	 *
	 * @param isin_company_map the isin_company_map
	 * @return true, if successful
	 */
	private boolean loadCompanyNews(Map<String, Company> isin_company_map) {
		System.out.println("Starting to load news.");

		FileReader file_reader;
		Map<Long, News> md5_news_map = new LinkedHashMap<Long, News>();

		News single_company_news;
		Company company;
		String[] tmp;
		try {
			file_reader = new FileReader(directory + news_string + file_extension);
			BufferedReader buffered_reader = new BufferedReader(file_reader);
			String line = null;
			
			FileReader file_reader2 = new FileReader(directory + news_details_string + file_extension);
			BufferedReader buffered_reader2 = new BufferedReader(file_reader2);
			FileReader file_reader3 = new FileReader(directory + sentence_information_string + file_extension);
			BufferedReader buffered_reader3 = new BufferedReader(file_reader3);
			
			ArrayList<String[]> details_buffer = new ArrayList<String[]>();
			ArrayList<String[]> info_buffer = new ArrayList<String[]>();
			
			while((line = buffered_reader2.readLine()) != null) {
				details_buffer.add(line.split("\t"));
			}
			
			while((line = buffered_reader3.readLine()) != null) {
				info_buffer.add(line.split("\t"));
			}

			HibernateSupport.beginTransaction();
			
			while ((line = buffered_reader.readLine()) != null) {
				single_company_news = new News(line);
				md5_news_map.put(single_company_news.getHash(), single_company_news);
				single_company_news.saveToDB();
				addDetails(single_company_news, details_buffer, info_buffer);
				
			}
			
			buffered_reader.close();
			buffered_reader2.close();
			buffered_reader3.close();
			
			file_reader = new FileReader(directory + news_to_company_string + file_extension);
			buffered_reader = new BufferedReader(file_reader);
			line = null;
			
			while ((line = buffered_reader.readLine()) != null) {
				tmp = line.split("\t");
				single_company_news = md5_news_map.get(Long.parseLong(tmp[0]));
				company = isin_company_map.get(tmp[1]);
				
				if(company != null && single_company_news != null) {
					company.addNews(single_company_news);
				}
			}
			
			buffered_reader.close();
			HibernateSupport.commitTransaction();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Finished loading news.");

		return true;
	}
	
	/**
	 * Adds the details.
	 *
	 * @param news the news
	 * @param details_buffer the details_buffer
	 * @param info_buffer the info_buffer
	 */
	private void addDetails(News news, ArrayList<String[]> details_buffer, ArrayList<String[]> info_buffer) {
		ArrayList<String[]> corresponding_details = addAndRemoveDetails(news.getHash(), details_buffer);
		String[] details_line;
		NewsDetail detail;
		for(int i = 0; i < corresponding_details.size(); i++) {
			details_line = corresponding_details.get(i);
			detail = new NewsDetail(details_line, news);
			addSentenceInformation(detail, info_buffer, Long.parseLong(details_line[5]));
			news.addCompanyNewsDetails(detail);
		}
	}
	
	/**
	 * Adds the sentence information.
	 *
	 * @param detail the detail
	 * @param info_buffer the info_buffer
	 * @param id the id
	 */
	private void addSentenceInformation(NewsDetail detail, ArrayList<String[]> info_buffer, long id) {
		ArrayList<String[]> corresponding_info = addAndRemoveInfo(id, info_buffer);
		String[] info_line;
		SentenceInformation info;
		for(int i = 0; i < corresponding_info.size(); i++) {
			info_line = corresponding_info.get(i);
			info = new SentenceInformation(info_line, detail);
			detail.addSentenceInformation(info);
		}
	}
	
	/**
	 * Adds the and remove details.
	 *
	 * @param hash the hash
	 * @param details_buffer the details_buffer
	 * @return the array list
	 */
	private ArrayList<String[]> addAndRemoveDetails(long hash, ArrayList<String[]> details_buffer) {
		ArrayList<String[]> result = new ArrayList<String[]>();
		String[] line = null;
		for(int i = 0; i < details_buffer.size(); i++) {
			line = details_buffer.get(i);
			if(Long.parseLong(line[0]) == hash) {
				result.add(line);
				details_buffer.remove(i--);
			}
		}
		return result;
	}
	
	/**
	 * Adds the and remove info.
	 *
	 * @param id the id
	 * @param info_buffer the info_buffer
	 * @return the array list
	 */
	private ArrayList<String[]> addAndRemoveInfo(long id, ArrayList<String[]> info_buffer) {
		ArrayList<String[]> result = new ArrayList<String[]>();
		String[] line = null;
		for(int i = 0; i < info_buffer.size(); i++) {
			line = info_buffer.get(i);
			if(Long.parseLong(line[0]) == id) {
				result.add(line);
				info_buffer.remove(i--);
			}
		}
		return result;
	}
	
	/**
	 * Load industry sectors.
	 *
	 * @param isin_company_map the isin_company_map
	 * @return true, if successful
	 */
	private boolean loadIndustrySectors(Map<String, Company> isin_company_map) {
		System.out.println("Starting to load industry sectors.");

		FileReader file_reader;
		Map<String, IndustrySector> isin_industry_map = new LinkedHashMap<String, IndustrySector>();

		IndustrySector industry_sector;
		Company company;
		String[] tmp;
		try {
			file_reader = new FileReader(directory + industry_sectors_string + file_extension);
			BufferedReader buffered_reader = new BufferedReader(file_reader);
			String line = null;
			HibernateSupport.beginTransaction();
			
			while ((line = buffered_reader.readLine()) != null) {
				industry_sector = new IndustrySector(line);
				isin_industry_map.put(industry_sector.getName(), industry_sector);
				industry_sector.saveToDB();
			}
			
			buffered_reader.close();
			
			file_reader = new FileReader(directory + this.industry_sector_to_company_string + file_extension);
			buffered_reader = new BufferedReader(file_reader);
			line = null;
			
			while ((line = buffered_reader.readLine()) != null) {
				tmp = line.split("\t");
				industry_sector = isin_industry_map.get(tmp[0]);
				company = isin_company_map.get(tmp[1]);
				
				if(industry_sector != null && company != null) {
					industry_sector.addCompany(company);
				}
			}
			
			buffered_reader.close();
			HibernateSupport.commitTransaction();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Finished loading industry sectors.");

		return true;
	}
	
	/**
	 * Load indexes.
	 *
	 * @param isin_company_map the isin_company_map
	 * @return true, if successful
	 */
	private boolean loadIndexes(Map<String, Company> isin_company_map) {
		System.out.println("Starting to load Indexes.");

		FileReader file_reader;
		Map<String, Index> isin_index_map = new LinkedHashMap<String, Index>();

		Index index;
		Company company;
		String[] tmp;
		try {
			file_reader = new FileReader(directory + index_string + file_extension);
			BufferedReader buffered_reader = new BufferedReader(file_reader);
			String line = null;
			HibernateSupport.beginTransaction();
			
			while ((line = buffered_reader.readLine()) != null) {
				index = new Index(line);
				isin_index_map.put(index.getIsin(), index);
				index.saveToDB();
			}
			
			buffered_reader.close();
			
			file_reader = new FileReader(directory + index_to_company_string + file_extension);
			buffered_reader = new BufferedReader(file_reader);
			line = null;
			
			while ((line = buffered_reader.readLine()) != null) {
				tmp = line.split("\t");
				index = isin_index_map.get(tmp[0]);
				company = isin_company_map.get(tmp[1]);
				if(company != null & index != null) {
					index.addCompany(company);
				}
			}
			
			buffered_reader.close();
			HibernateSupport.commitTransaction();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Finished loading Indexes.");

		return true;
	}
	
	/**
	 * Load kp is.
	 *
	 * @param isin_company_map the isin_company_map
	 * @return true, if successful
	 */
	private boolean loadKPIs(Map<String, Company> isin_company_map) {
		System.out.println("Starting to load KPIs.");

		FileReader file_reader;
		KeyPerformanceIndicator kpi;
		Company company;
		String[] tmp;
		try {
			file_reader = new FileReader(directory + kpi_string + file_extension);
			BufferedReader buffered_reader = new BufferedReader(file_reader);
			String line = null;
			HibernateSupport.beginTransaction();
			while ((line = buffered_reader.readLine()) != null) {
				tmp = line.split("\t");
				company = isin_company_map.get(tmp[0]);
				kpi = new KeyPerformanceIndicator(tmp, company);
				company.addKPI(kpi);
			}
			HibernateSupport.commitTransaction();
			buffered_reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Finished loading KPIs.");

		return true;
	}
	
	/**
	 * Load market values.
	 *
	 * @param isin_company_map the isin_company_map
	 * @return true, if successful
	 */
	private boolean loadMarketValues(Map<String, Company> isin_company_map) {
		System.out.println("Starting to load market values.");

		FileReader file_reader;
		MarketValue market_value;
		Company company;
		String[] tmp;
		try {
			file_reader = new FileReader(directory + market_values_string + file_extension);
			BufferedReader buffered_reader = new BufferedReader(file_reader);
			String line = null;
			int counter = 0;
			HibernateSupport.beginTransaction();
			while ((line = buffered_reader.readLine()) != null) {
				
				if((++counter) <= 8490000) {
					continue;
				}
				
				tmp = line.split("\t");
				
				company = isin_company_map.get(tmp[0]);
				market_value = new MarketValue(tmp, company);
				company.addMarketValueWithoutCheck(market_value);
				
				if((counter % 30000) == 0) {
					System.out.println(counter);
					HibernateSupport.commitTransaction();
					HibernateSupport.beginTransaction();
				}
				line = null;
				market_value = null;

			}
			HibernateSupport.commitTransaction();

			buffered_reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("Finished loading market values.");

		return true;
	}
	
	/**
	 * Load companies.
	 *
	 * @return the map
	 */
	private Map<String, Company> loadCompanies() {
		System.out.println("Starting to load companies.");
		FileReader file_reader;
		Map<String, Company> isin_company_map = new LinkedHashMap<String, Company>();

		Company company;
		try {
			file_reader = new FileReader(directory + company_string + file_extension);
			BufferedReader buffered_reader = new BufferedReader(file_reader);
			String line = null;
			HibernateSupport.beginTransaction();
			while ((line = buffered_reader.readLine()) != null) {
				company = new Company(line);
				isin_company_map.put(company.getIsin(), company);
				company.saveToDB();	
			}
			HibernateSupport.commitTransaction();
			buffered_reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		/*HibernateSupport.beginTransaction();
		HibernateSupport.getCurrentSession().createSQLQuery("LOAD DATA LOCAL INFILE :file INTO TABLE Company CHARACTER SET utf8 FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n';")
	    .setString("file", directory + company_string + file_extension)
	    .executeUpdate();
		HibernateSupport.commitTransaction();
		
		Map<String, Company> isin_company_map = new LinkedHashMap<String, Company>();
		List<Company> companies = HibernateSupport.readMoreObjects(Company.class, new ArrayList<Criterion>());

		for(int i = 0; i < companies.size(); i++) {
			isin_company_map.put(companies.get(i).getIsin(), companies.get(i));
		}*/
		
		System.out.println("Finished loading companies.");

		return isin_company_map;
	}

}
