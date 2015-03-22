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
	
	public PriceToBookValue(SingleCompany company, float price_to_book_value, Date date) {
		super(company, date);
		this.price_to_book_value = price_to_book_value;
	}

	public float getPrice_to_book_value() {
		return price_to_book_value;
	}
}
