package KPIToCrawl;

import java.util.Date;

import javax.persistence.Entity;

import Interface.KeyPerformanceIndicator;
import General.SingleCompany;

@Entity
public class DailyPrice extends KeyPerformanceIndicator {
	
	private float price;
	
	public DailyPrice() {
		super();
	}
	
	public DailyPrice(float price, SingleCompany company, Date date) {
		super(company, date);
		this.price = price;
	}

	public float getPrice() {
		return price;
	}
}
