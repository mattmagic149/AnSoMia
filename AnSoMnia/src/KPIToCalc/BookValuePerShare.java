package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class BookValuePerShare extends KeyPerformanceIndicator  {

	private float book_value_per_share;
		
	public BookValuePerShare() {
		super();		
	}
	
	public BookValuePerShare(SingleCompany company, float book_value, int shares, Date date) {
		super(company, date);
		calculateBookValuePerShare(book_value, shares);
	}
	
	private void calculateBookValuePerShare(float book_value, int shares) {
		book_value_per_share = book_value / shares;
		return;
	}

	public float getBook_value_per_share() {
		return book_value_per_share;
	}
}
