package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class ReturnOnEquityRatio extends KeyPerformanceIndicator  {
	
	private float return_on_equity;

	public ReturnOnEquityRatio() {
		super();
	}
	
	public ReturnOnEquityRatio(SingleCompany company, float return_on_equity, Date date) {
		super(company, date);
		this.return_on_equity = return_on_equity;
	}

	public float getROE() {
		return return_on_equity;
	}
}
