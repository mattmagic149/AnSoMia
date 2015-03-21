package General;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import Support.*;
import Interface.*;
import KeyPerformanceIndicators.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class SingleCompany implements ISaveAndDelete {
	
	@Id
	private String isin;
	
	private String company_name;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<PriceEarningsRatio> price_earnings_ratios;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<EarningsPerShare> earnings_per_shares;
	
	@OneToMany
	@JoinColumn(name="isin")
	private List<PriceEarningsToGrowthRatio> price_earnings_to_growth_ratios;
	
	
	
	
	public SingleCompany(String isin, String company_name){
		this.isin = isin;
		this.company_name = company_name;
		this.price_earnings_ratios = new ArrayList<PriceEarningsRatio>();
		this.earnings_per_shares = new ArrayList<EarningsPerShare>();
		this.price_earnings_to_growth_ratios = new ArrayList<PriceEarningsToGrowthRatio>();
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
		if (this.earnings_per_shares.add(eps)){
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
