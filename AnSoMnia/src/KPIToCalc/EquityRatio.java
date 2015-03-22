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
	
	public EquityRatio(SingleCompany company, float equity_ratio, Date date) {
		super(company, date);
		this.equity_ratio = equity_ratio;
	}

	public float getEquity_ratio() {
		return equity_ratio;
	}
}
