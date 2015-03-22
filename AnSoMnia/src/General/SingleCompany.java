package General;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import Support.*;
import Interface.*;
import KPIToCalc.*;
import KPIToCrawl.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class SingleCompany implements ISaveAndDelete {
	
	@Id
	private String isin;
	
	private String company_name;
	
	private String ticker;
	
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<BuyPrice> buy_prices;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<SellPrice> sell_prices;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<StockPrice> stock_prices;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<Alpha> alphas;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<Beta> betas;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<BookValuePerShare> book_values_per_share;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<CashflowPerShare> cashflows_per_share;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<DebtRatio> debt_ratios;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<DividendPriceRatio> dividend_price_ratios;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<EarningsPerShare> earnings_per_share;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<EquityRatio> equity_ratios;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<GrossMargin> gross_margins;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<OperatingMargin> operating_margins;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<PayoutRatio> payout_ratios;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<PriceCashflowRatio> price_cashflow_ratios;
	
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<PriceEarningsRatio> price_earnings_ratios;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<PriceEarningsToGrowthRatio> price_earnings_to_growth_ratios;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<PriceSalesRatio> price_sales_ratios;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<PriceToBookValue> price_to_book_values;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<ReturnOnEquityRatio> return_on_equity_ratios;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<Sharpe> sharpes;
	
	
	public SingleCompany() {
		
	}
	
	public SingleCompany(String isin, String company_name, String ticker) {
		this.isin = isin;
		this.company_name = company_name;
		this.ticker = ticker;
		
		this.buy_prices = new ArrayList<BuyPrice>();
		this.sell_prices = new ArrayList<SellPrice>();
		this.stock_prices = new ArrayList<StockPrice>();
		
		this.alphas = new ArrayList<Alpha>();
		this.betas = new ArrayList<Beta>();
		this.book_values_per_share = new ArrayList<BookValuePerShare>();
		this.cashflows_per_share = new ArrayList<CashflowPerShare>();
		this.debt_ratios = new ArrayList<DebtRatio>();
		this.dividend_price_ratios = new ArrayList<DividendPriceRatio>();
		this.earnings_per_share = new ArrayList<EarningsPerShare>();
		this.equity_ratios = new ArrayList<EquityRatio>();
		this.gross_margins = new ArrayList<GrossMargin>();
		this.operating_margins = new ArrayList<OperatingMargin>();
		this.payout_ratios = new ArrayList<PayoutRatio>();
		this.price_cashflow_ratios = new ArrayList<PriceCashflowRatio>();
		this.price_earnings_ratios = new ArrayList<PriceEarningsRatio>();
		this.price_earnings_to_growth_ratios = new ArrayList<PriceEarningsToGrowthRatio>();
		this.price_sales_ratios = new ArrayList<PriceSalesRatio>();
		this.price_to_book_values = new ArrayList<PriceToBookValue>();
		this.return_on_equity_ratios = new ArrayList<ReturnOnEquityRatio>();
		this.sharpes = new ArrayList<Sharpe>();
	}
	
	public String getIsin() {
		return isin;
	}
	public void setIsin(String isin) {
		this.isin = isin;
	}
	public String getCompanyName() {
		return company_name;
	}
	public void setCompanyName(String company_name) {
		this.company_name = company_name;
	}
	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	
	public boolean addBuyPrice(BuyPrice bp) {
		boolean success = false;
		if (this.buy_prices.add(bp)){
			HibernateSupport.beginTransaction();
			success = bp.saveToDB();
			HibernateSupport.commitTransaction();
		}
		return success;
	}
	
	public boolean addSellPrice(SellPrice sp) {
		boolean success = false;
		if (this.sell_prices.add(sp)){
			HibernateSupport.beginTransaction();
			success = sp.saveToDB();
			HibernateSupport.commitTransaction();
		}
		return success;
	}
	
	public boolean addStockPrice(StockPrice sp) {
		boolean success = false;
		if (this.stock_prices.add(sp)){
			HibernateSupport.beginTransaction();
			success = sp.saveToDB();
			HibernateSupport.commitTransaction();
		}
		return success;
	}
	
	
	
	public boolean addPriceEarningsRatio(PriceEarningsRatio per) {
		boolean success = false;
		if (this.price_earnings_ratios.add(per)){
			HibernateSupport.beginTransaction();
			success = per.saveToDB();
			HibernateSupport.commitTransaction();
		}
		return success;
	}
	
	public boolean addEarningsPerShare(EarningsPerShare eps) {
		boolean success = false;
		if (this.earnings_per_share.add(eps)){
			HibernateSupport.beginTransaction();
			success = eps.saveToDB();
			HibernateSupport.commitTransaction();
		}
		return success;
	}
	
	public boolean addPriceEarningsToGrowthRatio(PriceEarningsToGrowthRatio peg) {
		boolean success = false;
		if (this.price_earnings_to_growth_ratios.add(peg)){
			HibernateSupport.beginTransaction();
			success = peg.saveToDB();
			HibernateSupport.commitTransaction();
		}
		return success;
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
