package database;

import interfaces.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import utils.*;

@Entity
@Table(name = "MarketIndex")
public class Index extends ShareInfo implements ISaveAndDelete {

	@ManyToMany
	@JoinTable(name = "IndexToCompany", joinColumns = { @JoinColumn(name = "index_isin") }, inverseJoinColumns = { @JoinColumn(name = "company_isin") })
	private List<Company> companies;

	public Index() {

	}

	public Index(String isin, String index_name, String ticker, String wkn,
			String valor, String wallstreet) {
		this.isin = isin;
		this.name = index_name;
		this.ticker = ticker;
		this.wkn = wkn;
		this.valor = valor;
		this.wallstreet_query_string = wallstreet;

		this.companies = new ArrayList<Company>();

	}

	public Index(String serialized_index) {
		/*
		 * return this.isin + "\t" + this.name + "\t" + this.ticker + "\t" +
		 * this.wkn + "\t" + this.valor + "\t" + this.wallstreet_query_string;
		 */

		String[] tmp = serialized_index.split("\t");
		this.isin = tmp[0];
		this.name = tmp[1];
		this.ticker = tmp[2];
		this.wkn = tmp[3];
		this.valor = tmp[4];
		this.wallstreet_query_string = tmp[5];

		this.companies = new ArrayList<Company>();

	}

	// *************************************************************************************************
	// addCompany:
	// adds company to the companies_list.
	public boolean addCompany(Company company) {
		boolean success = false;

		if (company != null && !companies.contains(company)) {
			// add object to list and save to DB
			if (this.companies.add(company))
				success = this.saveToDB();
		}

		if (success) {
			return true;
		} else {
			return false;
		}
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public String serializeIndex() {

		return this.isin + "\t" + this.name + "\t" + this.ticker + "\t"
				+ this.wkn + "\t" + this.valor + "\t"
				+ this.wallstreet_query_string;
	}

	@Override
	public boolean saveToDB() {
		if (!HibernateSupport.commit(this))
			return false;
		return true;
	}

	@Override
	public void deleteFromDB(Object obj) {
		HibernateSupport.deleteObject(this);

	}

}
