package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class Alpha extends KeyPerformanceIndicator {
	private double alpha;
	
	public Alpha() {
		super();
	}
	
	public Alpha(SingleCompany company, double alpha, Date date) {
		super(company, date);
		this.alpha = alpha;
	}
}
