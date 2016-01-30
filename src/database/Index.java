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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import utils.*;

// TODO: Auto-generated Javadoc
/**
 * The Class Index.
 */
@Entity
@Table(name = "MarketIndex")
public class Index extends ShareInfo implements ISaveAndDelete {

	/** The companies. */
	@ManyToMany
	@JoinTable(name = "IndexToCompany", joinColumns = { @JoinColumn(name = "index_isin") }, inverseJoinColumns = { @JoinColumn(name = "company_isin") })
	private List<Company> companies;

	/**
	 * Instantiates a new index.
	 */
	public Index() {}

	/**
	 * Instantiates a new index.
	 *
	 * @param isin the isin
	 * @param index_name the index_name
	 * @param ticker the ticker
	 * @param wkn the wkn
	 * @param valor the valor
	 * @param wallstreet the wallstreet
	 * @param date_added the date_added
	 */
	public Index(String isin, String index_name, String ticker, String wkn,
			String valor, String wallstreet, Date date_added) {
		this.isin = isin;
		this.name = index_name;
		this.ticker = ticker;
		this.wkn = wkn;
		this.valor = valor;
		this.wallstreet_query_string = wallstreet;
		this.date_added = date_added;

		this.companies = new ArrayList<Company>();

	}

	/**
	 * Instantiates a new index.
	 *
	 * @param serialized_index the serialized_index
	 */
	public Index(String serialized_index) {

		String[] tmp = serialized_index.split("\t");
		this.isin = tmp[0];
		this.name = tmp[1];
		this.ticker = tmp[2];
		this.wkn = tmp[3];
		this.valor = tmp[4];
		this.wallstreet_query_string = tmp[5];
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			this.date_added = formatter.parse(tmp[6]);
		} catch (ParseException e) {
			e.printStackTrace();
			assert(false);
		}

		this.companies = new ArrayList<Company>();

	}
	/**
	 * Adds the company to the company list.
	 *
	 * @param company the company
	 * @return true, if successful
	 */
	public boolean addCompany(Company company) {
		boolean success = false;

		if (company != null && !companies.contains(company)) {
			// add object to list and save to DB
			if (this.companies.add(company))
				success = this.saveToDB();
		}

		return success;
	}

	/**
	 * Gets the companies.
	 *
	 * @return the companies
	 */
	public List<Company> getCompanies() {
		return companies;
	}

	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#serialize()
	 */
	@Override
	public String serialize() {
		return this.isin + "\t" + this.name + "\t" + this.ticker + "\t"
				+ this.wkn + "\t" + this.valor + "\t"
				+ this.wallstreet_query_string  + "\t" + this.date_added;
	}

	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#saveToDB()
	 */
	@Override
	public boolean saveToDB() {
		if (!HibernateSupport.commit(this))
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
