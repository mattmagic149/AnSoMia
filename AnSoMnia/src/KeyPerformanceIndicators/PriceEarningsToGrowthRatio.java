package KeyPerformanceIndicators;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import General.SingleCompany;
import Interface.ISaveAndDelete;
//import Support.HibernateSupport;
import Support.HibernateSupport;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PriceEarningsToGrowthRatio implements ISaveAndDelete  {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int eps_id;
	
	@ManyToOne
	@JoinColumn(name="isin",updatable=false)
	private SingleCompany company;
	
	private double price_earnings_to_growth_ratio;
	
	private Date date;
	
	public PriceEarningsToGrowthRatio(SingleCompany company, double price_earnings_to_growth_ratio, Date date){
		this.company = company;
		this.price_earnings_to_growth_ratio = price_earnings_to_growth_ratio;
		this.date = date;
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