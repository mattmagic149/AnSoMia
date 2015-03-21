package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class GrossMargin extends KeyPerformanceIndicator  {

	private double gross_margin;
		
	public GrossMargin() {
		super();
	}
	
	public GrossMargin(SingleCompany company, double gross_margin, Date date) {
		super(company, date);
		this.gross_margin = gross_margin;
	}
	

}
