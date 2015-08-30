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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import utils.HibernateSupport;

/**
 * The Class News.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class News implements ISaveAndDelete {

	/** The md5_hash. */
	@Id
	private long md5_hash;

	/** The news_url. */
	@Column(length = 500)
	private String news_url;
	
	/** The author. */
	private String author;
	
	/** The date. */
	private Date date;
	
	/** The original_content. */
	@Column(length = 8000)
	private String original_content;
	
	/** The translated_content. */
	@Column(length = 8000)
	private String translated_content;
	
	/** The language. */
	private String language;
	
	/** The companies. */
	@ManyToMany(mappedBy="company_news")
	private List<Company> companies;
	
	/** The news_details. */
	@OneToMany
	@JoinColumn(name="md5_hash")
	private List<NewsDetail> news_details;

	/**
	 * Instantiates a new news.
	 */
	public News() {
		this.news_details = new ArrayList<NewsDetail>();
	}
	
	/**
	 * Instantiates a new news.
	 *
	 * @param hash the hash
	 * @param url the url
	 * @param author the author
	 * @param date the date
	 * @param original_content the original_content
	 * @param translated_content the translated_content
	 * @param language the language
	 */
	public News(long hash, String url, String author, Date date, String original_content, 
			String translated_content, String language) {
		
		this.news_details = new ArrayList<NewsDetail>();
		
		this.md5_hash = hash;	
		this.news_url = url;
		this.author = author;
		this.date = date;
		this.original_content = original_content;
		this.translated_content = translated_content;
		this.language = language;
	}
	
	/**
	 * Instantiates a new news.
	 *
	 * @param serialized_news the serialized_news
	 */
	public News(String serialized_news) {
		this.news_details = new ArrayList<NewsDetail>();
		
		String[] tmp = serialized_news.split("\t");
		
		this.md5_hash = Long.parseLong(tmp[0]);
		this.news_url = tmp[1];
		this.author = tmp[2];
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			this.date = formatter.parse(tmp[3]);
		} catch (ParseException e) {
			e.printStackTrace();
			assert(false);
		}
		
		this.original_content = tmp[4];
		this.translated_content = tmp[5];
		this.language = tmp[6];
	}
	
	/**
	 * Gets the translated content.
	 *
	 * @return the translated content
	 */
	public String getTranslatedContent() {
		return translated_content;
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
	 * Gets the author.
	 *
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return news_url;
	}

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public String getContent() {
		return this.original_content;
	}
	
	/**
	 * Gets the hash.
	 *
	 * @return the hash
	 */
	public long getHash() {
		return this.md5_hash;
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
	 * Gets the news details.
	 *
	 * @return the news details
	 */
	public List<NewsDetail> getNewsDetails() {
		return news_details;
	}
	
	
	/**
	 * Sets the translated content.
	 *
	 * @param translated_content the new translated content
	 */
	public void setTranslatedContent(String translated_content) {
		this.translated_content = translated_content;
	}
	
	/**
	 * Contains details with specific analyser.
	 *
	 * @param analyser the analyser
	 * @return true, if successful
	 */
	public boolean containsDetailsWithSpecificAnalyser(String analyser) {
		
		for(int i = 0; i < this.news_details.size(); i++) {
			if(this.news_details.get(i).getAnalyser().equals(analyser)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds the company news details.
	 *
	 * @param details the details
	 * @return true, if successful
	 */
	public boolean addCompanyNewsDetails(NewsDetail details) {
		boolean success = false;
		
		if (this.news_details.add(details)){
			success = details.saveToDB();
		} else {
			assert(false);
		}
		return success;
	}
	
	/* (non-Javadoc)
	 * @see interfaces.ISaveAndDelete#serialize()
	 */
	@Override
	public String serialize() {
		return this.md5_hash + "\t" + this.news_url + "\t" + this.author + "\t" + 
				this.date + "\t" + this.original_content + "\t" +  this.translated_content + "\t" + 
				this.language;
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
