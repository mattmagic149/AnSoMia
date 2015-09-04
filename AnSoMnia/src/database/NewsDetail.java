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

import java.util.ArrayList;
import java.util.List;

import interfaces.ISaveAndDelete;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import utils.HibernateSupport;

/**
 * The Class NewsDetail.
 */
@Entity
public class NewsDetail implements ISaveAndDelete  {

	/** The details_id. */
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long details_id;
	
	/** The news. */
	@ManyToOne
	@JoinColumn(name="md5_hash",updatable=false)
	private News news;
		
	/** The analyser. */
	private String analyser;
	
	/** The language. */
	private String language;

	/** The total_polarity. */
	private double total_polarity;
	
	/** The total_objectivity. */
	private double total_objectivity;

	/** The sentence_information. */
	@OneToMany
	@JoinColumn(name="details_id")
	private List<SentenceInformation> sentence_information;
	
	/**
	 * Instantiates a new news detail.
	 */
	public NewsDetail() {}
	
	/**
	 * Instantiates a new news detail.
	 *
	 * @param news the news
	 * @param analyser the analyser
	 * @param language the language
	 * @param total_rating the total_rating
	 * @param total_objectivity the total_objectivity
	 */
	public NewsDetail(News news, String analyser, String language, double total_rating,
			double total_objectivity) {
		this.sentence_information = new ArrayList<SentenceInformation>();
		this.news = news;
		this.analyser = analyser;
		this.language = language;
		this.total_polarity = total_rating;
		this.total_objectivity = total_objectivity;
	}
	
	/**
	 * Instantiates a new news detail.
	 *
	 * @param serialized_details the serialized_details
	 * @param news the news
	 */
	public NewsDetail(String[] serialized_details, News news) {
		/*
		return news.getHash() + "\t" + this.analyser + "\t" + this.language + "\t" + 
				this.total_polarity + "\t" + this.total_objectivity;*/
		this.sentence_information = new ArrayList<SentenceInformation>();
		this.news = news;
		
		this.analyser = serialized_details[1];
		this.language = serialized_details[2];
		this.total_polarity = Double.parseDouble(serialized_details[3]);
		this.total_objectivity = Double.parseDouble(serialized_details[4]);
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return details_id;
	}
	
	/**
	 * Gets the news.
	 *
	 * @return the news
	 */
	public News getNews() {
		return news;
	}

	/**
	 * Gets the analyser.
	 *
	 * @return the analyser
	 */
	public String getAnalyser() {
		return analyser;
	}

	/**
	 * Gets the language.
	 *
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Gets the total polarity.
	 *
	 * @return the total polarity
	 */
	public double getTotalPolarity() {
		return total_polarity;
	}

	/**
	 * Gets the sentence information.
	 *
	 * @return the sentence information
	 */
	public List<SentenceInformation> getSentenceInformation() {
		return sentence_information;
	}
	
	
	public void setSentenceInformation(List<SentenceInformation> sentence_information) {
		this.sentence_information = sentence_information;
	}

	/**
	 * Gets the total objectivity.
	 *
	 * @return the total objectivity
	 */
	public double getTotalObjectivity() {
		return total_objectivity;
	}

	
	/**
	 * Adds the sentence information.
	 *
	 * @param info the info
	 * @return true, if successful
	 */
	public boolean addSentenceInformation(SentenceInformation info) {
		boolean success = false;
		
		if (this.sentence_information.add(info)){
			success = info.saveToDB();
		} else {
			assert(false);
		}
		return success;
	}
	
	public void removeAllSentenceInformation() {
		SentenceInformation info;
		while(this.sentence_information.size() > 0) {
			info = this.sentence_information.get(0);
			this.sentence_information.remove(0);
			info.deleteFromDB(info);
		}
	}
	
	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#serialize()
	 */
	@Override
	public String serialize() {
		return news.getHash() + "\t" + this.analyser + "\t" + this.language + "\t" + 
				this.total_polarity + "\t" + this.total_objectivity + "\t" + this.details_id;
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
