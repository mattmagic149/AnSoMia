package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class ReturnOnEquityRatio extends KeyPerformanceIndicator  {
	
	private double sharpe;

	public ReturnOnEquityRatio() {
		super();
	}
	
	public ReturnOnEquityRatio(SingleCompany company, double sharpe, Date date) {
		super(company, date);
		this.sharpe = sharpe;
	}

}
