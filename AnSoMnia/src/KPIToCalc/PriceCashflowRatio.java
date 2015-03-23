package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class PriceCashflowRatio extends KeyPerformanceIndicator {
	
	private float price_cashflow_ratio;
	
	public PriceCashflowRatio() {
		super();
	}
	
	public PriceCashflowRatio(SingleCompany company, float price, float cashflow, Date date) {
		super(company, date);
		calculatePriceCashflowRatio(price, cashflow);
	}
		
	private void calculatePriceCashflowRatio(float price, float cashflow) {
		price_cashflow_ratio = price / cashflow;
		return;
	}

	public float getPrice_cashflow_ratio() {
		return price_cashflow_ratio;
	}
}
