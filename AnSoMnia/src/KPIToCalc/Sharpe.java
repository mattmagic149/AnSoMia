package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class Sharpe extends KeyPerformanceIndicator {
	private float sharpe;
	
	public Sharpe() {
		super();
	}
	
	public Sharpe(SingleCompany company, float sharpe, Date date) {
		super(company, date);
		this.sharpe = sharpe;
	}

	public float getSharpe() {
		return sharpe;
	}
}
