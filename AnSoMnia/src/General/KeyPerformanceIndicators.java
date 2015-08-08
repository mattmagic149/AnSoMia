package General;

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
	
	private long revenue; //Umsatz
	private long number_of_employees;
	private long number_of_shares;
	private long operating_income;
	private long earnings_before_taxes;
	private long earnings_after_taxes;
	private long balance_sheet_total; //Gesamtkapital
	private long debt;
	private long equity; //Eigenkapital
	private float dividend;
	private float earnings_per_share;
	private long cashflow;
	private long gross_profit;
	private long working_capital;

	// KPIs to crawl
	private float liquidity_1;
	private float liquidity_2;
	private float liquidity_3;
	
	// KPIs to calculate
	/*private float book_value_per_share;
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
	private float beta;*/


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
		
		this.balance_sheet_total = Long.MIN_VALUE;
		this.cashflow = Long.MIN_VALUE;
		this.debt = Long.MIN_VALUE;
		this.dividend = Float.MIN_VALUE;
		this.equity = Long.MIN_VALUE;
		this.gross_profit = Long.MIN_VALUE;
		this.liquidity_1 = Float.MIN_VALUE;
		this.liquidity_2 = Float.MIN_VALUE;
		this.liquidity_3 = Float.MIN_VALUE;
		this.earnings_after_taxes = Long.MIN_VALUE;
		this.number_of_employees = Long.MIN_VALUE;	
		this.operating_income = Long.MIN_VALUE;
		this.number_of_shares = Long.MIN_VALUE;
		this.revenue = Long.MIN_VALUE;
		this.working_capital = Long.MIN_VALUE;
		
		
	}
	
	
	public void updateKpisToCalculate() {
		// initialize directly calculated KPIs
		//this.book_value_per_share = this.calculateBookValuePerShare(this.equity, this.number_of_shares);
		//this.cashflow_per_share = this.calculateCashflowPerShare(this.cashflow, this.number_of_shares);
		//this.debt_ratio = this.calculateDebtRatio(this.debt, this.working_capital);
		//this.dividend_price_ratio = this.calculateDividendPriceRatio(this.dividend, price);
		//this.earnings_per_share = this.calculateEarningsPerShare(this.earings_after_taxes, this.number_of_shares);
		//this.earnings_per_share_growth = this.calculateEarningsPerShareGrowth(this.earings_after_taxes, past_net_income);
		//this.equity_ratio = this.calculateEquityRatio(this.equity, this.working_capital);
		//this.gross_margin = this.calculateGrossMargin(this.gross_profit, revenue);
		//this.market_capitalisation = this.calculateMarketCapitalization(price, this.outstanding_shares); 
		//this.operating_margin = this.calculateOperatingMargin(this.operating_income, this.revenue);
		//this.price_cash_flow_ratio = this.calculatePriceCashflowRatio(price, this.cashflow);
		//this.price_earnings_ratio = this.calculatePriceEarningsRatio(price, this.earings_after_taxes);
		//this.price_sales_ratio = this.calculatePriceSalesRatio(price, this.revenue, this.outstanding_shares);
		//this.price_to_book_value = this.calculatePriceToBookValue(price, this.equity, this.outstanding_shares);
		//this.return_on_equity = this.calculateReturnOnEquityRatio(this.earings_after_taxes, this.equity);
				
		//this.payout_ratio = this.calculatePayoutRatio(this.dividend, this.earnings_per_share);
		//this.price_earnings_to_growth_ratio = this.calculatePriceEarningsToGrowthRatio(this.price_earnings_ratio, this.earnings_per_share_growth);
				
		//this.alpha = this.calculateAlpha(); 
		//this.beta = this.calculateBeta(); 
		//this.sharpe = this.calculateSharpe();
	}
	
	public void setProfileValues(float dividend, float equity_ratio, 
			float liquidity_1, float liquidity_2, float liquidity_3, long number_of_employees, 
			long number_of_shares, long working_capital) {
		
		if(dividend != Float.MIN_VALUE)
			this.dividend = dividend;
		
		if(liquidity_1 != Float.MIN_VALUE)
			this.liquidity_1 = liquidity_1;
		
		if(liquidity_2 != Float.MIN_VALUE)
			this.liquidity_2 = liquidity_2;
		
		if(liquidity_3 != Float.MIN_VALUE)
			this.liquidity_3 = liquidity_3;
		
		if(number_of_employees != -1)
			this.number_of_employees = number_of_employees;
		
		if(number_of_shares != -1)
			this.number_of_shares = number_of_shares;
		
		if(working_capital != -1)
			this.working_capital = working_capital;
		
	}
	
	public void setBalanceSheetValues(long revenue, long operating_income, long earings_after_taxes,
			long cashflow, long equity, long debt, long balance_sheet_total, long gross_profit) {
		
		if(revenue != Long.MIN_VALUE)
			this.revenue = revenue;
		
		if(operating_income != Long.MIN_VALUE)
			this.operating_income = operating_income;
		
		if(earings_after_taxes != Long.MIN_VALUE)
			this.earnings_after_taxes = earings_after_taxes;
		
		if(cashflow != Long.MIN_VALUE)
			this.cashflow = cashflow;

		if(equity != Long.MIN_VALUE)
			this.equity = equity;
		
		if(debt != Long.MIN_VALUE)
			this.debt = debt;
		
		if(balance_sheet_total != Long.MIN_VALUE)
			this.balance_sheet_total = balance_sheet_total;
		
		if(gross_profit != Long.MIN_VALUE)
			this.gross_profit = gross_profit;
		
	}
	
	public void setFinanceValues(long revenue, long operating_income, long earnings_after_taxes, 
			long earnings_before_taxes, long equity, long debt, long balance_sheet_total, 
			long gross_profit, long number_of_employees, float dividend, float earnings_per_share) {
		
		if(revenue != Long.MIN_VALUE)
			this.revenue = revenue;
		
		if(operating_income != Long.MIN_VALUE)
			this.operating_income = operating_income;
		
		if(earnings_after_taxes != Long.MIN_VALUE)
			this.earnings_after_taxes = earnings_after_taxes;
		
		if(earnings_before_taxes != Long.MIN_VALUE)
			this.earnings_before_taxes = earnings_before_taxes;
		
		if(equity != Long.MIN_VALUE)
			this.equity = equity;
		
		if(debt != Long.MIN_VALUE)
			this.debt = debt;
		
		if(balance_sheet_total != Long.MIN_VALUE)
			this.balance_sheet_total = balance_sheet_total;
		
		if(gross_profit != Long.MIN_VALUE)
			this.gross_profit = gross_profit;
		
		if(number_of_employees != Long.MIN_VALUE)
			this.number_of_employees = number_of_employees;
		
		if(dividend != Float.MIN_VALUE)
			this.dividend = dividend;
		
		if(earnings_per_share != Float.MIN_VALUE)
			this.earnings_per_share = earnings_per_share;
		
	}
	
	public void setAllValuesIfNotSet() {
		
	}
	
	/*
	// TODO: Implement Alpha
	private float calculateAlpha() {
		return Float.MIN_VALUE;
	}
	
	// TODO: Implement Beta
	private float calculateBeta() {
		return Float.MIN_VALUE;
	}
	
	private float calculateBookValuePerShare(long book_value, long shares) {
		if(book_value == Long.MIN_VALUE || shares == Long.MIN_VALUE)
			return Float.MIN_VALUE;
		
		return ((float)book_value / shares);
	}

	private float calculateCashflowPerShare(long cashflow, long shares) {
		if(cashflow == Long.MIN_VALUE || shares == Long.MIN_VALUE)
			return Float.MIN_VALUE;
		
		return ((float)cashflow / shares);
	}
	
	private float calculateDebtRatio(long debt, long total) {
		if(debt == Long.MIN_VALUE || total == Long.MIN_VALUE)
			return Float.MIN_VALUE;
		
		return ((float)debt / total);
	}
	
	private float calculateDividendPriceRatio(float dividend, float price) {
		return ((float)dividend / price);
	}
	
	private float calculateEarningsPerShare(long earnings, long shares) {
		if(earnings == Long.MIN_VALUE || shares == Long.MIN_VALUE)
			return Float.MIN_VALUE;
		
		return ((float)earnings / shares);
	}
	
	private float calculateEarningsPerShareGrowth(float current_earnings, float past_earnings) {
		return ((float)current_earnings / past_earnings);
	}
	
	private float calculateEquityRatio(long equity, long total) {
		if(equity == Long.MIN_VALUE || total == Long.MIN_VALUE)
			return Float.MIN_VALUE;
		
		return ((float)equity / total);
	}
	
	private float calculateGrossMargin(long gross, long sales) {
		if(gross == Long.MIN_VALUE || sales == Long.MIN_VALUE)
			return Float.MIN_VALUE;
		
		return ((float)gross / sales);
	}
	
	private float calculateMarketCapitalization(float price, long shares) {
		return (price * shares);
	}
	
	private float calculateOperatingMargin(long operating_income, long sales) {
		if(operating_income == Long.MIN_VALUE || sales == Long.MIN_VALUE)
			return Float.MIN_VALUE;
		
		return ((float)operating_income / sales);
	}
	
	private float calculatePayoutRatio(float dividends_per_share, float earnings_per_share) {
		if(dividends_per_share == Float.MIN_VALUE || earnings_per_share == Float.MIN_VALUE)
			return Float.MIN_VALUE;
		
		return ((float)dividends_per_share / earnings_per_share);
	}
	
	private float calculatePriceCashflowRatio(float price, float cashflow) {
		return (price / cashflow);
	}
	
	private float calculatePriceEarningsRatio(float price, float earnings) {
		return (price / earnings);
	}
	
	private float calculatePriceEarningsToGrowthRatio(float price_earnings_ratio, float eps_growth) {
		if(price_earnings_ratio == Float.MIN_VALUE || eps_growth == Float.MIN_VALUE)
			return Float.MIN_VALUE;
		
		return (price_earnings_ratio / eps_growth);
	}
	
	private float calculatePriceSalesRatio(float price, float sales, long shares) {
		return (price / (sales / shares));
	}
	
	private float calculatePriceToBookValue(float price, float book_value, long shares) {
		return (price / (book_value / shares));
	}
	
	private float calculateReturnOnEquityRatio(long net, long equity) {
		if(net == Long.MIN_VALUE || equity == Long.MIN_VALUE)
			return Float.MIN_VALUE;
		
		return ((float)net / equity);
	}
	
	
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
