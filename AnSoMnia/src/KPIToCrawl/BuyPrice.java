package KPIToCrawl;

import java.util.Date;

import javax.persistence.Entity;

import Interface.KeyPerformanceIndicator;
import General.SingleCompany;

@Entity
public class BuyPrice extends KeyPerformanceIndicator {
	
	private float price;
	
	public BuyPrice() {
		super();
	}
	
	public BuyPrice(float price, SingleCompany company, Date date) {
		super(company, date);
		this.price = price;
	}

	public float getPrice() {
		return price;
	}
}
