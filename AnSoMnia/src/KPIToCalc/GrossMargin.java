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
	
	public GrossMargin(SingleCompany company, float gross, float sales, Date date) {
		super(company, date);
		calculateGrossMargin(gross, sales);
	}
		
	private void calculateGrossMargin(float gross, float sales) {
		gross_margin = gross / sales;
		return;
	}

	public float getGross_margin() {
		return gross_margin;
	}
}
