package KPIToCalc;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;

import General.SingleCompany;
import Interface.KeyPerformanceIndicator;

@Entity
public class Sharpe extends KeyPerformanceIndicator {
	private float sharpe;
	
	public Sharpe() {
		super();
	}
	
	public Sharpe(SingleCompany company, ArrayList<Float> monthly_return, ArrayList<Float> riskfree,  Date date) {
		super(company, date);
		calculateSharpe(monthly_return, riskfree);
	}
		
	private void calculateSharpe(ArrayList<Float> monthly_return, ArrayList<Float> riskfree) {
		int last = monthly_return.size() - 1;
		float average_return;
		float volatility;
		float temp = 0;
		for(int i = last; i >= 0 && i >= monthly_return.size()-12; i--) {
			temp += monthly_return.get(i);
		}
		average_return = temp / monthly_return.size();
		
		temp = 0;
		
		for(int i = last; i >= 0 && i >= monthly_return.size()-12; i--) {
			temp += (monthly_return.get(i) - average_return) * (monthly_return.get(i) - average_return);
		}
		if(monthly_return.size() >= 12) {
			temp /= 11;
		} else {
			temp /= monthly_return.size() - 1;
		}
		volatility = (float) Math.sqrt(temp);
		
		sharpe = average_return / volatility;
		return;
	}

	public float getSharpe() {
		return sharpe;
	}

	public static void main( String[] args ) {
		ArrayList<Float> returns = new ArrayList<Float>();
		returns.add(1.1f);
		returns.add(1.2f);
		returns.add(1.3f);

		ArrayList<Float> riskfree = new ArrayList<Float>();
		riskfree.add(1.0f);
		riskfree.add(1.0f);
		riskfree.add(1.0f);
		
		SingleCompany company = new SingleCompany();
		Date date = new Date();
		
		Sharpe test = new Sharpe(company, returns, riskfree, date);
		System.out.println(test.sharpe);
	}
}
