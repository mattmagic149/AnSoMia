package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class EarningsPerShare extends KeyPerformanceIndicator  {
	private double earnings_per_share;
		
	public EarningsPerShare() {
		super();
	}
	
	public EarningsPerShare(SingleCompany company, double earnings_per_share, Date date) {
		super(company, date);
		this.earnings_per_share = earnings_per_share;
	}
}
