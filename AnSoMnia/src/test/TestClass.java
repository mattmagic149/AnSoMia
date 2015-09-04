package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.javatuples.Triplet;

import charts.MarketValueChart;
import analysers.MarketValueAnalyser;
import database.Company;
import database.MarketValue;
import database.News;
import utils.HibernateSupport;
import utils.MyDateUtils;

public class TestClass {
	
	static ArrayList<Triplet<String, Date, Date>> already_viewed = new ArrayList<Triplet<String, 
			Date, Date>>();
	
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
		
		/*TextAnalyserManager tam = new TextAnalyserManager();
		tam.createNumberOfNewsPerCompanyHist();*/
		

		/*Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy");
		from.setTime(format.parse("01.07.15"));
		to.setTime(format.parse("31.07.15"));
		
		MarketValueAnalyser mva = new MarketValueAnalyser(20, 10);
		mva.correlateIndustrySectors(from, to);*/
		
		MarketValueAnalyser mva = new MarketValueAnalyser(20, 10);
		PolynomialSplineFunction psf;
		
		//List<News> news_list = News.getNewsCompaniesGoodRepGePolarity(0.5); //EXTREM GUTES ERGEBNIS!
		List<News> news_list = News.getNewsCompaniesGoodRepLePolarity(-0.20); //EXTREM GUTES ERGEBNIS!

		//List<News> news_list = News.getNewsCompaniesGoodRepLePolarity(-0.6);
		if(news_list == null || news_list.size() == 0) {
			System.out.println("Quit, because Query provided no result.");
			return;
		}
		News news;
		List<Company> companies;
		Company company;
		
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		List<Date> dates;
		double[] stock_prices;
		List<double[]> stock_price_developement = new ArrayList<double[]>();
		List<String> infos = new ArrayList<String>();
		
		String title = "Stock Price Developement";
		String description;
		
		int news_size = news_list.size();
		System.out.println(news_size);

		for(int i = 0; i < news_size; i++) {
			news = news_list.get(i);
			companies = news.getCompanies();
			for(int j = 0; j < companies.size(); j++) {
				from.setTime(news.getDate());
				from.set(Calendar.HOUR, 0);
				from.set(Calendar.MINUTE, 0);
				from.set(Calendar.SECOND, 0);
				
				to.setTime(from.getTime());
				to.set(Calendar.DAY_OF_YEAR, from.get(Calendar.DAY_OF_YEAR) + 14);				
				
				company = companies.get(j);
				
				if(alreadyViewed(company.getName(), from.getTime(), to.getTime())) {
						//|| company.getName().contains("ADIDAS")) {
					System.out.println("continue..");
					continue;
				}

				if((psf = mva.getMarketValueSplineFromTo(company, from, to)) != null) {
					stock_prices = mva.getValuesFromPsf(psf, from, to);
					description = company.getName() + " (" + company.getIsin() + ")"
									+ " - polarity: " + news.getNewsDetails().get(0).getTotalPolarity();
					
					//values = company.getMarketValuesBetweenDatesFromDB(from.getTime(), to.getTime());
					MarketValueChart mvc = new MarketValueChart(title, "Date", "Price Per Unit");
					dates = MyDateUtils.getListOfDatesFromToCalendar(from, to);
					mvc.execute(stock_prices, description, dates);
					stock_price_developement.add(mva.getSharePriceDevelopement(psf, from, to));
					infos.add(description + " - objectivity" + news.getNewsDetails().get(0).getTotalObjectivity() + " hash = " + news.getHash());
				}
			}
		}
		
		Mean mean = new Mean();
		double[] stock_price_tmp = new double[stock_price_developement.size()];
		int array_size = stock_price_developement.get(0).length;
		for(int i = 0; i < array_size; i++) {
			for(int j = 0; j < stock_price_developement.size(); j++) {
				stock_price_tmp[j] = stock_price_developement.get(j)[i];
			}
			System.out.println((i +1 ) + ". day mean = " + mean.evaluate(stock_price_tmp));
		}

		System.out.println(infos.size());

		for(int i = 0; i < infos.size(); i++) {
			System.out.println(infos.get(i));
		}
		
		//double[] correlations = mva.correlateAllCompanies();
		//mva.createCorrelationHist("News Analysis", "description", correlations);
		
		/*mva.createChartsFromCorrelations("MarketValue Chart High Correlations", 
										 20, MarketValueAnalyser.ChartType.HIGH);
		
		mva.createChartsFromCorrelations("MarketValue Chart Low Correlations", 
				 20, MarketValueAnalyser.ChartType.LOW);
		
		mva.createChartsFromCorrelations("MarketValue Chart Mid Correlations", 
				 20, MarketValueAnalyser.ChartType.MID);
		
		mva.createCorrelationHist("News Analysis", "description", correlations);*/
		
		
		
		/*ArrayList<Criterion> criterions	= new ArrayList<Criterion>();
		criterions.add(Restrictions.gt("total_polarity", 0.3d));
		//criterions.add(Restrictions.between("total_polarity", -0.1d, 0.0d));
		//criterions.add(Restrictions.lt("total_polarity", -0.2d));

		//criterions.add(Restrictions.lt("total_polarity", -0.1d));
		//criterions.add(Restrictions.or(Restrictions.le("total_polarity", -0.1d), Restrictions.gt("total_polarity", 0.2d)));
		//criterions.add(Restrictions.gt("total_polarity", 0.1d));
		
		List<NewsDetail> details = HibernateSupport.readMoreObjects(NewsDetail.class, criterions);
		System.out.println(details.size());

		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		
		List<Company> companies;
		Company company;
		
		NewsDetail detail;
		News news;
		PolynomialSplineFunction psf;
		
		Variance var = new Variance();
		int counter = 0;
		int size = details.size();
		ArrayList<double[]> developements = new ArrayList<double[]>();
		double[] developement = null;
		while(details.size() > 0) {
			detail = details.get(0);
			details.remove(0);
			news = detail.getNews();
			
			from.setTime(news.getDate());
			to.setTime(news.getDate());
			to.set(Calendar.DAY_OF_YEAR, from.get(Calendar.DAY_OF_YEAR) + 7);
			
			companies = news.getCompanies();
			for(int i = 0; i < companies.size(); i++) {
				company = companies.get(i);
				psf = getMarketValueSplineFromTo(company, from, to);
				//System.out.println("from = " + from.getTime());
				//System.out.println("to = " + to.getTime());

				if(psf != null) {
					developement = getSharePriceDevelopement(psf, from, to);
					if(developement != null && company.getNumberOfNews() > 140) {
						developements.add(developement);
					}
				}

			}
			System.out.println(++counter + "/" + size);
		}
		
		size = developements.size();
		double[] single_result = new double[size];
		
		Mean mean = new Mean();
		Variance variance = new Variance();
		double mean_val;
		double var_val;
		
		System.out.println("#evaluated = " + developements.size());
		for(int i = 0; i < developement.length - 1; i++) {
			for(int j = 0; j < developements.size(); j++) {
				single_result[j] = developements.get(j)[i];
			}
			
			mean_val = mean.evaluate(single_result);
			var_val = variance.evaluate(single_result, mean_val);
			System.out.println("mean = " + mean_val);
			System.out.println("variance = " + var_val);
			
			new NewsHistogram("Share Price Developement", 
					single_result, 
					 "Share Price Developement " + (i + 1) + " day/s after news have been published.",
				     "Share Price Developement",
				     "#Occurences",
				     200, 
				     -30.0f, 
				     30.0f).execute();
			
		}*/
		
		/*Company company = HibernateSupport.readOneObjectByStringId(Company.class, "AT000000STR1");
		List<MarketValue> mvalues = company.getMarketValuesBetweenDatesFromDB(format.parse("27.07.15"), format.parse("07.08.15"));
		
		for(int i = 0; i < mvalues.size(); i++) {
			System.out.println(mvalues.get(i).getDate());
		}
		
		MarketValueAnalyser mva = new MarketValueAnalyser(20, 10);
		mva.correlationTest();*/

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
	
	public static boolean alreadyViewed(String name, Date from, Date to) {
		
		Triplet<String, Date, Date> to_check = new Triplet<String, Date, Date>(name, from, to);
		
		for(int i = 0; i < already_viewed.size(); i++) {
			if(already_viewed.get(i).equals(to_check)) {
				return true;
			}
		}
		
		return false;
	}

	public static double[] getSharePriceDevelopement(PolynomialSplineFunction psf,
													 Calendar from, 
													 Calendar to) {
		
		int diff = (int) TimeUnit.DAYS.convert(to.getTimeInMillis() 
									   - from.getTimeInMillis(), 
									   TimeUnit.MILLISECONDS);
		
		double[] result = new double[diff];
		double reference = psf.value(from.getTimeInMillis());
		double tmp;
		
		Calendar tmp_date = Calendar.getInstance();
		tmp_date.setTime(from.getTime());
		tmp_date.set(Calendar.DAY_OF_YEAR, from.get(Calendar.DAY_OF_YEAR) + 1);
		
		for(int i = 0; i < result.length; i++) {
			tmp = psf.value(tmp_date.getTimeInMillis());
			result[i] = 100 / reference * (tmp - reference);
			//reference = tmp;
			
			//increment
			tmp_date.set(Calendar.DAY_OF_YEAR, tmp_date.get(Calendar.DAY_OF_YEAR) + 1);
		}
		
		//System.out.println(psf.value(tmp_date.getTimeInMillis()) + " - " + tmp_date.getTime());

		
		return result;
	}
	
	public static PolynomialSplineFunction getMarketValueSplineFromTo(Company company,
																	  Calendar from,
																	  Calendar to) {
		
		List<MarketValue> values = company.getMarketValuesBetweenDatesFromDB(from.getTime(), to.getTime());
		
		if(values.size() == 0 || !normalizeMarketValues(company, values, from, to)) {
			return null;
		}
		
		//System.out.println("NOT NULL");
				
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
		

		/*System.out.println("from = " + from.getTime());
		System.out.println("from = " + from.getTime().getTime());

		System.out.println("to = " + to.getTime());
		System.out.println("from = " + from.getTime().getTime());

		System.out.println(values.size());
		for(int i = 0; i < values.size(); i++) {
			System.out.println(values.get(i).getDate());
			System.out.println(values.get(i).getDate().getTime());

		}*/
				
		MarketValue value;
		if(values.get(0).getDate().getTime() > from.getTime().getTime()) {

			value = getDateBeforeOrAfter(company, from, -1);
			if(value != null) {
				values.add(0, value);
			} else {
				System.out.println("returned FALSE normalizeMarketValues");
				return false;
			}
		}
		
		if(values.get(values.size() - 1).getDate().getTime() < to.getTime().getTime()) {
			
			value = getDateBeforeOrAfter(company, to, 1);
			if(value != null) {
				values.add(values.size(), value);
			} else {
				System.out.println("returned FALSE normalizeMarketValues");
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
			//System.out.println("I am trying to fetch a value...");
			cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + dec_inc);

			cr = new ArrayList<Criterion>();
			cr.add(Restrictions.eq("company", company));
			cr.add(Restrictions.eq("date", cal.getTime()));
			value = HibernateSupport.readOneObject(MarketValue.class, cr);
			if(value != null) {
				return value;
			}
		}
		
		System.out.println("returned NULL getDateBeforeOrAfter");
		return null;
	}

}
