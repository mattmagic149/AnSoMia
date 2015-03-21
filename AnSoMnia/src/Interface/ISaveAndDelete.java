package Interface;

/**
 * Interface for saving Objects into the Database via Hibernate
 * 
 * @author Senkl/Ivantsits
 *
 */

public interface ISaveAndDelete {
	
	/**
	 * Save Method
	 * 
	 * @return true if save was successful - false otherwise
	 */
	public boolean saveToDB();
	
	
	/**
	 * Deletes the Object from the Database
	 */
	public void deleteFromDB(Object obj);




}
