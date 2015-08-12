package Mining;

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

import General.MainApplication;
import General.SingleCompany;
import Support.HibernateSupport;

public abstract class Crawler {

	protected static List<SingleCompany> companies_not_crawled = new ArrayList<SingleCompany>();
	protected enum DECIMALS {
	    NONE, THOUSAND, MILLION, BILLION 
	}
	protected String name;
	protected Logger logger; 
	protected FileHandler fh;
	protected int reconnection_attempts;
	
	public void setName(String name) {
		this.name = name;
		this.reconnection_attempts = 1;
	}
	
	protected void startCrawling() throws Exception
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
	    
	    for (String key : MainApplication.isin_mutex_map.keySet()) {
	        isin_list.add(key);
	    }
	    
	    Collections.shuffle(isin_list);
	    String isin;
		Lock mutex;
		SingleCompany company;
		int companies_size = isin_list.size();
		
	    while(isin_list.size() > 0) {
	    	isin = isin_list.get(0);
	    	isin_list.remove(0);
	    	
	    	mutex = MainApplication.isin_mutex_map.get(isin);
	    	mutex.lock();
	    	
	    		company = HibernateSupport.readOneObjectByStringId(SingleCompany.class, isin);
	    		System.out.println("Crawling Company: " + company.getCompanyName() + ", "
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
				Thread.sleep(250);
	    	
	    	mutex.unlock();
	    	
	    }
		
		for(int i = 0; i < companies_not_crawled.size(); i++) {
			System.out.println("COMPANY: " + companies_not_crawled.get(i).getCompanyName() + " " + 
					  			companies_not_crawled.get(i).getIsin() + " " +
					  			companies_not_crawled.get(i).getTicker());
		}
		
		System.out.println((companies_not_crawled.size()) + " not crawled");
		
	  	  	 
	}
	
	protected boolean crawlInfos(SingleCompany company) {return false;}
	
	protected float parseFloat(String s) {
		float ret = Float.MIN_VALUE;
		String tmp = s.split(" ")[0];
		tmp = tmp.replace(".", "");
		tmp = tmp.replace(",", ".");

		try { 
			ret = Float.parseFloat(tmp); 
		} catch(NumberFormatException e) { 
			return Float.MIN_VALUE;
		} catch(NullPointerException e) {
			return Float.MIN_VALUE;
		}
	
		return ret;
	}

	protected long parseStringToLong(String s, DECIMALS dec) {
		long ret = Long.MIN_VALUE;
		int decimal_place = 0;
		
		if(dec == DECIMALS.NONE) {
			decimal_place = 0;
		} else if(dec == DECIMALS.THOUSAND) {
			decimal_place = 3;
		} else if(dec == DECIMALS.MILLION) {
			decimal_place = 6;
		} else if(dec == DECIMALS.BILLION) {
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
