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

import interfaces.ISaveAndDelete;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import utils.HibernateSupport;
import database.Company;

/**
 * The Class MarketValue.
 */
@Entity
public class MarketValue implements ISaveAndDelete, Comparable<MarketValue> {
	
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	
	/** The date. */
	private Date date;
	
	/** The market_place. */
	private String market_place;
	
	/** The stock_price. */
	private float stock_price;
	
	/** The bid_price. */
	private float bid_price;
	
	/** The ask_price. */
	private float ask_price;
	
	/** The currency. */
	private String currency;

	/** The open. */
	private float open;
	
	/** The high. */
	private float high;
	
	/** The low. */
	private float low;
	
	/** The close. */
	private float close;
	
	/** The performance. */
	private float performance;
	
	/** The volume. */
	private float volume;
	
	/** The revenue. */
	private float revenue;
	
	
	/** The company. */
	@ManyToOne
	@JoinColumn(name="isin",updatable=false)
	protected Company company;
	
	/**
	 * Instantiates a new market value.
	 */
	public MarketValue() {}
	
	/**
	 * Instantiates a new market value.
	 *
	 * @param company the company
	 * @param market_place the market_place
	 * @param stock_price the stock_price
	 * @param bid_price the bid_price
	 * @param ask_price the ask_price
	 * @param currency the currency
	 * @param date the date
	 */
	public MarketValue(Company company, String market_place, float stock_price, float bid_price, 
			float ask_price, String currency, Date date) {
		this.date = date;
		this.company = company;
		this.market_place = market_place;
		this.stock_price = stock_price;
		this.bid_price = bid_price;
		this.ask_price = ask_price;
		this.currency = currency;
	}
	
	public MarketValue(float high, Date date) {
		this.date = date;
		this.high = high;
	}
	
	/**
	 * Instantiates a new market value.
	 *
	 * @param company the company
	 * @param date the date
	 * @param open the open
	 * @param high the high
	 * @param low the low
	 * @param close the close
	 * @param performance the performance
	 * @param volume the volume
	 * @param revenue the revenue
	 * @param currency the currency
	 * @param market_place the market_place
	 */
	public MarketValue(Company company, Date date, float open, float high, float low, float close, float performance,
						float volume, float revenue, String currency, String market_place) {
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.performance = performance;
		this.volume = volume;
		this.revenue = revenue;
		this.currency = currency;
		this.date = date;
		this.market_place = market_place;
		this.company = company;
	}
	
	/**
	 * Instantiates a new market value.
	 *
	 * @param serialized_market_value the serialized_market_value
	 * @param company the company
	 */
	public MarketValue(String[] serialized_market_value, Company company) {
		
		this.ask_price = Float.parseFloat(serialized_market_value[1]);
		this.bid_price = Float.parseFloat(serialized_market_value[2]);
		this.stock_price = Float.parseFloat(serialized_market_value[3]);
		this.currency = serialized_market_value[4];
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			this.date = formatter.parse(serialized_market_value[5]);
		} catch (ParseException e) {
			e.printStackTrace();
			assert(false);
		}
		
		this.market_place = serialized_market_value[6];
		this.open = Float.parseFloat(serialized_market_value[7]);
		this.high = Float.parseFloat(serialized_market_value[8]);
		this.low = Float.parseFloat(serialized_market_value[9]);
		this.close = Float.parseFloat(serialized_market_value[10]);
		this.performance = Float.parseFloat(serialized_market_value[11]);
		this.volume = Float.parseFloat(serialized_market_value[12]);
		this.revenue = Float.parseFloat(serialized_market_value[13]);
		
		this.company = company;
		
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Gets the market place.
	 *
	 * @return the market place
	 */
	public String getMarketPlace() {
		return market_place;
	}

	/**
	 * Gets the stock_price.
	 *
	 * @return the stock_price
	 */
	public float getStock_price() {
		return stock_price;
	}

	/**
	 * Gets the bid price.
	 *
	 * @return the bid price
	 */
	public float getBidPrice() {
		return bid_price;
	}

	/**
	 * Gets the ask price.
	 *
	 * @return the ask price
	 */
	public float getAskPrice() {
		return ask_price;
	}

	/**
	 * Gets the currency.
	 *
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * Gets the company.
	 *
	 * @return the company
	 */
	public Company getCompany() {
		return company;
	}
	
	/**
	 * Gets the open.
	 *
	 * @return the open
	 */
	public float getOpen() {
		return open;
	}

	/**
	 * Gets the high.
	 *
	 * @return the high
	 */
	public float getHigh() {
		return high;
	}

	/**
	 * Gets the low.
	 *
	 * @return the low
	 */
	public float getLow() {
		return low;
	}

	/**
	 * Gets the close.
	 *
	 * @return the close
	 */
	public float getClose() {
		return close;
	}

	/**
	 * Gets the performance.
	 *
	 * @return the performance
	 */
	public float getPerformance() {
		return performance;
	}

	/**
	 * Gets the volume.
	 *
	 * @return the volume
	 */
	public float getVolume() {
		return volume;
	}

	/**
	 * Gets the revenue.
	 *
	 * @return the revenue
	 */
	public float getRevenue() {
		return revenue;
	}
	
	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#serialize()
	 */
	@Override
	public String serialize() {
		return this.company.getIsin() + "\t" + this.ask_price + "\t" +
				this.bid_price + "\t" + this.stock_price + "\t" + this.currency + "\t" +
				this.date + "\t" + this.market_place + "\t" + this.open + "\t" + 
				this.high + "\t" + this.low + "\t" + this.close + "\t" + 
				this.performance + "\t" + this.volume + "\t" + this.revenue;
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

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(MarketValue other_value) {
		return this.getDate().compareTo(other_value.getDate());
	}
		
}
