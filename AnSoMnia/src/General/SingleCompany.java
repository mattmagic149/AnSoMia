package General;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.*;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

import Support.*;
import Interface.*;

@Entity
public class SingleCompany implements ISaveAndDelete {
	
	@Id
	private String isin;
	
	private String company_name;
	
	private String ticker;
	
	private String wallstreet_query_string;
	
	private String wkn;
		
	private String valor;
	
	private String finance_query_string;
	
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<MarketValues> market_values_list;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<KeyPerformanceIndicators> kpis_list;
	
	@ManyToMany
	@JoinTable(name="CompanyToNews",
				joinColumns={@JoinColumn(name="isin")}, 
				inverseJoinColumns={@JoinColumn(name="url")})
	private List<CompanyNews> company_news;

	
	public SingleCompany() {
		
	}
	
	public SingleCompany(String isin, String company_name, String ticker) {
		this.isin = isin;
		this.company_name = company_name;
		this.ticker = ticker;
		
		this.market_values_list = new ArrayList<MarketValues>();
		this.kpis_list = new ArrayList<KeyPerformanceIndicators>();
		this.company_news = new ArrayList<CompanyNews>();

	}
	
	public String getIsin() {
		return isin;
	}
	public void setIsin(String isin) {
		this.isin = isin;
	}
	public String getCompanyName() {
		return company_name;
	}
	public void setCompanyName(String company_name) {
		this.company_name = company_name;
	}
	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	
	public String getWallstreetQueryString() {
		return wallstreet_query_string;
	}

	public void setWallstreetQueryString(String wallstreet_query_string) {
		this.wallstreet_query_string = wallstreet_query_string;
	}
	
	public String getFinanceQueryString() {
		return finance_query_string;
	}

	public void setFinanceQueryString(String finance_query_string) {
		this.finance_query_string = finance_query_string;
	}
	
	public List<MarketValues> getMarketValuesList() {
		return market_values_list;
	}

	public List<KeyPerformanceIndicators> getKpisList() {
		return kpis_list;
	}

	public String getWkn() {
		return wkn;
	}

	public void setWkn(String wkn) {
		this.wkn = wkn;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public boolean addMarketValues(MarketValues values) {
		boolean success = false;
		if (this.market_values_list.add(values)){
			HibernateSupport.beginTransaction();
			success = values.saveToDB();
			HibernateSupport.commitTransaction();
		}
		return success;
	}
	
	public boolean addKPIs(KeyPerformanceIndicators values) {
		boolean success = false;
		if (this.kpis_list.add(values)){
			HibernateSupport.beginTransaction();
			success = values.saveToDB();
			HibernateSupport.commitTransaction();
		}
		return success;
	}
	
	public KeyPerformanceIndicators getKpisCorrespondingToYear(int year) {
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
			
			HibernateSupport.beginTransaction();
			new_news.saveToDB();
			// refresh DB-Image
			try{
				HibernateSupport.getCurrentSession().refresh(this);
				Hibernate.initialize(this.company_news);
			} catch(HibernateException e){
				System.out.println("Error: Instance of "+ this.getClass().getName() + " not merged to DB");
				HibernateSupport.commitTransaction();
				return false;
			}
			
			// add object to list and save to DB
			if (this.company_news.add(new_news))
				success = this.saveToDB();
			HibernateSupport.commitTransaction();			
		}
		
		if (success){
			return true;
		}
		else
			return false;
	}
	
	public boolean checkUrlsAlreadyAdded(String url) {
		for(int i = 0; i < this.company_news.size(); i++) {
			if(this.company_news.get(i).getUrl().equals(url)) {
				return true;
			}
		}
		
		return false;
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
