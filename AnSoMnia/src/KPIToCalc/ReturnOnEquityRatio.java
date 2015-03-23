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
	
	public ReturnOnEquityRatio(SingleCompany company, float net, float equity, Date date) {
		super(company, date);
		calculateReturnOnEquityRatio(net, equity);
	}
		
	private void calculateReturnOnEquityRatio(float net, float equity) {
		return_on_equity = net / equity;
		return;
	}

	public float getROE() {
		return return_on_equity;
	}
}
