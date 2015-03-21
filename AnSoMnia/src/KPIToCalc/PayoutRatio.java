package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class PayoutRatio extends KeyPerformanceIndicator {

	private double payout_ratio;
	
	public PayoutRatio() {
		super();
	}
	
	public PayoutRatio(SingleCompany company, double payout_ratio, Date date) {
		super(company, date);
		this.payout_ratio = payout_ratio;
	}
}
