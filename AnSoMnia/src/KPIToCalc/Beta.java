package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class Beta extends KeyPerformanceIndicator  {

	private float beta;
		
	public Beta() {
		super();
	}
	
	public Beta(SingleCompany company, float beta, Date date) {
		super(company, date);
		this.beta = beta;
	}

	public float getBeta() {
		return beta;
	}
}
