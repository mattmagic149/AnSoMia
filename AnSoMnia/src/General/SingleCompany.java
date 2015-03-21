package General;
import javax.persistence.*;
import Support.*;
import Interface.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class SingleCompany implements ISaveAndDelete {
	
	@Id
	private String isin;
	private String company_name;
	
	
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
