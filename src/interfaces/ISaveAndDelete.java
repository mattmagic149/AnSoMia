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
package interfaces;

// TODO: Auto-generated Javadoc
/**
 * Interface for saving Objects into Database or into a file.
 *
 * @author Senkl/Ivantsits
 */

public interface ISaveAndDelete {
	
	/**
	 * Save Method.
	 *
	 * @return true if save was successful - false otherwise
	 */
	public boolean saveToDB();
	
	
	/**
	 * Deletes the Object from the Database.
	 *
	 * @param obj the obj
	 */
	public void deleteFromDB(Object obj);
	
	/**
	 * Serializes the object, to store it in a file.
	 *
	 * @return the string
	 */
	public String serialize();

}
