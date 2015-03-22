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
	
	public OperatingMargin(SingleCompany company, float operating_margin, Date date) {
		super(company, date);
		this.operating_margin = operating_margin;
	}

	public float getOperating_margin() {
		return operating_margin;
	}
}
