package database;

import interfaces.ISaveAndDelete;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CompanyNewsDetails implements ISaveAndDelete  {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	
	private float total_rating;
	private List<Integer> start_indexes;
	private List<Integer> end_indexes;
	private List<Integer> sentence_ratings;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public float getTotalRating() {
		return total_rating;
	}

	public void setTotalRating(float total_rating) {
		this.total_rating = total_rating;
	}

	public List<Integer> getStartIndexes() {
		return start_indexes;
	}

	public void setStartIndexes(List<Integer> start_indexes) {
		this.start_indexes = start_indexes;
	}

	public List<Integer> getEnd_indexes() {
		return end_indexes;
	}

	public void setEndIndexes(List<Integer> end_indexes) {
		this.end_indexes = end_indexes;
	}

	public List<Integer> getSentenceRatings() {
		return sentence_ratings;
	}

	public void setSentenceRatings(List<Integer> sentence_ratings) {
		this.sentence_ratings = sentence_ratings;
	}

	@Override
	public boolean saveToDB() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteFromDB(Object obj) {
		// TODO Auto-generated method stub
		
	}

	
	
}
