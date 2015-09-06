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

import java.util.Date;
import java.util.List;

import interfaces.*;

import javax.persistence.*;

import utils.*;

/**
 * The Class CompanyInformation.
 */
@Entity
public class CompanyInformation implements ISaveAndDelete {
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long ci_id;
	
	private String name;
	
	private String dbpedia_link;
	
	private String company_homepage;
	
	@Column(length = 8000)
	private String dbpedia_content;
	
	@OneToMany
	@JoinColumn(name="info")
	private List<Company> companies;
	
	private Date date_added;
	
	public CompanyInformation(String name, String dbpedia_link, String dbpedia_content, 
			String company_homepage, Date date_added) {
		this.name = name;
		this.dbpedia_link = dbpedia_link;
		this.dbpedia_content = dbpedia_content;
		this.company_homepage = company_homepage;
		this.date_added = date_added;
	}
	
	public boolean addCompany(Company company) {
		boolean success = false;
		if(!this.companies.contains(company)) {
			if (this.companies.add(company)){
				success = company.saveToDB();
			}
		}
		return success;
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

	@Override
	public String serialize() {
		// TODO Auto-generated method stub
		return null;
	}

}
