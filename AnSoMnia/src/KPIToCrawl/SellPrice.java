package KPIToCrawl;

import java.util.Date;

import javax.persistence.Entity;

import Interface.KeyPerformanceIndicator;
import General.SingleCompany;

@Entity
public class SellPrice extends KeyPerformanceIndicator {
	
	private float price;
	
	public SellPrice() {
		super();
	}
	
	public SellPrice(float price, SingleCompany company, Date date) {
		super(company, date);
		this.price = price;
	}

	public float getPrice() {
		return price;
	}
}
