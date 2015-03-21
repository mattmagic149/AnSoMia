import javax.persistence.*;

public class SingleCompany implements Interface.ISaveAndDelete {
	
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
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void deleteFromDB() {
		// TODO Auto-generated method stub
		
	}

}
