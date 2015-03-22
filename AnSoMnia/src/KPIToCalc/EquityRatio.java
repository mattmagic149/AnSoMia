package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class EquityRatio extends KeyPerformanceIndicator  {
	
	private float equity_ratio;

	public EquityRatio() {
		super();
	}
	
	public EquityRatio(SingleCompany company, float equity, float total, Date date) {
		super(company, date);
		calculateEquityRatio(equity, total);
	}
		
	private void calculateEquityRatio(float equity, float total) {
		equity_ratio = equity / total;
		return;
	}

	public float getEquity_ratio() {
		return equity_ratio;
	}
}
