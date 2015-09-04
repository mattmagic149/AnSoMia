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

import interfaces.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import utils.*;

/**
 * The Class IndustrySector.
 */
@Entity
public class IndustrySector implements ISaveAndDelete {
	
	/** The name. */
	@Id
	private String name;

	/** The wallstreet_query_string. */
	private String wallstreet_query_string;
	

	/** The companies. */
	@ManyToMany
	@JoinTable(name="IndustrySectorToCompany",
				joinColumns={@JoinColumn(name="industry_name")}, 
				inverseJoinColumns={@JoinColumn(name="company_isin")})
	private List<Company> companies;

	/**
	 * Instantiates a new industry sector.
	 */
	public IndustrySector() {
		
	}
	
	/**
	 * Instantiates a new industry sector.
	 *
	 * @param name the name
	 * @param wallstreet the wallstreet
	 */
	public IndustrySector(String name, String wallstreet) {
		this.name = name;
		this.wallstreet_query_string = wallstreet;
		
		this.companies = new ArrayList<Company>();

	}
	
	/**
	 * Instantiates a new industry sector.
	 *
	 * @param serialized_industry_sector the serialized_industry_sector
	 */
	public IndustrySector(String serialized_industry_sector) {
		String[] tmp = serialized_industry_sector.split("\t");
		this.name = tmp[0];
		this.wallstreet_query_string = tmp[1];
		
		this.companies = new ArrayList<Company>();
	}
	
	/**
	 * Gets the companies.
	 *
	 * @return the companies
	 */
	public List<Company> getCompanies() {
		return companies;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the wallstreet query string.
	 *
	 * @return the wallstreet query string
	 */
	public String getWallstreetQueryString() {
		return wallstreet_query_string;
	}
	
	/**
	 * Adds the company.
	 *
	 * @param company the company
	 * @return true, if successful
	 */
	public boolean addCompany(Company company) {
		boolean success = false;
		
		if(company != null && !companies.contains(company)){	
			// add object to list and save to DB
			if (this.companies.add(company))
				success = this.saveToDB();
		}
		
		return success;
	}

	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#serialize()
	 */
	@Override
	public String serialize() {
		return this.name + "\t" + this.wallstreet_query_string;
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
