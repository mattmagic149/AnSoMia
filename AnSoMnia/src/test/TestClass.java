package test;

import utils.HttpRequestManager;

public class TestClass {
	
	public static HttpRequestManager http_req_manager = HttpRequestManager.getInstance();

	public static void main(String[] args) {
		
		
		/*Company company_1 = HibernateSupport.readOneObjectByStringId(Company.class, "AT000000STR1");
		Company company_2 = HibernateSupport.readOneObjectByStringId(Company.class, "AT00000AMAG3");
		Company company_3 = HibernateSupport.readOneObjectByStringId(Company.class, "AT00000BENE6");
		Company company_4 = HibernateSupport.readOneObjectByStringId(Company.class, "AT00000FACC2");
		
		

		DateFormat date_format = new SimpleDateFormat("dd.MM.yy");
		Date from;
		Date to;
		try {
			from = date_format.parse("02.08.15");
			to = date_format.parse("20.08.15");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		ArrayList<MarketValue> values_1 = company_3.getMarketValuesBetweenDates(from, to);
		ArrayList<MarketValue> values_2 = company_4.getMarketValuesBetweenDates(from, to);
		
		MarketValueAnalyser mva = new MarketValueAnalyser();
		mva.normalizeArrays(values_1, values_2);
		
		System.out.println(mva.calculateCorrelationCoefficient(values_1, values_2));*/
	}

}
