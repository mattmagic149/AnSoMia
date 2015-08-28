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

@Entity
public class MarketValue implements ISaveAndDelete, Comparable<MarketValue> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	
	private Date date;
	private String market_place;
	private float stock_price;
	private float bid_price;
	private float ask_price;
	private String currency;

	private float open;
	private float high;
	private float low;
	private float close;
	private float performance;
	private float volume;
	private float revenue;
	
	
	@ManyToOne
	@JoinColumn(name="isin",updatable=false)
	protected Company company;
	
	public MarketValue() {
		this.date = new Date();
	}
	
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
	
	public long getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public String getMarketPlace() {
		return market_place;
	}

	public float getStock_price() {
		return stock_price;
	}

	public float getBidPrice() {
		return bid_price;
	}

	public float getAskPrice() {
		return ask_price;
	}

	public String getCurrency() {
		return currency;
	}

	public Company getCompany() {
		return company;
	}
	
	public float getOpen() {
		return open;
	}

	public float getHigh() {
		return high;
	}

	public float getLow() {
		return low;
	}

	public float getClose() {
		return close;
	}

	public float getPerformance() {
		return performance;
	}

	public float getVolume() {
		return volume;
	}

	public float getRevenue() {
		return revenue;
	}
	
	public String serializeMarketValue() {
		return this.company.getIsin() + "\t" + this.ask_price + "\t" +
				this.bid_price + "\t" + this.stock_price + "\t" + this.currency + "\t" +
				this.date + "\t" + this.market_place + "\t" + this.open + "\t" + 
				this.high + "\t" + this.low + "\t" + this.close + "\t" + 
				this.performance + "\t" + this.volume + "\t" + this.revenue;
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

	@Override
	public int compareTo(MarketValue other_value) {
		return this.getDate().compareTo(other_value.getDate());
	}
		
}
