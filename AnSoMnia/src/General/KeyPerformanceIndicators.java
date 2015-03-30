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
	
	// KPIs to crawl
	private float balance_sheet_total;
	private float cashflow;
	private float debt;
	private float dividend;
	private float equity;
	private float gross_profit;
	private float liquidity;
	private float net_income;
	private long number_of_employees;
	private float operating_income;
	private long outstanding_shares;
	private float revenue;
	private float working_capital;
	
	// KPIs to calculate
	//private float alpha;
	//private float beta;
	private float book_value_per_share;
	private float cashflow_per_share;
	private float debt_ratio;
	private float dividend_price_ratio; ///TODO: class DividendPriceRatio existed, but it was not included in the kpi.txt file???
	private float earnings_per_share;
	private float earnings_per_share_growth; // Is NOW included in kpi.txt (at the top, with all the other KPIs to calculate)
	private float equity_ratio;
	private float gross_margin;
	private float market_capitalisation;
	private float operating_margin;
	private float payout_ratio;
	private float price_cash_flow_ratio;
	private float price_earnings_ratio; // Already is included in kpi.txt (at the top, with all the other KPIs to calculate)
	private float price_earnings_to_growth_ratio; // Already is included in kpi.txt (at the top, with all the other KPIs to calculate)
	private float price_sales_ratio;
	private float price_to_book_value;
	private float return_on_equity;
	//private float sharpe;
	
	// TODO: categorize or delete
	private long earnings_ratio; // what da fuck is dat?
	private long earnings_to_growth_ratio; // what da fuck is dat?
	private long price_ratio; // what da fuck is dat?

	@ManyToOne
	@JoinColumn(name="isin",updatable=false)
	protected SingleCompany company;
	
	public KeyPerformanceIndicators() {
		this.date = new Date();
	}
	
	public KeyPerformanceIndicators(long total, long equity, long cashflow, long debt, long dividend, long gross_profit,long liquidity,
			long net_income, int number_of_employees, long operating_income, int shares, long revenue, long working_capital, long price, long past_net_income) {
		
		// initialize date
		this.date = new Date();
		
		// initialize crawled KPIs
		this.balance_sheet_total = total;
		this.cashflow = cashflow;
		this.debt = debt;
		this.dividend = dividend;
		this.equity = equity;
		this.gross_profit = gross_profit;
		this.liquidity = liquidity;
		this.net_income = net_income;
		this.number_of_employees = number_of_employees;	
		this.operating_income = operating_income;
		this.outstanding_shares = shares;
		this.revenue = revenue;
		this.working_capital = working_capital;
		
		// initialize directly calculated KPIs
		this.book_value_per_share = this.calculateBookValuePerShare(equity, shares);
		this.cashflow_per_share = this.calculateCashflowPerShare(cashflow, shares);
		this.debt_ratio = this.calculateDebtRatio(debt, total);
		this.dividend_price_ratio = this.calculateDividendPriceRatio(dividend, price);
		this.earnings_per_share = this.calculateEarningsPerShare(net_income, shares);
		this.earnings_per_share_growth = this.calculateEarningsPerShareGrowth(net_income, past_net_income);
		this.equity_ratio = this.calculateEquityRatio(equity, total);
		this.gross_margin = this.calculateGrossMargin(gross_profit, revenue);
		this.market_capitalisation = this.calculateMarketCapitalization(price, shares); 
		this.operating_margin = this.calculateOperatingMargin(operating_income, revenue);
		this.price_cash_flow_ratio = this.calculatePriceCashflowRatio(price, cashflow);
		this.price_earnings_ratio = this.calculatePriceEarningsRatio(price, net_income);
		this.price_sales_ratio = this.calculatePriceSalesRatio(price, revenue, shares);
		this.price_to_book_value = this.calculatePriceToBookValue(price, equity, shares);
		this.return_on_equity = this.calculateReturnOnEquityRatio(net_income, equity);
		
		// initialize derivative KPIs
		this.payout_ratio = this.calculatePayoutRatio(dividend, earnings_per_share);
		this.price_earnings_to_growth_ratio = this.calculatePriceEarningsToGrowthRatio(price_earnings_ratio, earnings_per_share_growth);
		/*
		 * this.alpha = this.calculateAlpha(); 
		 * this.beta = this.calculateBeta(); 
		 * this.sharpe = this.calculateSharpe();
		 */
		
	}
	
	/*
	// TODO: Implement Alpha
	private float calculateAlpha() {
		return -1.0f;
	}
	*/
	
	/*
	// TODO: Implement Beta
	private float calculateBeta() {
		return -1.0f;
	}
	*/
	
	private float calculateBookValuePerShare(float book_value, int shares) {
		return (book_value / shares);
	}

	private float calculateCashflowPerShare(float cashflow, int shares) {
		return (cashflow / shares);
	}
	
	private float calculateDebtRatio(float debt, float total) {
		return (debt / total);
	}
	
	private float calculateDividendPriceRatio(float dividend, float price) {
		return (dividend / price);
	}
	
	private float calculateEarningsPerShare(float earnings, int shares) {
		return (earnings / shares);
	}
	
	private float calculateEarningsPerShareGrowth(float current_earnings, float past_earnings) {
		return (current_earnings / past_earnings);
	}
	
	private float calculateEquityRatio(float equity, float total) {
		return (equity / total);
	}
	
	private float calculateGrossMargin(float gross, float sales) {
		return (gross / sales);
	}
	
	private float calculateMarketCapitalization(float price, int shares) {
		return (price * shares);
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
	
	/*
	// TODO: Implement Sharpe without ArrayLists monthly_return and riskfree as parameters
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
	*/
	
	/*
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
	*/
	
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
