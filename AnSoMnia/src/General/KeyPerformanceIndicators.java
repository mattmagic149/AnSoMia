package General;

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
	private long earnings_per_share;
	private long earnings_ratio;
	private long earnings_to_growth_ratio;
	private long price_ratio;
	private long cashflow_per_share;
	private long price_cash_flow_ratio;
	private long price_to_book_value;
	private long price_sales_ratio;
	//private long equity_ratio;
	private long debt_ratio;
	private long return_on_equity;
	private long payout_ratio;
	private long gross_margin;
	private long operating_margin;
	private long book_value_per_share;
	
	private long alpha;
	private long beta;
	private long sharpe;
	
	private long revenue;
	private long operating_income;
	private long net_income;
	private long dividend;
	private long outstanding_shares;
	private long cashflow;
	private long working_capital;
	private long book_value;
	private long equity_ratio;
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
