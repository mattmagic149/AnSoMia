package General;

import java.util.ArrayList;
import java.util.Calendar;
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
	private long balance_sheet_total;
	private long cashflow;
	private long debt;
	private float dividend;
	private long equity;
	private long gross_profit;
	private float liquidity_1;
	private float liquidity_2;
	private float liquidity_3;
	private long net_income;
	private int number_of_employees;
	private long operating_income;
	private long outstanding_shares;
	private long revenue;
	private long working_capital;
	
	// KPIs to calculate
	private float book_value_per_share;
	private float cashflow_per_share;
	private float debt_ratio;
	private float dividend_price_ratio;
	private float earnings_per_share;
	private float earnings_per_share_growth;
	private float equity_ratio;
	private float gross_margin;
	private float market_capitalisation;
	private float operating_margin;
	private float payout_ratio;
	private float price_cash_flow_ratio;
	private float price_earnings_ratio;
	private float price_earnings_to_growth_ratio;
	private float price_sales_ratio;
	private float price_to_book_value;
	private float return_on_equity;
	private float sharpe;
	private float alpha;
	private float beta;


	@ManyToOne
	@JoinColumn(name="isin",updatable=false)
	protected SingleCompany company;
	
	public KeyPerformanceIndicators() {
		//this.date = new Date();
	}

	public KeyPerformanceIndicators(int year, SingleCompany company) {
		Calendar c = Calendar.getInstance();
		c.set(year, 1, 1);
		this.date = c.getTime();
		this.company = company;
		
		this.balance_sheet_total = -1;
		this.cashflow = -1;
		this.debt = -1;
		this.dividend = -1;
		this.equity = -1;
		this.gross_profit = -1;
		this.liquidity_1 = -1;
		this.liquidity_2 = -1;
		this.liquidity_3 = -1;
		this.net_income = -1;
		this.number_of_employees = -1;	
		this.operating_income = -1;
		this.outstanding_shares = -1;
		this.revenue = -1;
		this.working_capital = -1;
		
		this.book_value_per_share = -1;
		this.cashflow_per_share = -1;
		this.debt_ratio = -1;
		this.dividend_price_ratio = -1;
		this.earnings_per_share = -1;
		this.earnings_per_share_growth = -1;
		this.equity_ratio = -1;
		this.gross_margin = -1;
		this.market_capitalisation = -1;
		this.operating_margin = -1;
		this.price_cash_flow_ratio = -1;
		this.price_earnings_ratio = -1;
		this.price_sales_ratio = -1;
		this.price_to_book_value = -1;
		this.return_on_equity = -1;
		
		// initialize derivative KPIs
		this.payout_ratio = -1;
		this.price_earnings_to_growth_ratio = -1;
		
		this.alpha = -1; 
		this.beta = -1; 
		this.sharpe = -1;
		
	}
	
	
	public void updateKpisToCalculate() {
		// initialize directly calculated KPIs
		this.book_value_per_share = this.calculateBookValuePerShare(this.equity, this.outstanding_shares);
		this.cashflow_per_share = this.calculateCashflowPerShare(this.cashflow, this.outstanding_shares);
		this.debt_ratio = this.calculateDebtRatio(this.debt, this.working_capital);
		//this.dividend_price_ratio = this.calculateDividendPriceRatio(this.dividend, price);
		this.earnings_per_share = this.calculateEarningsPerShare(this.net_income, this.outstanding_shares);
		//this.earnings_per_share_growth = this.calculateEarningsPerShareGrowth(this.net_income, past_net_income);
		this.equity_ratio = this.calculateEquityRatio(this.equity, this.working_capital);
		this.gross_margin = this.calculateGrossMargin(this.gross_profit, revenue);
		//this.market_capitalisation = this.calculateMarketCapitalization(price, this.outstanding_shares); 
		this.operating_margin = this.calculateOperatingMargin(this.operating_income, this.revenue);
		//this.price_cash_flow_ratio = this.calculatePriceCashflowRatio(price, this.cashflow);
		//this.price_earnings_ratio = this.calculatePriceEarningsRatio(price, this.net_income);
		//this.price_sales_ratio = this.calculatePriceSalesRatio(price, this.revenue, this.outstanding_shares);
		//this.price_to_book_value = this.calculatePriceToBookValue(price, this.equity, this.outstanding_shares);
		this.return_on_equity = this.calculateReturnOnEquityRatio(this.net_income, this.equity);
				
		this.payout_ratio = this.calculatePayoutRatio(this.dividend, this.earnings_per_share);
		this.price_earnings_to_growth_ratio = this.calculatePriceEarningsToGrowthRatio(this.price_earnings_ratio, this.earnings_per_share_growth);
				
		//this.alpha = this.calculateAlpha(); 
		//this.beta = this.calculateBeta(); 
		//this.sharpe = this.calculateSharpe();
	}
	
	public void setProfileValues(float dividend, float equity_ratio, 
			float liquidity_1, float liquidity_2, float liquidity_3, int number_of_employees, 
			long outstanding_shares, long working_capital) {
		
		if(dividend != -1)
			this.dividend = dividend;
		
		if(equity_ratio != -1)
			this.equity_ratio = equity_ratio;
		
		if(liquidity_1 != -1)
			this.liquidity_1 = liquidity_1;
		
		if(liquidity_2 != -1)
			this.liquidity_2 = liquidity_2;
		
		if(liquidity_3 != -1)
			this.liquidity_3 = liquidity_3;
		
		if(number_of_employees != -1)
			this.number_of_employees = number_of_employees;
		
		if(outstanding_shares != -1)
			this.outstanding_shares = outstanding_shares;
		
		if(working_capital != -1)
			this.working_capital = working_capital;
		
	}
	
	public void setBalanceSheetValues(long revenue, long operating_income, long net_income,
			long cashflow, long equity, long debt, long balance_sheet_total, long gross_profit) {
		
		if(revenue != -1)
			this.revenue = revenue;
		
		if(operating_income != -1)
			this.operating_income = operating_income;
		
		if(net_income != -1)
			this.net_income = net_income;
		
		if(cashflow != -1)
			this.cashflow = cashflow;

		if(equity != -1)
			this.equity = equity;
		
		if(debt != -1)
			this.debt = debt;
		
		if(balance_sheet_total != -1)
			this.balance_sheet_total = balance_sheet_total;
		
		if(gross_profit != -1)
			this.gross_profit = gross_profit;
		
	}
	
	public void setAllValuesIfNotSet() {
		
	}
	
	// TODO: Implement Alpha
	private float calculateAlpha() {
		return -1.0f;
	}
	
	// TODO: Implement Beta
	private float calculateBeta() {
		return -1.0f;
	}
	
	private float calculateBookValuePerShare(long book_value, long shares) {
		return ((float)book_value / shares);
	}

	private float calculateCashflowPerShare(long cashflow, long shares) {
		return ((float)cashflow / shares);
	}
	
	private float calculateDebtRatio(long debt, long total) {
		return ((float)debt / total);
	}
	
	private float calculateDividendPriceRatio(float dividend, float price) {
		return ((float)dividend / price);
	}
	
	private float calculateEarningsPerShare(long earnings, long shares) {
		return ((float)earnings / shares);
	}
	
	private float calculateEarningsPerShareGrowth(float current_earnings, float past_earnings) {
		return ((float)current_earnings / past_earnings);
	}
	
	private float calculateEquityRatio(long equity, long total) {
		return ((float)equity / total);
	}
	
	private float calculateGrossMargin(long gross, long sales) {
		return ((float)gross / sales);
	}
	
	private float calculateMarketCapitalization(float price, int shares) {
		return (price * shares);
	}
	
	private float calculateOperatingMargin(long operating_income, long sales) {
		return ((float)operating_income / sales);
	}
	
	private float calculatePayoutRatio(float dividends_per_share, float earnings_per_share) {
		return ((float)dividends_per_share / earnings_per_share);
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
	
	private float calculateReturnOnEquityRatio(long net, long equity) {
		System.out.println("return_on_equity = " + ((float)net / equity));
		return ((float)net / equity);
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
	
	public Date getDate() {
		return date;
	}
	
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
