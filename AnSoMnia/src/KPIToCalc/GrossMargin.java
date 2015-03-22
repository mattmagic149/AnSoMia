package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class GrossMargin extends KeyPerformanceIndicator  {

	private float gross_margin;
		
	public GrossMargin() {
		super();
	}
	
	public GrossMargin(SingleCompany company, float net, float sales, Date date) {
		super(company, date);
		calculateGrossMargin(equity, total);
	}
		
	private void calculateEquityRatio(float equity, float total) {
		equity_ratio = equity / total;
		return;
	}

	public float getGross_margin() {
		return gross_margin;
	}
}
