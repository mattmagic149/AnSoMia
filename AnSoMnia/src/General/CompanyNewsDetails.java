package General;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import Interface.ISaveAndDelete;

@Entity
public class CompanyNewsDetails implements ISaveAndDelete  {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	
	private float total_rating;
	private List<Integer> start_indexes;
	private List<Integer> end_indexes;
	private List<Integer> sentence_ratings;
	
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
