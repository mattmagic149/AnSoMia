package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class PriceSalesRatio extends KeyPerformanceIndicator {
	private double price_sales_ratio;
	
	public PriceSalesRatio() {
		super();
	}
	
	public PriceSalesRatio(SingleCompany company, double price_sales_ratio, Date date) {
		super(company, date);
		this.price_sales_ratio = price_sales_ratio;
	}
}
