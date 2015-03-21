package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class DebtRatio extends KeyPerformanceIndicator  {
	
	private double debt_ratio;

	public DebtRatio() {
		super();
	}
	
	public DebtRatio(SingleCompany company, double debt_ratio, Date date) {
		super(company, date);
		this.debt_ratio = debt_ratio;
	}

}
