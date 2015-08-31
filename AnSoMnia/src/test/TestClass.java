package test;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;

import charts.NewsHistogram;
import database.NewsDetail;
import utils.HibernateSupport;
import utils.HttpRequestManager;

public class TestClass {
	
	public static HttpRequestManager http_req_manager = HttpRequestManager.getInstance();

	public static void main(String[] args) {
		
		
		/*Company company_1 = HibernateSupport.readOneObjectByStringId(Company.class, "AT000000STR1");
		Company company_2 = HibernateSupport.readOneObjectByStringId(Company.class, "AT00000AMAG3");

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
		
		ArrayList<MarketValue> values_1 = company_1.getMarketValuesBetweenDates(from, to);
		ArrayList<MarketValue> values_2 = company_2.getMarketValuesBetweenDates(from, to);
		
		MarketValueAnalyser mva = new MarketValueAnalyser();
		mva.normalizeArrays(values_1, values_2);
		
		MarketValueChart mvc = new MarketValueChart("Test Chart 1", values_1, values_2);
		mvc.execute();*/
		
		//System.out.println(mva.calculateCorrelationCoefficient(values_1, values_2));

		
		List<NewsDetail> details = HibernateSupport.readMoreObjects(NewsDetail.class, new ArrayList<Criterion>());
		ArrayList<Double> values = new ArrayList<Double>();
		
		for(int i = 0; i < details.size(); i++) {
			values.add(details.get(i).getTotalPolarity());
		}
		
		NewsHistogram nh = new NewsHistogram("News Analysis", values, "description", 50, -1.0f, 1.0f);
		nh.execute();
		
		
	}

}
