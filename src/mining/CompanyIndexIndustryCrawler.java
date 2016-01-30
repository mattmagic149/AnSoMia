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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.HibernateSupport;
import utils.HttpRequestManager;
import utils.HttpRequester;
import database.*;

// TODO: Auto-generated Javadoc
/**
 * The Class CompanyIndexIndustryCrawler.
 */
public class CompanyIndexIndustryCrawler implements Job
{
	
	/** The wall_street_url. */
	private String wall_street_url = "http://www.wallstreet-online.de";	
	
	/** The isin_filter. */
	private String[] isin_filter = {"DE", "US", "AT"};
	
	/** The instrument_group_filter. */
	private String[] instrument_group_filter = {"BOND", "EXCHANGE", "EB", "FOND", "EXTERNAL", "WARRANTS", "INSTRUMENTS"};
	
	/** The company_name_filter. */
	private String[] company_name_filter = {"ETF", " ETN"," ETP", "ETC", "EXCH."};
	
	/** The isin_blacklist. */
	private String[] isin_blacklist;
	
	/** The logger. */
	private Logger logger; 
	
	/** The filehandler. */
	private FileHandler fh; 
	
	/** The http_req_manager. */
	private HttpRequestManager http_req_manager;
	
	/** The number_of_companies_added. */
	private int number_of_companies_added;
	
	/** The date_added. */
	private Date date_added;
	
	/** The csv_file_name. */
	private String csv_file_name;
	
	
	/**
	 * Instantiates a new company index industry crawler.
	 */
	public CompanyIndexIndustryCrawler() {
		this.csv_file_name = "data/companies.csv";
		this.logger = Logger.getLogger("MyLogger");
		this.logger.setUseParentHandlers(false);
		this.http_req_manager = HttpRequestManager.getInstance();
		this.date_added = new Date();
		
		this.isin_blacklist = readBlackList("data/blacklist.csv");
	  
		try {
			// This block configure the logger with handler and formatter
			String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
			fh = new FileHandler("data/tmp/company_indexer-"+ date + ".log", true);  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);
		} catch (SecurityException e) {  
			e.printStackTrace();
			return;
		} catch (IOException e) {  
			e.printStackTrace();
			return;
		}  
	}

	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		System.out.println("Downloading CompanyCSV now...");
		if(!downloadCompanyCSV()) {
			System.out.println("Could NOT dowload company csv file!!!");
		}

		System.out.println("Downloading CompanyCSV complete!");
	  
		filterAndAddToDB();
	}
  
	/**
	 * Download company csv.
	 *
	 * @return true, if successful
	 */
	private boolean downloadCompanyCSV() {
		URL website;
		try {
			website = new URL("http://www.deutsche-boerse-cash-market.com/blob/"
				  		+ "1424940/97e907d4cfa55e17b4e201cb47d6852f/data/allTradableInstruments.csv");
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(this.csv_file_name);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
  
	/**
	 * Read black list.
	 *
	 * @param filename the filename
	 * @return the string[]
	 */
	private String[] readBlackList(String filename) {
		FileReader file_reader;
		String[] result = null;
		try {
			file_reader = new FileReader(filename);
			BufferedReader buffered_reader = new BufferedReader(file_reader);
			List<String> lines = new ArrayList<String>();
			String line = null;
			while ((line = buffered_reader.readLine()) != null) {
				lines.add(line);
			}
			buffered_reader.close();
			result = lines.toArray(new String[lines.size()]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
     
		return result;
	}
  
	/**
	 * Filter and add to db.
	 */
	private void filterAndAddToDB() {
	  
		BufferedReader br = null;
		this.number_of_companies_added = 0;
		int row_counter = 0;
		String line;
		
		try {
			br = new BufferedReader(new FileReader(csv_file_name));
			while ((line = br.readLine()) != null) {
				if(row_counter++ < 5) {
					continue;
				}
				
				this.processCompanyLine(line);

			}
	 
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
		
		System.out.println("Added " + number_of_companies_added + " companies");
	}
	
	/**
	 * Process company line.
	 *
	 * @param line the line
	 * @return true, if successful
	 */
	private boolean processCompanyLine(String line) {
		String company_name = "";
		String company_isin = "";
		String company_ticker = "";
		String company_instrument_group = "";
		Company company = new Company();
		String[] company_strings = line.split(";");
		company_name = company_strings[1];
		company_isin = company_strings[2];
		company_ticker = company_strings[5];
		company_instrument_group = company_strings[8];
		
		if(!filter(company_isin, company_instrument_group, company_name, company_ticker))
			return false;
		

		List<Criterion>  criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("isin", company_isin));
		company = HibernateSupport.readOneObject(Company.class, criterions);
		if(company == null) {
			company = new Company(company_isin, company_name, company_ticker, this.date_added);
	  		System.out.println("now crawling " + company.getIsin());

	  		if(!this.crawlCompanyWallstreetInformation(company)) {
				this.logger.info("Company " + company.getIsin() + " not crawled from wallstreet-online.de");
	  		}
	  		
	  		if(!this.crawlCompanyFinanceInformation(company)) {
				logger.info("Company " + company.getIsin() + " not crawled from finanzen.at");
	  		}
			this.number_of_companies_added++;
			
		} 
		
		if(company.getFinanceQueryString() == null || company.getFinanceQueryString().equals("null")
				|| company.getFinanceQueryString().equals("NULL")) {
	  		System.out.println("now crawling " + company.getIsin() + " for finance");

	  		if(!this.crawlCompanyFinanceInformation(company)) {
				logger.info("Company " + company.getIsin() + " not crawled from finanzen.at");
	  		}
		} 
		
		if(company.getWallstreetQueryString() == null || company.getWallstreetQueryString().equals("null")
				|| company.getWallstreetQueryString().equals("NULL")) {
	  		System.out.println("now crawling " + company.getIsin() + " for wallstreet");

	  		if(!this.crawlCompanyWallstreetInformation(company)) {
				this.logger.info("Company " + company.getIsin() + " not crawled from wallstreet-online.de");
	  		}
		}
		
		
		if(company.getWallstreetId() == 0) {
			System.out.println(company.getWallstreetQueryString());
			HttpRequester http_requester = this.http_req_manager.getCorrespondingHttpRequester(wall_street_url);
			Element response = http_requester.getHtmlContent("/aktien/" + company.getWallstreetQueryString());
			
			if(response == null) {
				return false;
			}
			
			Elements trade_button = response.select(".tradebutton");
			if(trade_button.size() > 0) {
				company.setWallstreetId(Integer.parseInt(trade_button.first().attr("instid")));
			}
		}
		
		HibernateSupport.beginTransaction();
		company.saveToDB();
		HibernateSupport.commitTransaction();
		
		return true;
	}

  
  	/**
	   * Crawl company wallstreet information.
	   *
	   * @param company the company
	   * @return true, if successful
	   */
	  private boolean crawlCompanyWallstreetInformation(Company company) {
  		  		
  		String isin = company.getIsin();
  		HttpRequester http_requester = this.http_req_manager.getCorrespondingHttpRequester(this.wall_street_url);
  		String json = http_requester.getJsonContent("/_rpc/json/search/auto/searchInst/" + isin);
  		
  		if(json == null) {
  			return false;
  		}
		
		try {
			JSONObject obj = new JSONObject(json);
			JSONArray arr = obj.getJSONArray("result");
			
			String ankers_string = ((JSONObject) arr.get(0)).getString("label");
			Elements ankers = Jsoup.parse(ankers_string).select("a");
			System.out.println(ankers.attr("href").split("/")[2]);
			company.setWallstreetQueryString(ankers.attr("href").split("/")[2]);
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		
		
		Element response = http_requester.getHtmlContent("/aktien/" + company.getWallstreetQueryString());
		
		if(response == null) {
			return false;
		}

		Elements t_data = response.select(".t-data");
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
  	
  	/**
	   * Creates the industry sectors.
	   *
	   * @param industry_sectors the industry_sectors
	   * @return the array list
	   */
	  private ArrayList<IndustrySector> createIndustrySectors(Elements industry_sectors) {
  		ArrayList<IndustrySector> ret = new ArrayList<IndustrySector>();
  		IndustrySector industry_sector;
  		String industry_name;
  		String wallstreet_query_string;
  		
  		for(int k = 0; k < industry_sectors.size(); k++) {
			industry_name = industry_sectors.get(k).html().replace("&amp;", "");
			if(industry_sectors.get(k).attr("href").split("/").length >= 4) {
				wallstreet_query_string = industry_sectors.get(k).attr("href").split("/")[3];
			} else {
				wallstreet_query_string = "";
			}
			industry_sector = HibernateSupport.readOneObjectByStringId(IndustrySector.class, industry_name);
			
			if(industry_sector == null) {
				System.out.println("industry_sector == NULL");
				industry_sector = new IndustrySector(industry_name, wallstreet_query_string, this.date_added);
				
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
  	
  	/**
	   * Creates the indexes.
	   *
	   * @param indexes the indexes
	   * @return the array list
	   */
	  private ArrayList<Index> createIndexes(Elements indexes) {
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
				Element response;
				HttpRequester http_requester = this.http_req_manager.getCorrespondingHttpRequester(indexes.get(k).attr("abs:href"));
				response = http_requester.getHtmlContentWithCompleteUrl(indexes.get(k).attr("abs:href"));
				if(response == null) {
					continue;
				}
				
				t_data = response.select(".t-data");
				
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
				
				index = new Index(isin, name, symbol, wkn, valor, wall_street_query_string, this.date_added);
				HibernateSupport.beginTransaction();
				index.saveToDB();
				HibernateSupport.commitTransaction();
				ret.add(index);

			} else {
				ret.add(index);
			}
		}
  		
  		return ret;
  	}
  	
  	/**
	   * Crawl company finance information.
	   *
	   * @param company the company
	   * @return true, if successful
	   */
	  private boolean crawlCompanyFinanceInformation(Company company) {
  		String isin = company.getIsin();

  		HttpRequester http_requester = http_req_manager.getCorrespondingHttpRequester("http://finanzen.net");
  		Element response = http_requester.getHtmlContent("/ajax/SearchController_Suggest?max_results=1&Keywords=" + isin);
		
  		if(response == null) {
  			return false;
  		}
  		
		String tmp = response.select("body").first().toString().replace("\n", "");
		tmp = tmp.replaceAll(".*new Array[(](.+?)[)][)].*", "$1");
		String tmp_array[] = tmp.split(", ");
		tmp = tmp_array[tmp_array.length - 1].replace("\"", "");
		tmp_array = tmp.split("\\|");
		company.setFinanceQueryString(tmp_array[0]);
		
  		return true;
  	}
  
    /**
     * Filters unwanted companies.
     *
     * @param company_isin the company_isin
     * @param company_instrument_group the company_instrument_group
     * @param company_name the company_name
     * @param company_ticker the company_ticker
     * @return true, if successful
     */
    private boolean filter(String company_isin, String company_instrument_group, String company_name,
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