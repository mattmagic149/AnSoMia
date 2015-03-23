package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class PriceSalesRatio extends KeyPerformanceIndicator {
	
	private float price_sales_ratio;
	
	public PriceSalesRatio() {
		super();
	}
	
	public PriceSalesRatio(SingleCompany company, float price, float sales, int shares, Date date) {
		super(company, date);
		calculatePriceSalesRatio(price, sales, shares);
	}
		
	private void calculatePriceSalesRatio(float price, float sales, int shares) {
		price_sales_ratio = price / (sales / shares);
		return;
	}

	public float getPrice_sales_ratio() {
		return price_sales_ratio;
	}
}
