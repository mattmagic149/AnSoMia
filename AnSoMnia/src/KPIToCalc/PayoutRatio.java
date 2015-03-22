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
	
	public PayoutRatio(SingleCompany company, float payout_ratio, Date date) {
		super(company, date);
		this.payout_ratio = payout_ratio;
	}

	public float getPayout_ratio() {
		return payout_ratio;
	}
}
