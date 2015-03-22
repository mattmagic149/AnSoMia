package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class CashflowPerShare extends KeyPerformanceIndicator {
	
	private float cashflow_per_share;
	
	public CashflowPerShare() {
		super();
	}
	
	public CashflowPerShare(SingleCompany company, float cashflow, int shares, Date date) {
		super(company, date);
		calculateCashflowPerShare(cashflow, shares);
	}
		
	private void calculateCashflowPerShare(float cashflow, int shares) {
		cashflow_per_share = cashflow / shares;
		return;
	}

	public float getCashflow_per_share() {
		return cashflow_per_share;
	}
}
