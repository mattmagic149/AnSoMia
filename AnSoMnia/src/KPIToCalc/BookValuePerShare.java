package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class BookValuePerShare extends KeyPerformanceIndicator  {

	private double book_value_per_share;
		
	public BookValuePerShare() {
		super();
	}
	
	public BookValuePerShare(SingleCompany company, double book_value_per_share, Date date) {
		super(company, date);
		this.book_value_per_share = calculateBookValuePerShare();
	}
	
	private double calculateBookValuePerShare() {
		return 1.0;
	}
	

}
