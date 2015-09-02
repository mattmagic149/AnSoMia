package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.python.icu.util.Calendar;

import analysers.MarketValueAnalyser;
import database.Company;
import database.MarketValue;
import database.NewsDetail;
import utils.HibernateSupport;

public class TestClass {
	
	public static void main(String[] args) throws ParseException {
		
		
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

		
		/*List<NewsDetail> details = HibernateSupport.readMoreObjects(NewsDetail.class, new ArrayList<Criterion>());
		ArrayList<Double> values = new ArrayList<Double>();
		
		for(int i = 0; i < details.size(); i++) {
			values.add(details.get(i).getTotalObjectivity());
		}
		
		NewsHistogram nh = new NewsHistogram("News Analysis", values, "description", 50, -1.0f, 1.0f);
		nh.execute();*/		
		
		
		
		
		///TODO: correlate news with high and low polarity
		//ArrayList<Criterion> criterions	= new ArrayList<Criterion>();
		//Restrictions.le("total_polarity", -0.1d) very good result...
		//criterions.add(Restrictions.or(Restrictions.le("total_polarity", -0.1d), Restrictions.gt("total_polarity", 0.2d)));
		//criterions.add(Restrictions.gt("total_polarity", 0.1d));
		/*MarketValueAnalyser mva = new MarketValueAnalyser(20, 10);
		double[] correlations = mva.startCorrelationOfNewsWithMoreThanOneLinkedCompany();		
		
		mva.createChartsFromCorrelations("MarketValue Chart High Correlations", 
										 20, MarketValueAnalyser.ChartType.HIGH);
		
		mva.createChartsFromCorrelations("MarketValue Chart Low Correlations", 
				 20, MarketValueAnalyser.ChartType.LOW);
		
		mva.createChartsFromCorrelations("MarketValue Chart Mid Correlations", 
				 20, MarketValueAnalyser.ChartType.MID);
		
		mva.createCorrelationHist("News Analysis", "description", correlations);*/
		
		
		ArrayList<Criterion> criterions	= new ArrayList<Criterion>();
		Restrictions.gt("total_polarity", 0.4d);
		//criterions.add(Restrictions.or(Restrictions.le("total_polarity", -0.1d), Restrictions.gt("total_polarity", 0.2d)));
		//criterions.add(Restrictions.gt("total_polarity", 0.1d));
		
		List<NewsDetail> details = HibernateSupport.readMoreObjects(NewsDetail.class, criterions);
		
		System.out.println(details.size());
		
		/*Company company = HibernateSupport.readOneObjectByStringId(Company.class, "AT000000STR1");
		
		Date date = new Date();
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		Calendar tmp_date = Calendar.getInstance();
		
		from.setTime(date);
		to.setTime(date);

		from.set(Calendar.MONTH, 5);
		from.set(Calendar.DAY_OF_MONTH, 7);
		from.set(Calendar.HOUR, 0);
		from.set(Calendar.MINUTE, 0);
		from.set(Calendar.SECOND, 0);
		
		to.set(Calendar.MONTH, 5);
		to.set(Calendar.DAY_OF_MONTH, 28);
		to.set(Calendar.HOUR, 0);
		to.set(Calendar.MINUTE, 0);
		to.set(Calendar.SECOND, 0);
		
		PolynomialSplineFunction psf = getMarketValueSplineFromTo(company, from, to);
		if(psf == null) {
			return;
		}
		

		tmp_date.setTime(from.getTime());
		while(tmp_date.compareTo(to) <= 0) {
			System.out.println(psf.value(tmp_date.getTimeInMillis()) + " - " + tmp_date.getTime());
			
			tmp_date.set(Calendar.DAY_OF_YEAR, tmp_date.get(Calendar.DAY_OF_YEAR) + 1);

		}*/
		
	}
	
	public static PolynomialSplineFunction getMarketValueSplineFromTo(Company company,
																	  Calendar from,
																	  Calendar to) {
		
		List<MarketValue> values = company.getMarketValuesBetweenDatesFromDB(from.getTime(), to.getTime());
		
		if(!normalizeMarketValues(company, values, from, to)) {
			return null;
		}
		
		for(int i = 0; i < values.size(); i++) {
			System.out.println(values.get(i).getHigh() + " - " + values.get(i).getDate());
		}
				
		double[] x = new double[values.size()];
		double[] y = new double[values.size()];
		Calendar cal = Calendar.getInstance();
		for(int i = 0; i < values.size(); i++) {
			cal.setTime(values.get(i).getDate());
			x[i] = cal.getTimeInMillis();
			y[i] = values.get(i).getHigh();
		}
		
		return new LinearInterpolator().interpolate(x, y);
		
	}
	
	public static boolean normalizeMarketValues(Company company,
												List<MarketValue> values,
												Calendar from,
												Calendar to) {
		
		MarketValue value;
		if(values.get(0).getDate().after(from.getTime())) {
			value = getDateBeforeOrAfter(company, from, -1);
			if(value != null) {
				values.add(0, value);
			} else {
				return false;
			}
		}
		
		if(values.get(values.size() - 1).getDate().before(to.getTime())) {
			value = getDateBeforeOrAfter(company, to, 1);
			if(value != null) {
				values.add(values.size(), value);
			} else {
				return false;
			}
		}
		
		return true;
		
	}
	
	public static MarketValue getDateBeforeOrAfter(Company company, Calendar date, int dec_inc) {
		MarketValue value;
		List<Criterion> cr;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date.getTime());

		for(int count = 0; count < 10; count++) {
			System.out.println("I am trying to fetch a value...");
			cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + dec_inc);

			cr = new ArrayList<Criterion>();
			cr.add(Restrictions.eq("company", company));
			cr.add(Restrictions.eq("date", cal.getTime()));
			value = HibernateSupport.readOneObject(MarketValue.class, cr);
			if(value != null) {
				return value;
			}
		}
		
		return null;
	}

}
