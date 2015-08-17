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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import DatabaseClasses.Company;
import DatabaseClasses.Index;
import DatabaseClasses.IndustrySector;
import General.*;
import Support.HibernateSupport;

public class CompanyIndexIndustryCrawler implements Job
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

	  /*try {
		  System.out.println("Downloading CompanyCSV now...");
		  downloadCompanyCSV();
	  } catch (IOException e) {
		  System.out.println(e);
	  }*/

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
      FileReader file_reader = new FileReader(filename);
      BufferedReader buffered_reader = new BufferedReader(file_reader);
      List<String> lines = new ArrayList<String>();
      String line = null;
      while ((line = buffered_reader.readLine()) != null) {
          lines.add(line);
      }
      buffered_reader.close();
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
		Company company = new Company();
	 
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
				
				String[] company_strings = line.split(";");
				company_name = company_strings[1];
				company_isin = company_strings[2];
				company_ticker = company_strings[5];
				company_instrument_group = company_strings[8];
				
				if(!filter(company_isin, company_instrument_group, company_name, company_ticker))
					continue;
				

				List<Criterion>  criterions = new ArrayList<Criterion>();
				criterions.add(Restrictions.eq("isin", company_isin));
				company = HibernateSupport.readOneObject(Company.class, criterions);
				if(company == null) {
					company = new Company(company_isin, company_name, company_ticker);
			  		System.out.println("now crawling " + company.getIsin());

			  		crawlWallstreetWrapper(company); ///TODO: if...
			  		crawlFinanceWrapper(company); ///TODO: if...
					counter++;
					
				} 
				
				if(company.getFinanceQueryString() == null || company.getFinanceQueryString().equals("null")
						|| company.getFinanceQueryString().equals("NULL")) {
			  		System.out.println("now crawling " + company.getIsin() + " for finance");

			  		crawlFinanceWrapper(company); ///TODO: if...
				} 
				
				if(company.getWallstreetQueryString() == null || company.getWallstreetQueryString().equals("null")
						|| company.getWallstreetQueryString().equals("NULL")) {
			  		System.out.println("now crawling " + company.getIsin() + " for wallstreet");

			  		crawlWallstreetWrapper(company); ///TODO: if...
				}
				
				
				if(company.getWallstreetId() == 0) {
					System.out.println(company.getWallstreetQueryString());

					try {
						Connection.Response response = Jsoup.connect(wall_street_url + "/aktien/" + company.getWallstreetQueryString()).execute();
						Elements trade_button = response.parse().select(".tradebutton");
						if(trade_button.size() > 0) {
							company.setWallstreetId(Integer.parseInt(trade_button.first().attr("instid")));
							company.setWallstreetMarketId(Integer.parseInt(trade_button.first().attr("marketId")));
						}
					} catch(HttpStatusException e) {
						e.printStackTrace();  
					}
				}
				
				HibernateSupport.beginTransaction();
				company.saveToDB();
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

  	static public boolean crawlWallstreetWrapper(Company company) {
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
  	
  	static public boolean crawlFinanceWrapper(Company company) {
  	
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

  
  	static public boolean crawlCompanyWallstreetInformation(Company company) throws IOException {
  		  		
  		String isin = company.getIsin();
		String json = Jsoup.connect("http://www.wallstreet-online.de/_rpc/json/search/auto/searchInst/" + isin).ignoreContentType(true).execute().body();
		
		try {
			JSONObject obj = new JSONObject(json);
			JSONArray arr = obj.getJSONArray("result");
			
			String ankers_string = ((JSONObject) arr.get(0)).getString("label");
			Elements ankers = Jsoup.parse(ankers_string).select("a");
			System.out.println(ankers.attr("href").split("/")[2]);
			company.setWallstreetQueryString(ankers.attr("href").split("/")[2]);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Connection.Response response = Jsoup.connect(wall_street_url + "/aktien/" + company.getWallstreetQueryString()).execute();

		Elements t_data = response.parse().select(".t-data");
		Elements rows;
		Elements divs;
		Elements indexes;
		Elements industry_sectors;
		ArrayList<Index> index_list = new ArrayList<Index>();
		ArrayList<IndustrySector> industry_list = new ArrayList<IndustrySector>();
		for(int i = 0; i < t_data.size(); i++) {
			if(t_data.get(i).toString().contains("Symbole")) {
				divs = t_data.get(i).select("div");
				//System.out.println(t_data.get(i));
				rows = t_data.get(i).select("tr");
				
				for(int j = 0; j < rows.size(); j++) {
					if(rows.get(j).toString().contains("Unternehmen")) {
						//System.out.println(rows.get(j).child(1).child(0).html());
						company.setName(rows.get(j).child(1).child(0).html());
					} else if(rows.get(j).toString().contains("Herkunft")) {
						//System.out.println(rows.get(j).child(1).child(0).html());
						company.setOrigin(rows.get(j).child(1).child(0).html());
					} else if(rows.get(j).toString().contains("Website")) {
						//System.out.println(rows.get(j).child(1).child(0).child(0).html());
						company.setWebSite(rows.get(j).child(1).child(0).child(0).html());
					} else if(rows.get(j).toString().contains("Indizes")) {
						indexes = rows.get(j).select("a");
						index_list = createIndexes(indexes);						
					} else if(rows.get(j).toString().contains("Branche")) {
						industry_sectors = rows.get(j).select("a");
						industry_list = createIndustrySectors(industry_sectors);
					}
				}


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
		
		HibernateSupport.beginTransaction();
		company.saveToDB();

		for(int k = 0; k < index_list.size(); k++) {
			index_list.get(k).addCompany(company);
			System.out.println("adding " + company.getName() + "(" + company.getIsin() + ")" + " to "
								+ index_list.get(k).getName() + "(" + index_list.get(k).getIsin() + ")");
		}
		

		
		for(int k = 0; k < industry_list.size(); k++) {
			industry_list.get(k).addCompany(company);
			System.out.println("adding " + company.getName() + "(" + company.getIsin() + ")" + " to "
								+ industry_list.get(k).getName());
		}
		
		HibernateSupport.commitTransaction();
		
  		return true;
  	}
  	
  	static public ArrayList<IndustrySector> createIndustrySectors(Elements industry_sectors) {
  		ArrayList<IndustrySector> ret = new ArrayList<IndustrySector>();
  		IndustrySector industry_sector;
  		String industry_name;
  		String wallstreet_query_string;
  		
  		for(int k = 0; k < industry_sectors.size(); k++) {
			industry_name = industry_sectors.get(k).html();
			if(industry_sectors.get(k).attr("href").split("/").length >= 4) {
				wallstreet_query_string = industry_sectors.get(k).attr("href").split("/")[3];
			} else {
				wallstreet_query_string = "";
			}
			industry_sector = HibernateSupport.readOneObjectByStringId(IndustrySector.class, industry_name);
			
			if(industry_sector == null) {
				System.out.println("industry_sector == NULL");
				industry_sector = new IndustrySector(industry_name, wallstreet_query_string);
				
				HibernateSupport.beginTransaction();
				industry_sector.saveToDB();
				HibernateSupport.commitTransaction();
				ret.add(industry_sector);
				
			} else {
				System.out.println("industry_sector != null");

				ret.add(industry_sector);
			}
			
			
  		}
  		
  		return ret;
  	}
  	
  	static public ArrayList<Index> createIndexes(Elements indexes) {
  		ArrayList<Index> ret = new ArrayList<Index>();
		Index index;
		String wall_street_query_string;
		String wkn = null;
		String isin = null;
		String symbol = null;
		String valor = null;
		String name = null;
		String tmp = null;
		Elements t_data;
		Elements divs;
  		for(int k = 0; k < indexes.size(); k++) {
			List<Criterion>  criterions = new ArrayList<Criterion>();
			criterions.add(Restrictions.eq("name", indexes.get(k).html()));
			name = indexes.get(k).html();
			index = HibernateSupport.readOneObject(Index.class, criterions);
			if(index == null) {
				//System.out.println("index == null");
				wall_street_query_string = indexes.get(k).attr("href").split("/")[2];
				Connection.Response response;
				try {
					response = Jsoup.connect(indexes.get(k).attr("abs:href")).execute();
					t_data = response.parse().select(".t-data");
					
					for(int i = 0; i < t_data.size(); i++) {
						if(t_data.get(i).toString().contains("Symbole")) {
							divs = t_data.get(i).select("div");
							
							for(int j = 0; j < divs.size(); j++) {
								if(divs.get(j).toString().contains("ISIN") ||
								   divs.get(j).toString().contains("WKN") ||
								   divs.get(j).toString().contains("SYMBOL") ||
								   divs.get(j).toString().contains("VALOR")) {
									
										tmp = divs.get(j).toString().replace("\n", "");
										wkn = tmp.replaceAll("<div>.*WKN: (.+?)<br>.*</div>", "$1");
										valor = tmp.replaceAll("<div>.*VALOR: (.+?)</div>", "$1");
										symbol = tmp.replaceAll("<div>.*SYMBOL: (.+?)<br>.*</div>", "$1");
										isin = tmp.replaceAll("<div>.*ISIN: (.+?)\\s<br>.*</div>", "$1");

								}
							}			
						}
					}
					
					index = new Index(isin, name, symbol, wkn, valor, wall_street_query_string);
					HibernateSupport.beginTransaction();
					index.saveToDB();
					HibernateSupport.commitTransaction();
					ret.add(index);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				ret.add(index);
			}
		}
  		
  		return ret;
  	}
  	
  	static public boolean crawlCompanyFinanceInformation(Company company) throws IOException {
  		  		
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