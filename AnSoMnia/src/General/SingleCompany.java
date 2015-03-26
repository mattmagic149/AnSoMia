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
	private List<MarketValues> market_values_list;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<KeyPerformanceIndicators> kpis_list;
	
	
	
	public SingleCompany() {
		
	}
	
	public SingleCompany(String isin, String company_name, String ticker) {
		this.isin = isin;
		this.company_name = company_name;
		this.ticker = ticker;
		
		this.market_values_list = new ArrayList<MarketValues>();
		this.kpis_list = new ArrayList<KeyPerformanceIndicators>();
		
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
	
	public boolean addMarketValues(MarketValues values) {
		boolean success = false;
		if (this.market_values_list.add(values)){
			HibernateSupport.beginTransaction();
			success = values.saveToDB();
			HibernateSupport.commitTransaction();
		}
		return success;
	}
	
	public boolean addKPIs(KeyPerformanceIndicators values) {
		boolean success = false;
		if (this.kpis_list.add(values)){
			HibernateSupport.beginTransaction();
			success = values.saveToDB();
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
