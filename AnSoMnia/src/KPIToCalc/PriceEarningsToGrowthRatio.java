package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.*;
import Interface.KeyPerformanceIndicator;


@Entity
public class PriceEarningsToGrowthRatio extends KeyPerformanceIndicator {
	
	private double price_earnings_to_growth_ratio;
	
	public PriceEarningsToGrowthRatio() {
		super();
	}
	
	public PriceEarningsToGrowthRatio(SingleCompany company, double price_earnings_to_growth_ratio, Date date) {
		super(company, date);
		this.price_earnings_to_growth_ratio = price_earnings_to_growth_ratio;
	}
	
}
