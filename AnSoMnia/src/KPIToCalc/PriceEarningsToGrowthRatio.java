package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.*;
import Interface.KeyPerformanceIndicator;

@Entity
public class PriceEarningsToGrowthRatio extends KeyPerformanceIndicator {
	
	private float price_earnings_to_growth_ratio;
	
	public PriceEarningsToGrowthRatio() {
		super();
	}
	
	public PriceEarningsToGrowthRatio(SingleCompany company, float price_earnings_to_growth_ratio, Date date) {
		super(company, date);
		this.price_earnings_to_growth_ratio = price_earnings_to_growth_ratio;
	}

	public float getPrice_earnings_to_growth_ratio() {
		return price_earnings_to_growth_ratio;
	}
}
