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
	
	public PriceEarningsToGrowthRatio(SingleCompany company, float price_earnings_ratio, float eps_growth, Date date) {
		super(company, date);
		calculatePriceEarningsToGrowthRatio(price_earnings_ratio, eps_growth);
	}
		
	private void calculatePriceEarningsToGrowthRatio(float price_earnings_ratio, float eps_growth) {
		price_earnings_to_growth_ratio = price_earnings_ratio / eps_growth;
		return;
	}


	public float getPrice_earnings_to_growth_ratio() {
		return price_earnings_to_growth_ratio;
	}
}
