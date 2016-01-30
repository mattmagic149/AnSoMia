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
package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class FileUtils.
 */
public class FileUtils {
	
	/** The Constant path. */
	private final static String path = "data/";
	
	/**
	 * Read good reputation companies.
	 *
	 * @return the list
	 */
	public static List<String> readGoodReputationCompanies() {
		return readFile(path + "rep_companies.csv");
	}
	
	/**
	 * Read file.
	 *
	 * @param file_name the file_name
	 * @return the list
	 */
	private static List<String> readFile(String file_name) {
		FileReader file_reader;
		List<String> result = new ArrayList<String>();
		try {
			file_reader = new FileReader(file_name);
			BufferedReader buffered_reader = new BufferedReader(file_reader);
			String line = null;
			while ((line = buffered_reader.readLine()) != null) {
				result.add(line);
			}
			file_reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
}
