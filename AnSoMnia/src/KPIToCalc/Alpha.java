package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class Alpha extends KeyPerformanceIndicator {
	
	private float alpha;
	
	public Alpha() {
		super();
	}
	
	public Alpha(SingleCompany company, float alpha, Date date) {
		super(company, date);
		this.alpha = alpha;
	}

	public float getAlpha() {
		return alpha;
	}
}
