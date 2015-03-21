package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class PriceCashflowRatio extends KeyPerformanceIndicator {
	private double price_cashflow_ratio;
	
	public PriceCashflowRatio() {
		super();
	}
	
	public PriceCashflowRatio(SingleCompany company, double price_cashflow_ratio, Date date) {
		super(company, date);
		this.price_cashflow_ratio = price_cashflow_ratio;
	}
}
