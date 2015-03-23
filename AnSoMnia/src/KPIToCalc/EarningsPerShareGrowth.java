package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class EarningsPerShareGrowth extends KeyPerformanceIndicator  {
	
	private float earnings_per_share_growth;
		
	public EarningsPerShareGrowth() {
		super();
	}
	
	public EarningsPerShareGrowth(SingleCompany company, float current_earnings, int past_earnings, Date date) {
		super(company, date);
		calculateEarningsPerShareGrowth(current_earnings, past_earnings);
	}
		
	private void calculateEarningsPerShareGrowth(float current_earnings, int past_earnings) {
		earnings_per_share_growth = current_earnings / past_earnings;
		return;
	}

	public float getEarnings_per_share() {
		return earnings_per_share_growth;
	}
}
