package General;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import General.SingleCompany;
import Interface.ISaveAndDelete;
import Support.HibernateSupport;

@Entity
public class MarketValues implements ISaveAndDelete {
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	
	private Date date;
	private String market_place;
	private float stock_price;
	private float bid_price;
	private float ask_price;
	private String currency;
	
	@ManyToOne
	@JoinColumn(name="isin",updatable=false)
	protected SingleCompany company;
	
	public MarketValues() {
		this.date = new Date();
	}
	
	public MarketValues(SingleCompany company, String market_place, float stock_price, float bid_price, float ask_price, String currency) {
		this.date = new Date();
		this.company = company;
		this.market_place = market_place;
		this.stock_price = stock_price;
		this.bid_price = bid_price;
		this.ask_price = ask_price;
		this.currency = currency;
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

	public SingleCompany getCompany() {
		return company;
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
