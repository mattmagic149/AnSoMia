package General;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import Support.HibernateSupport;

public class CompanyIndexer
{
  public static void main( String[] args ) throws Exception
  {
	  
	  CompanyIndexer obj = new CompanyIndexer();
	  /*try {
		  System.out.println("Downloading CompanyCSV now...");
		  obj.downloadCompanyCSV();
	  } catch (IOException e) {
		  System.out.println(e);
	  }*/

	  System.out.println("Downloading CompanyCSV complete!");
	  
	  obj.filterAndAddToDB();
  }
  
  public void downloadCompanyCSV() throws IOException {
	  URL website = new URL("http://www.deutsche-boerse-cash-market.com/blob/"
		  		+ "1424940/97e907d4cfa55e17b4e201cb47d6852f/data/allTradableInstruments.csv");
		  
	  ReadableByteChannel rbc = Channels.newChannel(website.openStream());
	  FileOutputStream fos = new FileOutputStream("data/companies.csv");
	  fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	  fos.close();
  }
  
  public void filterAndAddToDB() {
	  
		String csv_file = "data/companies.csv";
		BufferedReader br = null;
		String line = "";
		String company_name = "";
		String company_isin = "";
		String company_ticker = "";
		String company_instrument_group = "";
		SingleCompany company_obj = new SingleCompany();
		
		String[] isin_filter = {"DE", "US", "AT"};
		String[] instrument_group_filter = {"BOND", "EXCHANGE", "EB", "FOND", "EXTERNAL", "WARRANTS", "INSTRUMENTS"};
	 
		int counter = 0;
		int row_counter = 0;
		
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
				

				if(company_isin.length() != 12 || company_name.length() < 3 ||
						company_isin.length() < 3 || company_ticker.length() < 3 || 
						company_instrument_group.contains(instrument_group_filter[0]) ||
						company_instrument_group.contains(instrument_group_filter[1]) ||	
						company_instrument_group.contains(instrument_group_filter[2]) ||
						company_instrument_group.contains(instrument_group_filter[3]) ||
						company_instrument_group.contains(instrument_group_filter[4]) ||
						company_instrument_group.contains(instrument_group_filter[5]) ||
						company_instrument_group.contains(instrument_group_filter[6]) ||
						company_name.contains("ETF")) {
					continue;
				}
				
				if(company_isin.startsWith(isin_filter[0]) || company_isin.startsWith(isin_filter[1]) 
														|| company_isin.startsWith(isin_filter[2])) {
	
						
						
						List<Criterion>  criterions = new ArrayList<Criterion>();
						criterions.add(Restrictions.eq("isin", company_isin));
						company_obj = HibernateSupport.readOneObject(SingleCompany.class, criterions);
						
						if(company_obj == null) {
							company_obj = new SingleCompany(company_isin, company_name, company_ticker);
							HibernateSupport.beginTransaction();
							company_obj.saveToDB();
							HibernateSupport.commitTransaction();
							counter++;
						}
						
						//System.out.println("instrument_group = " + company_instrument_group);

		
				}
			}
	 
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
		//System.out.println(company_isin);
		System.out.println("Done");
	  }
  
}