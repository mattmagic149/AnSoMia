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
	private float stock_price;
	private float bid_price;
	private float ask_price;
	
	@ManyToOne
	@JoinColumn(name="isin",updatable=false)
	protected SingleCompany company;
	
	public MarketValues() {
		this.date = new Date();
	}
	
	public MarketValues(SingleCompany company, float stock_price, float bid_price, float ask_price) {
		this.date = new Date();
		this.company = company;
		this.stock_price = stock_price;
		this.bid_price = bid_price;
		this.ask_price = ask_price;
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
