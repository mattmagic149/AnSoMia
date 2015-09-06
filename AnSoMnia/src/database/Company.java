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
package database;

import interfaces.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import javax.persistence.*;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.javatuples.Pair;

import utils.*;

// TODO: Auto-generated Javadoc
/**
 * The Class Company.
 */
@Entity
public class Company extends ShareInfo implements ISaveAndDelete {
	
	/** The website of the company. */
	private String web_site;
	
	/** The origin of the company. */
	private String origin;
	
	/** The wallstreetid. */
	private int wallstreet_id;
		
	/** List of all market values. */
	@OneToMany
	@JoinColumn(name="isin")
	private List<MarketValue> market_values_list;
	
	/** List of all KeyPerformanceIndicators. */
	@OneToMany
	@JoinColumn(name="isin")
	private List<KeyPerformanceIndicator> kpis_list;
	
	/** List of all news. */
	@ManyToMany
	@JoinTable(name="CompanyToNews",
				joinColumns={@JoinColumn(name="isin")}, 
				inverseJoinColumns={@JoinColumn(name="md5_hash")})
	private List<News> company_news;
	
	/** List of all indexes. */
	@ManyToMany(mappedBy="companies")
	private List<Index> indexes;
	
	/** List of all industry sectors. */
	@ManyToMany(mappedBy="companies")
	private List<Index> industry_sectors;
	
	@ManyToOne
	@JoinColumn(name="info",updatable=true)
	private CompanyInformation info;

	
	/**
	 * Instantiates a new company.
	 */
	public Company() {
		
	}
	
	/**
	 * Instantiates a new company.
	 *
	 * @param isin the isin
	 * @param company_name the company_name
	 * @param ticker the ticker
	 * @param date_added the date_added
	 */
	public Company(String isin, String company_name, String ticker, Date date_added) {
		this.isin = isin;
		this.name = company_name;
		this.ticker = ticker;
		this.date_added = date_added;
		
		this.market_values_list = new ArrayList<MarketValue>();
		this.kpis_list = new ArrayList<KeyPerformanceIndicator>();
		this.company_news = new ArrayList<News>();

	}
	
	/**
	 * Instantiates a new company.
	 *
	 * @param serialized_company the serialized_company
	 */
	public Company(String serialized_company) {
		String[] tmp = serialized_company.split("\t");
		this.isin = tmp[0];
		this.finance_query_string = tmp[1];
		this.name = tmp[2];
		this.ticker = tmp[3];
		this.valor = tmp[4];
		this.wallstreet_query_string = tmp[5];
		this.wkn = tmp[6];
		this.origin = tmp[7];
		this.wallstreet_id = Integer.parseInt(tmp[8]);
		this.web_site = tmp[9];
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			this.date_added = formatter.parse(tmp[10]);
		} catch (ParseException e) {
			e.printStackTrace();
			assert(false);
		}
		
		
		this.market_values_list = new ArrayList<MarketValue>();
		this.kpis_list = new ArrayList<KeyPerformanceIndicator>();
		this.company_news = new ArrayList<News>();
		
	}
	
	/**
	 * Gets the wallstreet id.
	 *
	 * @return the wallstreet id
	 */
	public int getWallstreetId() {
		return wallstreet_id;
	}

	/**
	 * Sets the wallstreet id.
	 *
	 * @param wallstreet_id the new wallstreet id
	 */
	public void setWallstreetId(int wallstreet_id) {
		this.wallstreet_id = wallstreet_id;
	}
	
	/**
	 * Gets the market values list.
	 *
	 * @return the market values list
	 */
	public List<MarketValue> getMarketValuesList() {
		return market_values_list;
	}

	/**
	 * Gets the kpis list.
	 *
	 * @return the kpis list
	 */
	public List<KeyPerformanceIndicator> getKpisList() {
		return kpis_list;
	}
	
	/**
	 * Gets the origin.
	 *
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * Sets the origin.
	 *
	 * @param origin the new origin
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	/**
	 * Gets the web site.
	 *
	 * @return the web site
	 */
	public String getWebSite() {
		return web_site;
	}

	/**
	 * Sets the web site.
	 *
	 * @param web_site the new web site
	 */
	public void setWebSite(String web_site) {
		this.web_site = web_site;
	}
	
	/**
	 * Checks if the market value is already added.
	 *
	 * @param date the date
	 * @return true, if is market value already added
	 */
	public boolean isMarketValueAlreadyAdded(Date date) {
		for(int i = 0; i < market_values_list.size(); i++) {
			if(DateUtils.isSameDay(date, market_values_list.get(i).getDate())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the number of added dates of particular month and year.
	 *
	 * @param date the year and month to check
	 * @return the number of added dates of particular month and year
	 */
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
	
	/**
	 * Gets the number of added dates of particular month and year from db.
	 *
	 * @param date the date
	 * @return the number of added dates of particular month and year from db
	 */
	public int getNumberOfAddedDatesOfParticularMonthAndYearFromDB(Date date) {
		Pair<Calendar, Calendar> from_to = MyDateUtils.getMaxAndMinOfMonth(date);
		Calendar from = from_to.getValue0();
		Calendar to = from_to.getValue1();	
		
		HibernateSupport.beginTransaction();
		
		long values_size = (long)HibernateSupport.getCurrentSession().createCriteria(MarketValue.class)
				.add(Restrictions.eq("company", this))
				.add(Restrictions.between("date", from.getTime(), to.getTime()))
				.setProjection(Projections.rowCount())
				.uniqueResult();
		
		HibernateSupport.commitTransaction();
		
		return (int) values_size;
	}
	
	/**
	 * Adds the market value.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
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
	
	/**
	 * Adds the market value without check.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	public boolean addMarketValueWithoutCheck(MarketValue values) {
		boolean success = false;
		
		if (this.market_values_list.add(values)){
			success = values.saveToDB();
		} else {
			assert(false);
		}
		return success;
	}
	
	/**
	 * Adds the kpi.
	 *
	 * @param values the kpi to add
	 * @return true, if successful
	 */
	public boolean addKPI(KeyPerformanceIndicator values) {
		boolean success = false;
		if (this.kpis_list.add(values)){
			success = values.saveToDB();
		}
		return success;
	}
	
	/**
	 * Gets the kpis corresponding to year.
	 *
	 * @param year the year
	 * @return the kpis corresponding to year
	 */
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
	
	/**
	 * Update kpis to calculate.
	 */
	public void updateKpisToCalculate() {
		for(int i = 0; i < this.kpis_list.size(); i++) {
			kpis_list.get(i).updateKpisToCalculate();
			HibernateSupport.beginTransaction();
			kpis_list.get(i).saveToDB();
			HibernateSupport.commitTransaction();
		}
	}
	
	/**
	 * Gets the company news.
	 *
	 * @return the company news
	 */
	public List<News> getCompanyNews() {
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
	
	/**
	 * Adds the news.
	 *
	 * @param new_news the new_news
	 * @return true, if successful
	 */
	public boolean addNews(News new_news) {
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
	
	/**
	 * Check news already added.
	 *
	 * @param hash the hash of the news
	 * @return true, if successful
	 */
	public boolean checkNewsAlreadyAdded(long hash) {
		for(int i = 0; i < this.company_news.size(); i++) {
			if(this.company_news.get(i).getHash() == hash) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Check url already added to news.
	 *
	 * @param url the url
	 * @return true, if successful
	 */
	public boolean checkUrlAlreadyAddedToNews(String url) {
		for(int i = 0; i < this.company_news.size(); i++) {
			if(this.company_news.get(i).getUrl().equals(url)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets the market values between two dates [from, to].
	 *
	 * @param from the start date
	 * @param to the end date
	 * @return the market values between dates
	 */
	public ArrayList<MarketValue> getMarketValuesBetweenDates(Date from, Date to) {
		ArrayList<MarketValue> ret = new ArrayList<MarketValue>();
		MarketValue current_value;
		for(int i = 0; i < this.market_values_list.size(); i++) {
			current_value = this.market_values_list.get(i);
			
			if((DateUtils.isSameDay(from, current_value.getDate()) || current_value.getDate().after(from)) &&
				(DateUtils.isSameDay(to, current_value.getDate()) || current_value.getDate().before(to))) {
				
				ret.add(current_value);
			}
		}
		
		return ret;
	}
	
	/**
	 * Gets the market values between two dates from database [from, to].
	 *
	 * @param from the start date
	 * @param to the end date
	 * @return the market values between dates
	 */
	public ArrayList<MarketValue> getMarketValuesBetweenDatesFromDB(Date from, Date to) {
		List<Criterion> list = new ArrayList<Criterion>();
		list.add(Restrictions.eq("company", this));
		list.add(Restrictions.between("date", from, to));
		return HibernateSupport.readMoreObjectsDesc(MarketValue.class, list, "date");
	}
	
	/**
	 * Gets the number of news.
	 *
	 * @return the number of news
	 */
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
	public int getNumberOfNews() {
		HibernateSupport.beginTransaction();
		int result = this.company_news.size();
		HibernateSupport.commitTransaction();

		return result;
	}
		
	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#serialize()
	 */
	@Override
	public String serialize() {
		return this.isin + "\t" + this.finance_query_string + "\t" + this.name + "\t" + 
				this.ticker + "\t" + this.valor + "\t" + this.wallstreet_query_string + "\t" + 
				this.wkn + "\t" +  this.origin + "\t" +this.wallstreet_id + "\t" + 
				this.web_site  + "\t" + this.date_added;
	}
	
	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#saveToDB()
	 */
	@Override
	public boolean saveToDB() {
		if(!HibernateSupport.commit(this))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#deleteFromDB(java.lang.Object)
	 */
	@Override
	public void deleteFromDB(Object obj) {
		HibernateSupport.deleteObject(this);	
	}

}
