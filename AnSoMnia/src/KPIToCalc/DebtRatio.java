package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class DebtRatio extends KeyPerformanceIndicator  {
	
	private float debt_ratio;

	public DebtRatio() {
		super();
	}
	
	public DebtRatio(SingleCompany company, float debt, int total, Date date) {
		super(company, date);
		calculateDebtRatio(debt, total);
	}
		
	private void calculateDebtRatio(float debt, int total) {
		debt_ratio = debt / total;
		return;
	}

	public float getDebt_ratio() {
		return debt_ratio;
	}
}
