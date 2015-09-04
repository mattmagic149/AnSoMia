package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	
	private final static String path = "data/";
	
	public static List<String> readGoodReputationCompanies() {
		return readFile(path + "rep_companies.csv");
	}
	
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
