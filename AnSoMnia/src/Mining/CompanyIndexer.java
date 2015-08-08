package Mining;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import General.SingleCompany;
import Support.HibernateSupport;

public class CompanyIndexer implements Job
{
	private static String wall_street_url = "http://www.wallstreet-online.de";	
	private static String[] isin_filter = {"DE", "US", "AT"};
	private static String[] instrument_group_filter = {"BOND", "EXCHANGE", "EB", "FOND", "EXTERNAL", "WARRANTS", "INSTRUMENTS"};
	private static String[] company_name_filter = {"ETF", " ETN"," ETP", "ETC", "EXCH."};
	private static String[] isin_blacklist;
	public static Logger logger; 
	public static FileHandler fh; 
	

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
	  logger = Logger.getLogger("MyLogger");
	  logger.setUseParentHandlers(false);
	  
	  try {
		  // This block configure the logger with handler and formatter
		  String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		  fh = new FileHandler("data/tmp/company_indexer-"+ date + ".log", true);  
		  logger.addHandler(fh);
		  SimpleFormatter formatter = new SimpleFormatter();  
		  fh.setFormatter(formatter);
	  } catch (SecurityException e) {  
	      e.printStackTrace();  
	  } catch (IOException e) {  
	      e.printStackTrace();  
	  }  

	  try {
		  System.out.println("Downloading CompanyCSV now...");
		  downloadCompanyCSV();
	  } catch (IOException e) {
		  System.out.println(e);
	  }

	  System.out.println("Downloading CompanyCSV complete!");
	  
	  filterAndAddToDB();
  }
  
  public static void downloadCompanyCSV() throws IOException {
	  URL website = new URL("http://www.deutsche-boerse-cash-market.com/blob/"
		  		+ "1424940/97e907d4cfa55e17b4e201cb47d6852f/data/allTradableInstruments.csv");
		  
	  ReadableByteChannel rbc = Channels.newChannel(website.openStream());
	  FileOutputStream fos = new FileOutputStream("data/companies.csv");
	  fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	  fos.close();
  }
  
  public static String[] readBlackList(String filename) throws IOException {
      FileReader fileReader = new FileReader(filename);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      List<String> lines = new ArrayList<String>();
      String line = null;
      while ((line = bufferedReader.readLine()) != null) {
          lines.add(line);
      }
      bufferedReader.close();
      return lines.toArray(new String[lines.size()]);
  }
  
  public static void filterAndAddToDB() {
	  
		String csv_file = "data/companies.csv";
		BufferedReader br = null;
		String line = "";
		String company_name = "";
		String company_isin = "";
		String company_ticker = "";
		String company_instrument_group = "";
		SingleCompany company_obj = new SingleCompany();
	 
		int counter = 0;
		int row_counter = 0;
	
		try {
			isin_blacklist = readBlackList("data/blacklist.csv");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			br = new BufferedReader(new FileReader(csv_file));
			while ((line = br.readLine()) != null) {
				if(row_counter++ < 5)
					continue;
				
				String[] company = line.split(";");
				company_name = company[1];
				company_isin = company[2];
				company_ticker = company[5];
				company_instrument_group = company[8];
				
				
				
				if(!filter(company_isin, company_instrument_group, company_name, company_ticker))
					continue;
				

				List<Criterion>  criterions = new ArrayList<Criterion>();
				criterions.add(Restrictions.eq("isin", company_isin));
				company_obj = HibernateSupport.readOneObject(SingleCompany.class, criterions);
				if(company_obj == null) {
					company_obj = new SingleCompany(company_isin, company_name, company_ticker);
			  		System.out.println("now crawling " + company_obj.getIsin());

			  		crawlWallstreetWrapper(company_obj); ///TODO: if...
			  		crawlFinanceWrapper(company_obj); ///TODO: if...
					counter++;
					
				} else if(company_obj.getFinanceQueryString() == null) {
			  		System.out.println("now crawling " + company_obj.getIsin() + " for finance");

			  		crawlFinanceWrapper(company_obj); ///TODO: if...
				} else if(company_obj.getWallstreetQueryString() == null) {
			  		System.out.println("now crawling " + company_obj.getIsin() + " for wallstreet");

			  		crawlWallstreetWrapper(company_obj); ///TODO: if...
				}
				
				HibernateSupport.beginTransaction();
				company_obj.saveToDB();
				HibernateSupport.commitTransaction();

			}
	 
		} catch(HttpStatusException e) {
			System.out.println(e + "in BIG catch block");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Added " + counter + " companies");
	}

  	static public boolean crawlWallstreetWrapper(SingleCompany company) {
  		try {
			Thread.sleep(1000);
			crawlCompanyWallstreetInformation(company);
		} catch(SocketTimeoutException e) {
			try {
				Thread.sleep(1000);
				crawlCompanyWallstreetInformation(company);
			} catch(SocketTimeoutException ex) {
				try {
					Thread.sleep(1000);
					crawlCompanyWallstreetInformation(company);
				} catch(InterruptedException | IOException exc) {
					System.out.println(exc);
					logger.info("Company " + company.getIsin() + " not crawled from wallstreet-online.de");
					return false;
				}
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
			}
			
		} catch(IOException | InterruptedException e) {
			System.out.println(e);
			logger.info("Company " + company.getIsin() + " not crawled from wallstreet-online.de");
			return false;
		}
  		
  		return true;
  		
  	}
  	
  	static public boolean crawlFinanceWrapper(SingleCompany company) {
  	
  		try {
			Thread.sleep(1000);
			crawlCompanyFinanceInformation(company);
		} catch(SocketTimeoutException e) {
			try {
				Thread.sleep(1000);
				crawlCompanyFinanceInformation(company);
			} catch(SocketTimeoutException ex) {
				try {
					Thread.sleep(1000);
					crawlCompanyFinanceInformation(company);
				} catch(IOException | InterruptedException e1) {
					System.out.println(e1 + " in crawlFinanceWrapper after 3rd try...");
					logger.info("Company " + company.getIsin() + " not crawled from finanzen.at");
					return false;
				}
				
			} catch (InterruptedException | IOException e1) {
				e1.printStackTrace();
				return false;
			}
			
		} catch(IOException | InterruptedException e) {
			System.out.println(e);
			logger.info("Company " + company.getIsin() + " not crawled from finanzen.at");
			return false;
		}
  		
  		return true;
  	}

  
  	static public boolean crawlCompanyWallstreetInformation(SingleCompany company) throws IOException {
  		  		
  		String isin = company.getIsin();
		Connection.Response response = Jsoup.connect(wall_street_url + "/suche/?suche=&q=" + isin).execute();
		String[] url_split = response.url().toString().split("/");
		company.setWallstreetQueryString(url_split[url_split.length - 1]);
		
		Elements t_data = response.parse().select(".t-data");
		for(int i = 0; i < t_data.size(); i++) {
			if(t_data.get(i).toString().contains("Symbole")) {
				Elements divs = t_data.get(i).select("div");
				
				for(int j = 0; j < divs.size(); j++) {
					if(divs.get(j).toString().contains("ISIN") ||
					   divs.get(j).toString().contains("WKN") ||
					   divs.get(j).toString().contains("SYMBOL") ||
					   divs.get(j).toString().contains("VALOR")) {
						
							String tmp = divs.get(j).toString().replace("\n", "");
							company.setWkn(tmp.replaceAll("<div>.*WKN: (.+?)<br>.*</div>", "$1"));
							company.setValor(tmp.replaceAll("<div>.*VALOR: (.+?)</div>", "$1"));
					}
				}				
			}
		}
  		
  		return true;
  	}
  	
static public boolean crawlCompanyFinanceInformation(SingleCompany company) throws IOException {
  		  		
  		String isin = company.getIsin();
		Document response = Jsoup.connect("http://www.finanzen.net/ajax/SearchController_Suggest?max_results=1&Keywords=" + isin)//.data("query", "Java")
				  //.userAgent("Mozilla")
				  .timeout(3000)
				  .get();
		
		String tmp = response.select("body").first().toString().replace("\n", "");
		tmp = tmp.replaceAll(".*new Array[(](.+?)[)][)].*", "$1");
		String tmp_array[] = tmp.split(", ");
		tmp = tmp_array[tmp_array.length - 1].replace("\"", "");
		tmp_array = tmp.split("\\|");
		company.setFinanceQueryString(tmp_array[0]);
		
  		return true;
  	}
  
    static public boolean filter(String company_isin, String company_instrument_group, String company_name,
    		String company_ticker) {
	  
    	for(int i = 0; i < isin_blacklist.length; i++) {
    		if(company_isin.contains(isin_blacklist[i])) {
    			return false;
    		}
		}
	  
		for(int i = 0; i < instrument_group_filter.length; i++) {
			if(company_instrument_group.contains(instrument_group_filter[i])) {
				return false;
			}
		}
		
		for(int i = 0; i < company_name_filter.length; i++) {
			if(company_name.contains(company_name_filter[i])) {
				return false;
			}
		}
		
		if(company_isin.length() != 12 || company_name.length() < 3 ||
				company_isin.length() < 3 || company_ticker.length() < 3) {
			return false;
		}
		
		if(!company_isin.startsWith(isin_filter[0]) && !company_isin.startsWith(isin_filter[1]) 
				&& !company_isin.startsWith(isin_filter[2])) {
			return false;
		}
	  
		return true;
    }
  
}