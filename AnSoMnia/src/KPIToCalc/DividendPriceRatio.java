package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class DividendPriceRatio extends KeyPerformanceIndicator {

	private double dividend_price_ratio;
	
	public DividendPriceRatio() {
		super();
	}
	
	public DividendPriceRatio(SingleCompany company, double dividend_price_ratio, Date date) {
		super(company, date);
		this.dividend_price_ratio = dividend_price_ratio;
	}
}
