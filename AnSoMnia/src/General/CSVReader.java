package General;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader
{
  public static void main( String[] args ) throws Exception
  {
	  CSVReader obj = new CSVReader();
	  obj.run();
  }
  
  public void run() {
	  
		String csv_file = "data/isin.csv";
		BufferedReader br = null;
		String line = "";
		String company_name = "";
		String company_isin = "";
	 
		try {
			br = new BufferedReader(new FileReader(csv_file));
			while ((line = br.readLine()) != null) {
				String[] company = line.split(";");
				company_name = company[0];
				company_isin = company[1];	
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
		System.out.println(company_name);
		System.out.println(company_isin);
		System.out.println("Done");
	  }
  
}