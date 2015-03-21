package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class CashflowPerShare extends KeyPerformanceIndicator {
	private double cashflow_per_share;
	
	public CashflowPerShare() {
		super();
	}
	
	public CashflowPerShare(SingleCompany company, double cashflow_per_share, Date date) {
		super(company, date);
		this.cashflow_per_share = cashflow_per_share;
	}
}
