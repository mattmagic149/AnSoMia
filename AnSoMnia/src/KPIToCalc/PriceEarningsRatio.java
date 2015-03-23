package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class PriceEarningsRatio extends KeyPerformanceIndicator  {
	
	private float price_earnings_ratio;

	public PriceEarningsRatio() {
		super();
	}
	
	public PriceEarningsRatio(SingleCompany company, float price, float earnings, Date date) {
		super(company, date);
		calculatePriceEarningsRatio(price, earnings);
	}
		
	private void calculatePriceEarningsRatio(float price, float earnings) {
		price_earnings_ratio = price / earnings;
		return;
	}

	public float getPrice_earnings_ratio() {
		return price_earnings_ratio;
	}
}
