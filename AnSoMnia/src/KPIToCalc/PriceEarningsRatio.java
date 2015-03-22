package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class PriceEarningsRatio extends KeyPerformanceIndicator  {
	
	private float price_earnings_ratio;

	public PriceEarningsRatio(float price, float earnings) {
		super();
		this.price_earnings_ratio = price / earnings;
	}
	
	public PriceEarningsRatio(SingleCompany company, float price_earnings_ratio, Date date) {
		super(company, date);
		this.price_earnings_ratio = price_earnings_ratio;
	}

	public float getPrice_earnings_ratio() {
		return price_earnings_ratio;
	}
}
