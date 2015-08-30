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
package mining;

import general.MainApplication;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import utils.HibernateSupport;
import database.Company;

/**
 * The Class Crawler.
 */
public abstract class Crawler {

	/** The companies_not_crawled. */
	protected List<Company> companies_not_crawled;
	
	/**
	 * The Enum Decimals.
	 */
	protected enum Decimals {
    	NONE, 
    	THOUSAND, 
    	MILLION, 
    	BILLION 
	}
	
	/** The name. */
	protected String name;
	
	/** The logger. */
	protected Logger logger; 
	
	/** The fh. */
	protected FileHandler fh;
	
	/** The reconnection_attempts. */
	protected int reconnection_attempts;
	
	/**
	 * Instantiates a new crawler.
	 */
	public Crawler() {
		companies_not_crawled = new ArrayList<Company>();
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
		this.reconnection_attempts = 1;
	}
	
	/**
	 * Start crawling.
	 *
	 * @throws Exception the exception
	 */
	protected void startCrawling()
	{
		
		logger = Logger.getLogger("MyLogger");
		logger.setUseParentHandlers(false);
	  
		try {
			// This block configure the logger with handler and formatter
			String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		  	fh = new FileHandler("data/tmp/" + this.name + "-"+ date + ".log", true);  
		  	logger.addHandler(fh);
		  	SimpleFormatter formatter = new SimpleFormatter();  
		  	fh.setFormatter(formatter);
		} catch (SecurityException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
		
	    ArrayList<String> isin_list = new ArrayList<String>();
	    
	    for (String key : MainApplication.getInstance().getIsinMutexMap().keySet()) {
	        isin_list.add(key);
	    }
	    
	    Collections.shuffle(isin_list);
	    String isin;
		Lock mutex;
		Company company;
		int companies_size = isin_list.size();
		
	    while(isin_list.size() > 0) {
	    	isin = isin_list.get(0);
	    	isin_list.remove(0);
	    	
	    	mutex = MainApplication.getInstance().getIsinMutexMap().get(isin);
	    	mutex.lock();
	    	
	    		company = HibernateSupport.readOneObjectByStringId(Company.class, isin);
	    		System.out.println("Crawling Company: " + company.getName() + ", "
						  + company.getIsin() + ", " + company.getTicker());
	    		
	    		for(int i = 0; i < 3; i++) {
	    			if(this.crawlInfos(company)) {
	    				break;
	    			} else if(i == (2)) {
						companies_not_crawled.add(company);
						this.logger.info("Company " + company.getIsin() + " not crawled from wallstreet-online.de");
	    			}
	    		}
	    		
			  	System.out.print("Crawled ");
			  	System.out.printf("%.2f", ((companies_size - isin_list.size())/(float)companies_size) * 100);
			  	System.out.println(" % - " + (companies_not_crawled.size()) + " not crawled");	    	
	    	mutex.unlock();
	    	
	    }
		
		for(int i = 0; i < companies_not_crawled.size(); i++) {
			System.out.println("COMPANY: " + companies_not_crawled.get(i).getName() + " " + 
					  			companies_not_crawled.get(i).getIsin() + " " +
					  			companies_not_crawled.get(i).getTicker());
		}
		
		System.out.println((companies_not_crawled.size()) + " not crawled");
		
	  	  	 
	}
	
	/**
	 * Crawl infos.
	 *
	 * @param company the company
	 * @return true, if successful
	 */
	protected boolean crawlInfos(Company company) {return false;}
	
	/**
	 * Parses the float.
	 *
	 * @param s the s
	 * @param return_on_error the return_on_error
	 * @return the float
	 */
	protected float parseFloat(String s, float return_on_error) {
		float ret = return_on_error;
		String tmp = s.split(" ")[0];
		tmp = tmp.replace(".", "");
		tmp = tmp.replace(",", ".");

		try { 
			ret = Float.parseFloat(tmp); 
		} catch(NumberFormatException e) { 
			return return_on_error;
		} catch(NullPointerException e) {
			return return_on_error;
		}
	
		return ret;
	}

	/**
	 * Parses the string to long.
	 *
	 * @param s the s
	 * @param dec the dec
	 * @return the long
	 */
	protected long parseStringToLong(String s, Decimals dec) {
		long ret = Long.MIN_VALUE;
		int decimal_place = 0;
		
		if(dec == Decimals.NONE) {
			decimal_place = 0;
		} else if(dec == Decimals.THOUSAND) {
			decimal_place = 3;
		} else if(dec == Decimals.MILLION) {
			decimal_place = 6;
		} else if(dec == Decimals.BILLION) {
			decimal_place = 9;
		}
				
		if(s.equals("-") || s.equals("") || s.equals(null)) {
			return Long.MIN_VALUE;
		}
		
		s = s.replace(".", "");
		String tmp[] = s.split(",");

		if(tmp.length == 2) {
			decimal_place -= tmp[1].length();
		}
		
		s = s.replace(",", "");
		
		for(int i = 0; i < decimal_place; i++) {
			s += '0';
		}

		try { 
			ret = Long.parseLong(s); 
		} catch(NumberFormatException e) { 
			return Long.MIN_VALUE; 
		} catch(NullPointerException e) {
			return Long.MIN_VALUE;
		}

	
		return ret;
	}
	
	/**
	 * Extract year from string.
	 *
	 * @param s the string
	 * @return the int
	 */
	protected int extractYearFromString(String s) {
		int ret = -1;
		
		String tmp = null;
		if(s.length() < 4)
			return -1;
		
		tmp = s.substring(0, 4);
		
		try { 
			ret = Integer.parseInt(tmp); 
		} catch(NumberFormatException e) { 
			return -1; 
		} catch(NullPointerException e) {
			return -1;
		}
		
		return ret;
	}
	
}
