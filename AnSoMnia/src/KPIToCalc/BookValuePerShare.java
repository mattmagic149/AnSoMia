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
	
	public BookValuePerShare(SingleCompany company, float book_value_per_share, Date date) {
		super(company, date);
		this.book_value_per_share = book_value_per_share;
	}

	public float getBook_value_per_share() {
		return book_value_per_share;
	}
}
