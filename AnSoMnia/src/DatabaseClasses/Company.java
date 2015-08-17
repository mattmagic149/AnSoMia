package DatabaseClasses;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.time.DateUtils;

import javax.persistence.*;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

import Support.*;
import Interface.*;

@Entity
public class Company extends ShareInfo implements ISaveAndDelete {
	
	private String web_site;
	
	private String origin;
	
	private int wallstreet_id;
	
	private int wallstreet_market_id;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<MarketValue> market_values_list;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<KeyPerformanceIndicator> kpis_list;
	
	@ManyToMany
	@JoinTable(name="CompanyToNews",
				joinColumns={@JoinColumn(name="isin")}, 
				inverseJoinColumns={@JoinColumn(name="url")})
	private List<CompanyNews> company_news;
	
	@ManyToMany(mappedBy="companies")
	private List<Index> indexes;
	
	@ManyToMany(mappedBy="companies")
	private List<Index> industry_sectors;

	
	public Company() {
		
	}
	
	public Company(String isin, String company_name, String ticker) {
		this.isin = isin;
		this.name = company_name;
		this.ticker = ticker;
		
		this.market_values_list = new ArrayList<MarketValue>();
		this.kpis_list = new ArrayList<KeyPerformanceIndicator>();
		this.company_news = new ArrayList<CompanyNews>();

	}
	
	public Company(String serialized_company) {
		String[] tmp = serialized_company.split("\t");
		this.isin = tmp[0];
		this.name = tmp[1];
		this.ticker = tmp[2];
		this.valor = tmp[3];
		this.wkn = tmp[4];
		this.finance_query_string = tmp[5];
		this.wallstreet_query_string = tmp[6];
		this.origin = tmp[7];
		this.web_site = tmp[8];
		this.wallstreet_id = Integer.parseInt(tmp[9]);
		this.wallstreet_market_id = Integer.parseInt(tmp[10]);
		
		this.market_values_list = new ArrayList<MarketValue>();
		this.kpis_list = new ArrayList<KeyPerformanceIndicator>();
		this.company_news = new ArrayList<CompanyNews>();
		
		/*return this.isin + "\t" + this.name + "\t" + this.ticker + "\t" + this.valor + "\t" + 
				this.wkn + "\t" + this.finance_query_string + "\t" + this.wallstreet_query_string + "\t" +
				this.origin + "\t" + this.web_site;*/
	}
	
	public int getWallstreetId() {
		return wallstreet_id;
	}

	public void setWallstreetId(int wallstreet_id) {
		this.wallstreet_id = wallstreet_id;
	}

	public int getWallstreetMarketId() {
		return wallstreet_market_id;
	}

	public void setWallstreetMarketId(int wallstreet_market_id) {
		this.wallstreet_market_id = wallstreet_market_id;
	}
	
	public List<MarketValue> getMarketValuesList() {
		return market_values_list;
	}

	public List<KeyPerformanceIndicator> getKpisList() {
		return kpis_list;
	}
	
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public String getWebSite() {
		return web_site;
	}

	public void setWebSite(String web_site) {
		this.web_site = web_site;
	}
	
	public boolean isMarketValueAlreadyAdded(Date date) {
		for(int i = 0; i < market_values_list.size(); i++) {
			if(DateUtils.isSameDay(date, market_values_list.get(i).getDate())) {
				return true;
			}
		}
		return false;
	}
	
	public int getNumberOfAddedDatesOfParticularMonthAndYear(Date date) {
		int result = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		Date added_date;
		Calendar added_calendar = Calendar.getInstance();
		
		for(int i = 0; i < market_values_list.size(); i++) {
			added_date = market_values_list.get(i).getDate();
			added_calendar.setTime(added_date);
			
			if(added_calendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
			   added_calendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
				result++;
			}
		}
		
		return result;
	}
	
	public boolean addMarketValue(MarketValue values) {
		boolean success = false;
		
		if(!this.isMarketValueAlreadyAdded(values.getDate())) {
			if (this.market_values_list.add(values)){
				success = values.saveToDB();
			} else {
				assert(false);
			}
		}
		return success;
	}
	
	public boolean addKPIs(KeyPerformanceIndicator values) {
		boolean success = false;
		if (this.kpis_list.add(values)){
			success = values.saveToDB();
		}
		return success;
	}
	
	public KeyPerformanceIndicator getKpisCorrespondingToYear(int year) {
		Calendar new_year = Calendar.getInstance();
		Calendar existing_year = Calendar.getInstance();
		new_year.set(year, 1, 1);
		
		for(int i = 0; i < this.kpis_list.size(); i++) {
			existing_year.setTime(this.kpis_list.get(i).getDate());

			if(new_year.get(Calendar.YEAR) == existing_year.get(Calendar.YEAR)) {
				return this.kpis_list.get(i);
			}
		}
		
		return null;
	}
	
	public void updateKpisToCalculate() {
		for(int i = 0; i < this.kpis_list.size(); i++) {
			kpis_list.get(i).updateKpisToCalculate();
			HibernateSupport.beginTransaction();
			kpis_list.get(i).saveToDB();
			HibernateSupport.commitTransaction();
		}
	}
	
	public List<CompanyNews> getCompanyNews() {
		HibernateSupport.beginTransaction();
		// refresh DB-Image
		try{
			HibernateSupport.getCurrentSession().refresh(this);
			Hibernate.initialize(this.company_news);
		} catch(HibernateException e){
			System.out.println("Error: Instance of "+ this.getClass().getName() + " not merged to DB");
			return null;
		}
		finally{
			HibernateSupport.commitTransaction();
		}
		return this.company_news;
	}
	
	//*************************************************************************************************
	// addNews:
	// adds news to the news_list.
	public boolean addNews(CompanyNews new_news) {
		boolean success = false;
		
		if(new_news != null){	
			// add object to list and save to DB
			if (this.company_news.add(new_news))
				success = this.saveToDB();
		}
		
		if (success){
			return true;
		}
		else
			return false;
	}
	
	public boolean checkNewsAlreadyAdded(long hash) {
		for(int i = 0; i < this.company_news.size(); i++) {
			if(this.company_news.get(i).getHash() == hash) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean checkUrlAlreadyAdded(String url) {
		for(int i = 0; i < this.company_news.size(); i++) {
			if(this.company_news.get(i).getUrl().equals(url)) {
				return true;
			}
		}
		
		return false;
	}
	
	public String serializeCompany() {
		return this.isin + "\t" + this.name + "\t" + this.ticker + "\t" + this.valor + "\t" + 
				this.wkn + "\t" + this.finance_query_string + "\t" + this.wallstreet_query_string + "\t" +
				this.origin + "\t" + this.web_site + "\t" + this.wallstreet_id + "\t" + this.wallstreet_market_id;
	}
	
	
	@Override
	public boolean saveToDB() {
		if(!HibernateSupport.commit(this))
			return false;
		return true;
	}
	@Override
	public void deleteFromDB(Object obj) {
		HibernateSupport.deleteObject(this);
		
	}

}
