package KPIToCrawl;

import java.util.Date;

import javax.persistence.Entity;

import Interface.KeyPerformanceIndicator;
import General.SingleCompany;

@Entity
public class StockPrice extends KeyPerformanceIndicator {
	
	private float price;
	
	public StockPrice() {
		super();
	}
	
	public StockPrice(float price, SingleCompany company, Date date) {
		super(company, date);
		this.price = price;
	}

	public float getPrice() {
		return price;
	}
}
