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

import interfaces.ISaveAndDelete;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import utils.HibernateSupport;

/**
 * The Class EntityInformation.
 */
@Entity
public class EntityInformation implements ISaveAndDelete {
	
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	
	/** The start_index. */
	private int start_index;

	/** The end_index. */
	private int end_index;
	
	@ManyToOne
	@JoinColumn(name="entity_id",updatable=false)
	private NewsDetail details;
	
	@OneToOne
    @JoinColumn(name = "company_info_id")
	private CompanyInformation info;
	
	/**
	 * Instantiates a new sentence information.
	 */
	public EntityInformation() {}
	
	/**
	 * Instantiates a new sentence information.
	 *
	 * @param details the details
	 * @param start_index the start_index
	 * @param end_index the end_index
	 * @param polarity the polarity
	 * @param objectivity the objectivity
	 */
	public EntityInformation(NewsDetail details, CompanyInformation info, int start_index, int end_index) {
		this.details = details;
		this.start_index = start_index;
		this.end_index = end_index;
		this.info = info;
	}
	
	/**
	 * Gets the start index.
	 *
	 * @return the start index
	 */
	public int getStartIndex() {
		return start_index;
	}

	/**
	 * Gets the end index.
	 *
	 * @return the end index
	 */
	public int getEndIndex() {
		return end_index;
	}


	/**
	 * Gets the details.
	 *
	 * @return the details
	 */
	public NewsDetail getDetails() {
		return details;
	}


	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#serialize()
	 */
	@Override
	public String serialize() {
		return null;
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
