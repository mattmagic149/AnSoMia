package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class Sharpe extends KeyPerformanceIndicator {
	private double alpha;
	
	public Sharpe() {
		super();
	}
	
	public Sharpe(SingleCompany company, double alpha, Date date) {
		super(company, date);
		this.alpha = alpha;
	}
}
