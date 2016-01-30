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

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

// TODO: Auto-generated Javadoc
/**
 * The Class ShareInfo.
 */
@MappedSuperclass
public abstract class ShareInfo {

	/** The isin. */
	@Id
	protected String isin;
	
	/** The name. */
	protected String name;
	
	/** The ticker. */
	protected String ticker;
	
	/** The wkn. */
	protected String wkn;
		
	/** The valor. */
	protected String valor;
	
	/** The wallstreet_query_string. */
	protected String wallstreet_query_string;
	
	/** The finance_query_string. */
	protected String finance_query_string;
	
	/** The date_added. */
	protected Date date_added;
	
	/**
	 * Gets the finance query string.
	 *
	 * @return the finance query string
	 */
	public String getFinanceQueryString() {
		return finance_query_string;
	}

	/**
	 * Sets the finance query string.
	 *
	 * @param finance_query_string the new finance query string
	 */
	public void setFinanceQueryString(String finance_query_string) {
		this.finance_query_string = finance_query_string;
	}
	
	/**
	 * Gets the isin.
	 *
	 * @return the isin
	 */
	public String getIsin() {
		return isin;
	}
	
	/**
	 * Sets the isin.
	 *
	 * @param isin the new isin
	 */
	public void setIsin(String isin) {
		this.isin = isin;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param company_name the new name
	 */
	public void setName(String company_name) {
		this.name = company_name;
	}
	
	/**
	 * Gets the ticker.
	 *
	 * @return the ticker
	 */
	public String getTicker() {
		return ticker;
	}

	/**
	 * Sets the ticker.
	 *
	 * @param ticker the new ticker
	 */
	public void setTicker(String ticker) {
		this.ticker = ticker;
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
	 * Sets the wallstreet query string.
	 *
	 * @param wallstreet_query_string the new wallstreet query string
	 */
	public void setWallstreetQueryString(String wallstreet_query_string) {
		this.wallstreet_query_string = wallstreet_query_string;
	}
	
	/**
	 * Gets the wkn.
	 *
	 * @return the wkn
	 */
	public String getWkn() {
		return wkn;
	}

	/**
	 * Sets the wkn.
	 *
	 * @param wkn the new wkn
	 */
	public void setWkn(String wkn) {
		this.wkn = wkn;
	}

	/**
	 * Gets the valor.
	 *
	 * @return the valor
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * Sets the valor.
	 *
	 * @param valor the new valor
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}

	/**
	 * Gets the date added.
	 *
	 * @return the date added
	 */
	public Date getDateAdded() {
		return date_added;
	}
	
}
