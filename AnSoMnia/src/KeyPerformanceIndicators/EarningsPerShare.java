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
public class EarningsPerShare implements ISaveAndDelete  {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int eps_id;
	
	@ManyToOne
	@JoinColumn(name="isin",updatable=false)
	private SingleCompany company;
	
	private double earnings_per_share;
	
	private Date date;
	
	public EarningsPerShare(SingleCompany company, double earnings_per_share, Date date){
		this.company = company;
		this.earnings_per_share = earnings_per_share;
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
