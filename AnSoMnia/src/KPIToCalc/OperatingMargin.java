package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class OperatingMargin extends KeyPerformanceIndicator  {

	private float operating_margin;
		
	public OperatingMargin() {
		super();
	}
	
	public OperatingMargin(SingleCompany company, float operating_income, float sales, Date date) {
		super(company, date);
		calculateOperatingMargin(operating_income, sales);
	}
		
	private void calculateOperatingMargin(float operating_income, float sales) {
		operating_margin = operating_income / sales;
		return;
	}

	public float getOperating_margin() {
		return operating_margin;
	}
}
