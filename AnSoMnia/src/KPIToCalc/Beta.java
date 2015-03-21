package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class Beta extends KeyPerformanceIndicator  {

	private double beta;
		
	public Beta() {
		super();
	}
	
	public Beta(SingleCompany company, double beta, Date date) {
		super(company, date);
		this.beta = beta;
	}
	

}
