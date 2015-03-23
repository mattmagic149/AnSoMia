package KPIToCalc;

import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class PriceToBookValue extends KeyPerformanceIndicator {
	
	private float price_to_book_value;
	
	public PriceToBookValue() {
		super();
	}
	
	public PriceToBookValue(SingleCompany company, float price, float book_value, int shares, Date date) {
		super(company, date);
		calculatePriceToBookValue(price, book_value, shares);
	}
		
	private void calculatePriceToBookValue(float price, float book_value, int shares) {
		price_to_book_value = price / (book_value / shares);
		return;
	}

	public float getPrice_to_book_value() {
		return price_to_book_value;
	}
}
