package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class PayoutRatio extends KeyPerformanceIndicator {

	private float payout_ratio;
	
	public PayoutRatio() {
		super();
	}
	
	public PayoutRatio(SingleCompany company, float dividends_per_share, float earnings_per_share , Date date) {
		super(company, date);
		calculatePayoutRatio(dividends_per_share, earnings_per_share);
	}
		
	private void calculatePayoutRatio(float dividends_per_share, float earnings_per_share) {
		payout_ratio = dividends_per_share / earnings_per_share;
		return;
	}

	public float getPayout_ratio() {
		return payout_ratio;
	}
}
