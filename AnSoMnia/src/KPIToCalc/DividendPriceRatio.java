package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class DividendPriceRatio extends KeyPerformanceIndicator {

	private float dividend_price_ratio;
	
	public DividendPriceRatio() {
		super();
	}
	
	public DividendPriceRatio(SingleCompany company, float dividend_price_ratio, Date date) {
		super(company, date);
		this.dividend_price_ratio = dividend_price_ratio;
	}

	public float getDividend_price_ratio() {
		return dividend_price_ratio;
	}
}
