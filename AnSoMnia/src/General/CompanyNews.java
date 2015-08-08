package General;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import Interface.ISaveAndDelete;
import Support.HibernateSupport;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class CompanyNews implements ISaveAndDelete  {
	
	@Id
	private String news_url;
	
	private String author;
	private Date date;
	private String content;
	private float total_rating;
	/*private List<Integer> start_indexes;
	private List<Integer> end_indexes;
	private List<Integer> sentence_ratings;*/
	
	@ManyToMany(mappedBy="company_news")
	private List<SingleCompany> companies;
	
	public CompanyNews() {}
	
	public CompanyNews(String url, String author, Date date, String content) {
		this.news_url = url;
		this.author = author;
		this.date = date;
		this.content = content;
		this.total_rating = Float.MIN_VALUE;
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
		return this.content;
	}
	
	public float getRating() {
		return this.total_rating;
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
