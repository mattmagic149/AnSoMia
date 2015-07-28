package Mining;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;

import General.SingleCompany;
import Support.HibernateSupport;

public abstract class KPICrawler {

	public static List<SingleCompany> company_not_crawled = new ArrayList<SingleCompany>();
	public enum DECIMALS {
	    NONE, THOUSAND, MILLION, BILLION 
	}
	
	public void execute() throws Exception
	{
						
		List<Criterion>  criterions = new ArrayList<Criterion>();
		List<SingleCompany> companies = HibernateSupport.readMoreObjects(SingleCompany.class, criterions);
		//SingleCompany cmp = HibernateSupport.readOneObjectByStringId(SingleCompany.class, "AT0000720008");
		//crawlKpis(cmp);

		int companies_size = companies.size();
		int timeout_counter = 0;
		boolean success = true;

		for(int i = 0; i < companies_size; i++) {
			System.out.println("Crawling Company: " + companies.get(i).getCompanyName() + ", "
					  + companies.get(i).getIsin() + ", " + companies.get(i).getTicker());
			try {
				if(!this.crawlKpis(companies.get(i))) {
					company_not_crawled.add(companies.get(i));
				}
			} catch(SocketTimeoutException e) {
				success = false;
				if(timeout_counter < 3) {
					i--;
					timeout_counter++;
				} else {
					success = true;
					company_not_crawled.add(companies.get(i));
				}
			} catch(IOException e) {
				System.out.println(e);
				company_not_crawled.add(companies.get(i));
			}
			  
			if(success == true) {
				timeout_counter = 0;
			}
			success = true;
		  	System.out.print("Crawled ");
		  	System.out.printf("%.2f", ((i + 1)/(float)companies_size) * 100);
		  	System.out.println(" % - " + (company_not_crawled.size()) + " not crawled");
			Thread.sleep(250);
		  }
		
		for(int i = 0; i < company_not_crawled.size(); i++) {
			System.out.println("COMPANY: " + company_not_crawled.get(i).getCompanyName() + " " + 
					  			company_not_crawled.get(i).getIsin() + " " +
					  			company_not_crawled.get(i).getTicker());
		}
		
		System.out.println((company_not_crawled.size()) + " not crawled");
		
	  	  	 
	}
	
	public boolean crawlKpis(SingleCompany company) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static float parseFloat(String s) {
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

	public static long parseStringToLong(String s, DECIMALS dec) {
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
	
	public static int extractYearFromString(String s) {
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
