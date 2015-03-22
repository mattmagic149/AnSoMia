package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class EarningsPerShare extends KeyPerformanceIndicator  {
	
	private float earnings_per_share;
		
	public EarningsPerShare() {
		super();
	}
	
	public EarningsPerShare(SingleCompany company, float earnings, int shares, Date date) {
		super(company, date);
		calculateEarningsPerShare(earnings, shares);
	}
		
	private void calculateEarningsPerShare(float earnings, int shares) {
		earnings_per_share = earnings / shares;
		return;
	}

	public float getEarnings_per_share() {
		return earnings_per_share;
	}
}
