package General;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import DatabaseClasses.Company;
import Mining.CompanyIndexIndustryCrawler;
import Support.HibernateSupport;

public class TestClass {

	public static void main(String[] args) {
		/*List<CompanyNews> all_news = HibernateSupport.readMoreObjects(CompanyNews.class, new ArrayList<Criterion>());
		CompanyNews news;
		int bigger_size_count = 0;
		int bigger_length_count = 0;

		for(int i = 0; i < all_news.size(); i++) {
			news = all_news.get(i);
			if(news.getCompanies().size() > 1) {
				//System.out.println("the company.size() of this news is: " + news.getCompanies().size());
				//System.out.println("text = " + news.getContent());
				bigger_size_count++;
			}
			if(news.getContent().length() < 100) {
				System.out.println("text = " + news.getContent());
				System.out.println("url = " + news.getUrl());
				bigger_length_count++;
			}
		}
		
		System.out.println("bigger_size_count = " + bigger_size_count);
		System.out.println("bigger_size_count = " + bigger_length_count);*/
		
		/*String line;
		String[] line_split;
		String isin;
		BufferedReader br;
		Company company;
		int counter = 0;
		try {
			br = new BufferedReader(new FileReader("data/market_values_backup.txt"));
			while ((line = br.readLine()) != null) {
				line_split = line.split(",");
				isin = line_split[7].substring(2, 14);
				company = HibernateSupport.readOneObjectByStringId(Company.class, isin);
				
				if(company == null && !isin.equals("isin`) VALUE")) {
					System.out.println(isin);
					counter++;
				}

			}
			System.out.println(counter);


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		/*List<Company> companies = HibernateSupport.readMoreObjects(Company.class, new ArrayList<Criterion>());
		Company company;
		for(int i = 0; i < companies.size(); i++) {
			System.out.println(i + 1);
			company = companies.get(i);
			if(company.getWallstreetId() == 0) {
				Connection.Response response;
				try {
					response = Jsoup.connect("http://www.wallstreet-online.de" + "/aktien/" + company.getWallstreetQueryString()).execute();
					Elements trade_button = response.parse().select(".tradebutton");
					if(trade_button.size() > 0) {
						company.setWallstreetId(Integer.parseInt(trade_button.first().attr("instid")));
						company.setWallstreetMarketId(Integer.parseInt(trade_button.first().attr("marketId")));
						HibernateSupport.beginTransaction();
						company.saveToDB();
						HibernateSupport.commitTransaction();

					}
					
				} catch (IOException | NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		CompanyIndexIndustryCrawler ciic = new CompanyIndexIndustryCrawler();
		companies = HibernateSupport.readMoreObjects(Company.class, new ArrayList<Criterion>());
		for(int i = 0; i < companies.size(); i++) {
			//System.out.println(i + 1);
			company = companies.get(i);
			try {
				if(company.getWallstreetQueryString() == null || company.getWallstreetQueryString().equals("null")
						|| company.getWallstreetQueryString().equals("NULL")) {
					ciic.crawlCompanyWallstreetInformation(company);
					System.out.println(company.getIsin());
				}
				
				if(company.getFinanceQueryString() == null || company.getFinanceQueryString().equals("null")
						|| company.getFinanceQueryString().equals("NULL")) {
					ciic.crawlCompanyFinanceInformation(company);
					System.out.println(company.getIsin());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}*/
		
		Company company = HibernateSupport.readOneObjectByStringId(Company.class, "AT000000STR1");
		
		String date_string = "08.08.15";
		Date date;
		Date date2 = new Date();
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
		try {
			date = dateFormat.parse(date_string);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		System.out.println(date);
		
		if (DateUtils.isSameDay(date, date2)) {
			System.out.println("dates are the same");
		} else if (date.before(date2)) {
			System.out.println("date is before");
		} else {
			System.out.println("date is after");
		}
		
		System.out.println(company.getNumberOfAddedDatesOfParticularMonthAndYear(date2));
		System.out.println(company.isMarketValueAlreadyAdded(date));

		

	}

}
