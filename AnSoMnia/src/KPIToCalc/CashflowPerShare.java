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
	
	public CashflowPerShare(SingleCompany company, float cashflow_per_share, Date date) {
		super(company, date);
		this.cashflow_per_share = cashflow_per_share;
	}

	public float getCashflow_per_share() {
		return cashflow_per_share;
	}
}
