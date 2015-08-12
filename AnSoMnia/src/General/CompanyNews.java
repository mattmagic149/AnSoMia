package General;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;

import Interface.ISaveAndDelete;
import Support.HibernateSupport;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class CompanyNews implements ISaveAndDelete {
	
	
	@Id
	private long md5_hash;

	@Column(length = 500)
	private String news_url;
	
	private String author;
	private Date date;
	
	@Column(length = 8000)
	private String original_content;
	
	@Column(length = 8000)
	private String translated_content;
	
	private String language;
	private float total_rating;

	/*private List<Integer> start_indexes;
	private List<Integer> end_indexes;
	private List<Integer> sentence_ratings;*/
	
	@ManyToMany(mappedBy="company_news")
	private List<SingleCompany> companies;
	
	public CompanyNews() {}
	
	public CompanyNews(long hash, String url, String author, Date date, String original_content, 
			String translated_content, String language) {
		
		this.md5_hash = hash;	
		this.news_url = url;
		this.author = author;
		this.date = date;
		this.original_content = original_content;
		this.translated_content = translated_content;
		this.language = language;
		this.total_rating = Float.MIN_VALUE;
	}
	
	public String getTranslatedContent() {
		return translated_content;
	}

	public String getLanguage() {
		return language;
	}

	public String getAuthor() {
		return author;
	}

	public String getUrl() {
		return news_url;
	}

	public Date getDate() {
		return this.date;
	}

	public String getContent() {
		return this.original_content;
	}
	
	public float getRating() {
		return this.total_rating;
	}
	
	public long getHash() {
		return this.md5_hash;
	}
	
	public List<SingleCompany> getCompanies() {
		return companies;
	}



	/*public List<Integer> getStartIndexes() {
		return this.start_indexes;
	}
	
	public List<Integer> getEndIndexes() {
		return this.end_indexes;
	}

	public List<Integer> getSentenceRatings() {
		return this.sentence_ratings;
	}*/

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
