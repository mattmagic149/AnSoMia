/*
 * @Author: Matthias Ivantsits
 * Supported by TU-Graz (KTI)
 * 
 * Tool, to gather market information, in quantitative and qualitative manner.
 * Copyright (C) 2015  Matthias Ivantsits
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package database;

import interfaces.ISaveAndDelete;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import utils.HibernateSupport;
import database.Company;

/**
 * The Class KeyPerformanceIndicator.
 */
@Entity
public class KeyPerformanceIndicator implements ISaveAndDelete {
	
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;

	/** The date. */
	private Date date;
	
	/** The revenue. */
	private long revenue; //Umsatz
	
	/** The number_of_employees. */
	private long number_of_employees;
	
	/** The number_of_shares. */
	private long number_of_shares;
	
	/** The operating_income. */
	private long operating_income;
	
	/** The earnings_before_taxes. */
	private long earnings_before_taxes;
	
	/** The earnings_after_taxes. */
	private long earnings_after_taxes;
	
	/** The balance_sheet_total. */
	private long balance_sheet_total; //Gesamtkapital
	
	/** The debt. */
	private long debt;
	
	/** The equity. */
	private long equity; //Eigenkapital
	
	/** The dividend. */
	private float dividend;
	
	/** The earnings_per_share. */
	private float earnings_per_share;
	
	/** The cashflow. */
	private long cashflow;
	
	/** The gross_profit. */
	private long gross_profit;
	
	/** The working_capital. */
	private long working_capital;

	/** The liquidity_1. */
	// KPIs to crawl
	private float liquidity_1;
	
	/** The liquidity_2. */
	private float liquidity_2;
	
	/** The liquidity_3. */
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


	/** The company. */
	@ManyToOne
	@JoinColumn(name="isin",updatable=false)
	protected Company company;
	
	/**
	 * Instantiates a new key performance indicator.
	 */
	public KeyPerformanceIndicator() {
		//this.date = new Date();
	}

	/**
	 * Instantiates a new key performance indicator.
	 *
	 * @param year the year
	 * @param company the company
	 */
	public KeyPerformanceIndicator(int year, Company company) {
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
	
	/**
	 * Instantiates a new key performance indicator.
	 *
	 * @param serialized_kpi the serialized_kpi
	 * @param company the company
	 */
	public KeyPerformanceIndicator(String[] serialized_kpi, Company company) {
		
		this.company = company;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			this.date = formatter.parse(serialized_kpi[1]);
		} catch (ParseException e) {
			e.printStackTrace();
			assert(false);
		}
		
		this.revenue = Long.parseLong(serialized_kpi[2]);
		this.number_of_employees = Long.parseLong(serialized_kpi[3]);
		this.number_of_shares = Long.parseLong(serialized_kpi[4]);
		this.operating_income = Long.parseLong(serialized_kpi[5]);
		this.earnings_before_taxes = Long.parseLong(serialized_kpi[6]);
		this.earnings_after_taxes = Long.parseLong(serialized_kpi[7]);
		this.balance_sheet_total = Long.parseLong(serialized_kpi[8]);
		this.debt = Long.parseLong(serialized_kpi[9]);
		this.equity = Long.parseLong(serialized_kpi[10]);
		this.dividend = Float.parseFloat(serialized_kpi[11]);
		this.earnings_per_share = Float.parseFloat(serialized_kpi[12]);
		this.cashflow = Long.parseLong(serialized_kpi[13]);
		this.gross_profit = Long.parseLong(serialized_kpi[14]);
		this.working_capital = Long.parseLong(serialized_kpi[15]);
		this.liquidity_1 = Float.parseFloat(serialized_kpi[16]);
		this.liquidity_2 = Float.parseFloat(serialized_kpi[17]);
		this.liquidity_3 = Float.parseFloat(serialized_kpi[18]);
		
	}
	
	
	/**
	 * Update kpis to calculate.
	 */
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
	
	/**
	 * Sets the profile values.
	 *
	 * @param dividend the dividend
	 * @param equity_ratio the equity_ratio
	 * @param liquidity_1 the liquidity_1
	 * @param liquidity_2 the liquidity_2
	 * @param liquidity_3 the liquidity_3
	 * @param number_of_employees the number_of_employees
	 * @param number_of_shares the number_of_shares
	 * @param working_capital the working_capital
	 */
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
	
	/**
	 * Sets the balance sheet values.
	 *
	 * @param revenue the revenue
	 * @param operating_income the operating_income
	 * @param earings_after_taxes the earings_after_taxes
	 * @param cashflow the cashflow
	 * @param equity the equity
	 * @param debt the debt
	 * @param balance_sheet_total the balance_sheet_total
	 * @param gross_profit the gross_profit
	 */
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
	
	/**
	 * Sets the finance values.
	 *
	 * @param revenue the revenue
	 * @param operating_income the operating_income
	 * @param earnings_after_taxes the earnings_after_taxes
	 * @param earnings_before_taxes the earnings_before_taxes
	 * @param equity the equity
	 * @param debt the debt
	 * @param balance_sheet_total the balance_sheet_total
	 * @param gross_profit the gross_profit
	 * @param number_of_employees the number_of_employees
	 * @param dividend the dividend
	 * @param earnings_per_share the earnings_per_share
	 */
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
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Gets the revenue.
	 *
	 * @return the revenue
	 */
	public long getRevenue() {
		return revenue;
	}

	/**
	 * Gets the number of employees.
	 *
	 * @return the number of employees
	 */
	public long getNumberOfEmployees() {
		return number_of_employees;
	}

	/**
	 * Gets the number of shares.
	 *
	 * @return the number of shares
	 */
	public long getNumberOfShares() {
		return number_of_shares;
	}

	/**
	 * Gets the operating income.
	 *
	 * @return the operating income
	 */
	public long getOperatingIncome() {
		return operating_income;
	}

	/**
	 * Gets the earnings before taxes.
	 *
	 * @return the earnings before taxes
	 */
	public long getEarningsBeforeTaxes() {
		return earnings_before_taxes;
	}

	/**
	 * Gets the earnings after taxes.
	 *
	 * @return the earnings after taxes
	 */
	public long getEarningsAfterTaxes() {
		return earnings_after_taxes;
	}

	/**
	 * Gets the balance sheet total.
	 *
	 * @return the balance sheet total
	 */
	public long getBalanceSheetTotal() {
		return balance_sheet_total;
	}

	/**
	 * Gets the debt.
	 *
	 * @return the debt
	 */
	public long getDebt() {
		return debt;
	}

	/**
	 * Gets the equity.
	 *
	 * @return the equity
	 */
	public long getEquity() {
		return equity;
	}

	/**
	 * Gets the dividend.
	 *
	 * @return the dividend
	 */
	public float getDividend() {
		return dividend;
	}

	/**
	 * Gets the earnings per share.
	 *
	 * @return the earnings per share
	 */
	public float getEarningsPerShare() {
		return earnings_per_share;
	}

	/**
	 * Gets the cashflow.
	 *
	 * @return the cashflow
	 */
	public long getCashflow() {
		return cashflow;
	}

	/**
	 * Gets the gross profit.
	 *
	 * @return the gross profit
	 */
	public long getGrossProfit() {
		return gross_profit;
	}

	/**
	 * Gets the working capital.
	 *
	 * @return the working capital
	 */
	public long getWorkingCapital() {
		return working_capital;
	}

	/**
	 * Gets the liquidity1.
	 *
	 * @return the liquidity1
	 */
	public float getLiquidity1() {
		return liquidity_1;
	}

	/**
	 * Gets the liquidity2.
	 *
	 * @return the liquidity2
	 */
	public float getLiquidity2() {
		return liquidity_2;
	}

	/**
	 * Gets the liquidity3.
	 *
	 * @return the liquidity3
	 */
	public float getLiquidity3() {
		return liquidity_3;
	}

	/**
	 * Gets the company.
	 *
	 * @return the company
	 */
	public Company getCompany() {
		return company;
	}

	
	/*private float calculateAlpha() {
		return Float.MIN_VALUE;
	}
	
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

	
	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	
	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#serialize()
	 */
	@Override
	public String serialize() {
		
		return this.company.getIsin() + "\t" + this.date + "\t" + this.revenue + "\t" + 
				this.number_of_employees + "\t" + this.number_of_shares + "\t" + 
				this.operating_income + "\t" + this.earnings_before_taxes + "\t" + 
				this.earnings_after_taxes + "\t" + this.balance_sheet_total + "\t" +
				this.debt + "\t" + this.equity + "\t" + this.dividend + "\t" + 
				this.earnings_per_share + "\t" + this.cashflow + "\t" + this.gross_profit + "\t" + 
				this.working_capital + "\t" + this.liquidity_1 + "\t" + this.liquidity_2 + "\t" + 
				this.liquidity_3;
	}
	
	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#saveToDB()
	 */
	@Override
	public boolean saveToDB() {
		if(!HibernateSupport.commit(this))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#deleteFromDB(java.lang.Object)
	 */
	@Override
	public void deleteFromDB(Object obj) {
		HibernateSupport.deleteObject(this);
	}
		
}
