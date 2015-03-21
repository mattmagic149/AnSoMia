package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class EquityRatio extends KeyPerformanceIndicator  {
	
	private double equity_ratio;

	public EquityRatio() {
		super();
	}
	
	public EquityRatio(SingleCompany company, double equity_ratio, Date date) {
		super(company, date);
		this.equity_ratio = equity_ratio;
	}

}
