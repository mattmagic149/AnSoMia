package database;

import interfaces.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import utils.*;

@Entity
public class IndustrySector implements ISaveAndDelete {
	
	@Id
	private String name;

	private String wallstreet_query_string;
	

	@ManyToMany
	@JoinTable(name="IndustrySectorToCompany",
				joinColumns={@JoinColumn(name="industry_name")}, 
				inverseJoinColumns={@JoinColumn(name="company_isin")})
	private List<Company> companies;

	public IndustrySector() {
		
	}
	
	public IndustrySector(String name, String wallstreet) {
		this.name = name;
		this.wallstreet_query_string = wallstreet;
		
		this.companies = new ArrayList<Company>();

	}
	
	public IndustrySector(String serialized_industry_sector) {
		String[] tmp = serialized_industry_sector.split("\t");
		this.name = tmp[0];
		this.wallstreet_query_string = tmp[1];
		
		this.companies = new ArrayList<Company>();
	}
	
	public List<Company> getCompanies() {
		return companies;
	}
	
	public String getName() {
		return name;
	}

	public String getWallstreetQueryString() {
		return wallstreet_query_string;
	}
	
	
	//*************************************************************************************************
	// addCompany:
	// adds a company to the companies_list.
	public boolean addCompany(Company company) {
		boolean success = false;
		
		if(company != null && !companies.contains(company)){	
			// add object to list and save to DB
			if (this.companies.add(company))
				success = this.saveToDB();
		}
		
		if (success){
			return true;
		} else {
			return false;
		}
	}

	public String serializeIndustrySector() {
		return this.name + "\t" + this.wallstreet_query_string;
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
