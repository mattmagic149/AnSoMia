package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class OperatingMargin extends KeyPerformanceIndicator  {

	private double operating_margin;
		
	public OperatingMargin() {
		super();
	}
	
	public OperatingMargin(SingleCompany company, double operating_margin, Date date) {
		super(company, date);
		this.operating_margin = operating_margin;
	}
	

}
