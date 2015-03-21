package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class PriceEarningsRatio extends KeyPerformanceIndicator  {
	
	private double price_earnings_ratio;

	public PriceEarningsRatio() {
		super();
	}
	
	public PriceEarningsRatio(SingleCompany company, double price_earnings_ratio, Date date) {
		super(company, date);
		this.price_earnings_ratio = price_earnings_ratio;
	}

}
