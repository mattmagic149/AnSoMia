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

import utils.HibernateSupport;

/**
 * The Class SentenceInformation.
 */
@Entity
public class SentenceInformation implements ISaveAndDelete {
	
	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	
	/** The start_index. */
	private int start_index;

	/** The end_index. */
	private int end_index;
	
	/** The polarity. */
	private double polarity;
	
	/** The objectivity. */
	private double objectivity;
	
	/** The details. */
	@ManyToOne
	@JoinColumn(name="details_id",updatable=false)
	private NewsDetail details;
	
	/**
	 * Instantiates a new sentence information.
	 */
	public SentenceInformation() {}
	
	/**
	 * Instantiates a new sentence information.
	 *
	 * @param details the details
	 * @param start_index the start_index
	 * @param end_index the end_index
	 * @param polarity the polarity
	 */
	public SentenceInformation(NewsDetail details, int start_index, int end_index, double polarity, double objectivity) {
		this.details = details;
		this.start_index = start_index;
		this.end_index = end_index;
		this.polarity = polarity;
		this.objectivity = objectivity;
	}
	
	/**
	 * Instantiates a new sentence information.
	 *
	 * @param serialized_info the serialized_info
	 * @param details the details
	 */
	public SentenceInformation(String[] serialized_info, NewsDetail details) {
		this.details = details;
		this.start_index = Integer.parseInt(serialized_info[1]);
		this.end_index = Integer.parseInt(serialized_info[2]);
		this.polarity = Float.parseFloat(serialized_info[3]);
		this.objectivity = Float.parseFloat(serialized_info[5]);
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
	 * Gets the polarity.
	 *
	 * @return the polarity
	 */
	public double getPolarity() {
		return polarity;
	}

	/**
	 * Gets the details.
	 *
	 * @return the details
	 */
	public NewsDetail getDetails() {
		return details;
	}
	
	public double getObjectivity() {
		return objectivity;
	}

	public void setObjectivity(double objectivity) {
		this.objectivity = objectivity;
	}

	public void setPolarity(double polarity) {
		this.polarity = polarity;
	}

	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#serialize()
	 */
	@Override
	public String serialize() {
		return details.getId() + "\t" + this.start_index + "\t" +  this.end_index + "\t" + 
				this.polarity + "\t" + this.details.getNews().getHash() + "\t" + this.objectivity;
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
