package General;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import General.SingleCompany;
import Interface.ISaveAndDelete;
import Support.HibernateSupport;

@Entity
public class KeyPerformanceIndicators implements ISaveAndDelete {
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	
	private Date date;
	private float earnings_per_share;
	private float earnings_per_share_growth; ///TODO: class EarningsPerShareGrowth existed, but it was not included in the kpi.txt file???
	private long earnings_ratio;
	private float price_earnings_ratio; ///TODO: class PriceEarningsRatio existed, but it was not included in the kpi.txt file???
	private long earnings_to_growth_ratio;
	private float price_earnings_to_growth_ratio; ///TODO: class PriceEarningsToGrowthRatio existed, but it was not included in the kpi.txt file???
	private long price_ratio;
	private float cashflow_per_share;
	private float price_cash_flow_ratio;
	private float price_to_book_value;
	private float price_sales_ratio;
	//private long equity_ratio;
	private float debt_ratio;
	private float return_on_equity;
	private float payout_ratio;
	private float gross_margin;
	private float operating_margin;
	private float book_value_per_share;
	private float dividend_price_ratio; ///TODO: class DividendPriceRatio existed, but it was not included in the kpi.txt file???
	
	private long alpha;
	private long beta;
	private float sharpe;
	
	private long revenue;
	private long operating_income;
	private long net_income;
	private long dividend;
	private long outstanding_shares;
	private long cashflow;
	private long working_capital;
	private long book_value;
	private float equity_ratio;
	private long equity;
	private long debt;
	private long balance_sheet_total;
	private long market_capitalisation;
	private long gross_profit;
	private long liquidity;
	private long number_of_employees;
	
	
	@ManyToOne
	@JoinColumn(name="isin",updatable=false)
	protected SingleCompany company;
	
	public KeyPerformanceIndicators() {
		this.date = new Date();
	}
	
	private float calculateBookValuePerShare(float book_value, int shares) {
		return (book_value / shares);
	}

	private float calculateCashflowPerShare(float cashflow, int shares) {
		return (cashflow / shares);
	}
	
	private float calculateDebtRatio(float debt, int total) {
		return (debt / total);
	}
	
	private float calculateDividendPriceRatio(float dividend, float price) {
		return (dividend / price);
	}
	
	private float calculateEarningsPerShare(float earnings, int shares) {
		return (earnings / shares);
	}
	
	private float calculateEarningsPerShareGrowth(float current_earnings, int past_earnings) {
		return (current_earnings / past_earnings);
	}
	
	private float calculateEquityRatio(float equity, float total) {
		return (equity / total);
	}
	
	private float calculateGrossMargin(float gross, float sales) {
		return (gross / sales);
	}
	
	private float calculateOperatingMargin(float operating_income, float sales) {
		return (operating_income / sales);
	}
	
	private float calculatePayoutRatio(float dividends_per_share, float earnings_per_share) {
		return (dividends_per_share / earnings_per_share);
	}
	
	private float calculatePriceCashflowRatio(float price, float cashflow) {
		return (price / cashflow);
	}
	
	private float calculatePriceEarningsRatio(float price, float earnings) {
		return (price / earnings);
	}
	
	private float calculatePriceEarningsToGrowthRatio(float price_earnings_ratio, float eps_growth) {
		return (price_earnings_ratio / eps_growth);
	}
	
	private float calculatePriceSalesRatio(float price, float sales, int shares) {
		return (price / (sales / shares));
	}
	
	private float calculatePriceToBookValue(float price, float book_value, int shares) {
		return (price / (book_value / shares));
	}
	
	private float calculateReturnOnEquityRatio(float net, float equity) {
		return (net / equity);
	}
	
	private float calculateSharpe(ArrayList<Float> monthly_return, ArrayList<Float> riskfree) {
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
		
		return (average_return / volatility);
	}
	
	/*public static void main( String[] args ) {
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
	}*/
	
	@Override
	public boolean saveToDB() {
		if(!HibernateSupport.commit(this))
			return false;
		return true;
	}

	@Override
	public void deleteFromDB(Object obj) {
		HibernateSupport.deleteObject(this);
	}
		
}
