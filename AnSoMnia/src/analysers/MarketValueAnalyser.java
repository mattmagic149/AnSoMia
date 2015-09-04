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
package analysers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.stat.correlation.*;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.javatuples.Triplet;

import utils.HibernateSupport;
import utils.MathUtils;
import charts.MarketValueChart;
import charts.NewsHistogram;
import database.*;

// TODO: Auto-generated Javadoc
/**
 * The Class MarketValueAnalyser.
 */
public class MarketValueAnalyser {
	
	/**
	 * The Enum ChartType.
	 */
	public enum ChartType {		
		LOW,		
		MID, 		
		HIGH
	};
	
	/** The high_correlations. */
	private ArrayList<Triplet<ArrayList<MarketValue>, ArrayList<MarketValue>, Double>> high_correlations;
	
	/** The low_correlations. */
	private ArrayList<Triplet<ArrayList<MarketValue>, ArrayList<MarketValue>, Double>> low_correlations;
	
	/** The mid_correlations. */
	private ArrayList<Triplet<ArrayList<MarketValue>, ArrayList<MarketValue>, Double>> mid_correlations;
	
	/** The already_correlated. */
	private ArrayList<Triplet<String, String, Date>> already_correlated;
	
	/** The days_to_correlate. */
	private int days_to_correlate;
	
	/** The min_days_to_correlate. */
	private int min_days_to_correlate;
	
	private PearsonsCorrelation p_corr;
	
	/**
	 * Instantiates a new market value analyser.
	 */
	public MarketValueAnalyser() {
		this.high_correlations = new ArrayList<Triplet<ArrayList<MarketValue>, ArrayList<MarketValue>, Double>>();
		this.low_correlations = new ArrayList<Triplet<ArrayList<MarketValue>, ArrayList<MarketValue>, Double>>();
		this.mid_correlations = new ArrayList<Triplet<ArrayList<MarketValue>, ArrayList<MarketValue>, Double>>();
		
		this.already_correlated = new ArrayList<Triplet<String, String, Date>>();
		
		this.p_corr = new PearsonsCorrelation();
		
		this.days_to_correlate = 7;
		this.min_days_to_correlate = 4;
	}
	
	/**
	 * Instantiates a new market value analyser.
	 *
	 * @param days the days
	 * @param min_days the min_days
	 */
	public MarketValueAnalyser(int days, int min_days) {
		this.high_correlations = new ArrayList<Triplet<ArrayList<MarketValue>, ArrayList<MarketValue>, Double>>();
		this.low_correlations = new ArrayList<Triplet<ArrayList<MarketValue>, ArrayList<MarketValue>, Double>>();
		this.mid_correlations = new ArrayList<Triplet<ArrayList<MarketValue>, ArrayList<MarketValue>, Double>>();
		
		this.already_correlated = new ArrayList<Triplet<String, String, Date>>();
		
		this.p_corr = new PearsonsCorrelation();

		this.days_to_correlate = days;
		this.min_days_to_correlate = min_days;
	}
	
	/**
	 * Calculate correlation coefficient.
	 *
	 * @param first_values the first_values
	 * @param second_values the second_values
	 * @return the float
	 */
	public double calculateCorrelationCoefficient(ArrayList<MarketValue> first_values,
												ArrayList<MarketValue> second_values) {
		
		double[] first = new double[first_values.size()];
		double[] second = new double[first_values.size()];
		
		for(int i = 0; i < first_values.size(); i++) {
			first[i] = first_values.get(i).getHigh();
			second[i] = second_values.get(i).getHigh();
		}
		
		return this.p_corr.correlation(first, second);
		
		/*float correlation_coefficient = 0;
		float first_values_average = 0, second_values_average = 0;
		float first_values_squared_average = 0, second_values_squared_average = 0;
		float first_values_times_second_values_average = 0;
		float first_values_variance = 0, second_values_variance = 0;
		float covariance = 0;
		
		MarketValue first_value;
		MarketValue second_value;
		float first_current_high;
		float second_current_high;
		
		int size = first_values.size();
		if(size == 0) {
			return Float.NaN;
		}
		
		for(int i = 0; i < size; i++) {
			first_value = first_values.get(i);
			second_value = second_values.get(i);
			first_current_high = first_value.getHigh();
			second_current_high = second_value.getHigh();
			
			first_values_average += first_current_high;
			second_values_average += second_current_high;
			
			first_values_squared_average += first_current_high * first_current_high;
			second_values_squared_average += second_current_high * second_current_high;
			
			first_values_times_second_values_average += first_current_high * second_current_high;
		}
		
		first_values_average = first_values_average/size;
		second_values_average = second_values_average/size;
		first_values_squared_average = first_values_squared_average/size;
		second_values_squared_average = second_values_squared_average/size;
		first_values_times_second_values_average = first_values_times_second_values_average/size;
		
		first_values_variance = first_values_squared_average - 
					first_values_average * first_values_average;
		
		second_values_variance = second_values_squared_average - 
					second_values_average * second_values_average;
		covariance = first_values_times_second_values_average - 
					first_values_average * second_values_average;
		
		correlation_coefficient = (float) (covariance / 
						(Math.sqrt(first_values_variance * second_values_variance)));
		
		
		System.out.println("first_values_average  = " + first_values_average);
		System.out.println("second_values_average  = " + second_values_average);
		System.out.println("first_values_squared_average  = " + first_values_squared_average);
		System.out.println("second_values_squared_average  = " + second_values_squared_average);
		System.out.println("first_values_times_second_values_average  = " + first_values_times_second_values_average);
		
		System.out.println("first_values_variance  = " + first_values_variance);
		System.out.println("second_values_variance  = " + second_values_variance);

		System.out.println("covariance  = " + covariance);

		if(correlation_coefficient == Float.NaN || 
				correlation_coefficient == Float.NEGATIVE_INFINITY ||
				correlation_coefficient == Float.POSITIVE_INFINITY ) {
			return Float.NaN;
		}
		
		return correlation_coefficient;*/
	}
	
	/**
	 * Calculate correlation of multiple companies.
	 *
	 * @param companies the companies
	 * @param from the from
	 * @param to the to
	 * @param min_days the min_days
	 * @return the array list
	 */
	private ArrayList<Double> calculateCorrelationOfMultipleCompanies(List<Company> companies, 
																	Date from, Date to, int min_days) {
		
		ArrayList<Double> result = new ArrayList<Double>();
		ArrayList<ArrayList<MarketValue>> market_values_list = new ArrayList<ArrayList<MarketValue>>();
		ArrayList<MarketValue> values1 = new ArrayList<MarketValue>();
		ArrayList<MarketValue> values2 = new ArrayList<MarketValue>();
		Company current_company;
		for(int i = 0; i < companies.size(); i++) {
			current_company = companies.get(i);
			market_values_list.add(current_company.getMarketValuesBetweenDatesFromDB(from, to));
		}
		
		//normalize all values.
		/*for(int i = 0; i < market_values_list.size(); i++) {
			values1 = market_values_list.get(i);
			if(values1.size() < min_days) {
				return null;
			}
			for(int j = (i + 1); j < market_values_list.size(); j++) {
				values2 = market_values_list.get(j);
				if(values2.size() < min_days) {
					return null;
				}
			}
		}*/
		double tmp;

		Triplet<String, String, Date> current_correlation;
		String isin1;
		String isin2;
		String smaller_isin;
		String bigger_isin;

		for(int i = 0; i < market_values_list.size(); i++) {
			values1 = market_values_list.get(i);

			if(values1.size() < min_days) {
				continue;
			}
			
			isin1 = values1.get(0).getCompany().getIsin();
			
			for(int j = (i + 1); j < market_values_list.size(); j++) {
				values2 = market_values_list.get(j);
				
				if(values2.size() < min_days) {
					continue;
				}
				
				isin2 = values2.get(0).getCompany().getIsin();
				//normalize the values to have the same length
				this.normalizeArrays(values1, values2);
				
				if(values2.size() < min_days || values1.get(0).getCompany().equals(values2.get(0).getCompany())) {
					continue;
				}
				
				smaller_isin = isin2.hashCode() <= isin1.hashCode() ? isin2 : isin1;
				bigger_isin = isin2.hashCode() <= isin1.hashCode() ? isin1 : isin2;
				
				current_correlation = new Triplet<String, String, Date>(smaller_isin, bigger_isin, from);
				
				if(this.checkCurrentRequestAlreadyCorrelated(current_correlation)) {
					System.out.println("already correlated...");
					continue;
				}
				
				tmp = this.calculateCorrelationCoefficient(values1, values2);
				
				if(!Double.isNaN(tmp) && !Double.isInfinite(tmp)) {
					result.add(tmp);
					System.out.println("new correlation");
					this.already_correlated.add(current_correlation);
				}
				
				if(tmp > 0.95 && this.high_correlations.size() < 20) {
					this.high_correlations.add(new Triplet<ArrayList<MarketValue>, 
									ArrayList<MarketValue>, Double>(values1, values2, tmp));
				}
				
				if(tmp < -0.90 && this.low_correlations.size() < 20) {
					this.low_correlations.add(new Triplet<ArrayList<MarketValue>, 
									ArrayList<MarketValue>, Double>(values1, values2, tmp));
				}
				
				if((tmp > -0.05 && tmp < 0.05) && this.mid_correlations.size() < 20) {
					this.mid_correlations.add(new Triplet<ArrayList<MarketValue>, 
									ArrayList<MarketValue>, Double>(values1, values2, tmp));
				}
				
			}
		}
		
		return result;
	}
	
	/**
	 * Check current request already correlated.
	 *
	 * @param to_check the to_check
	 * @return true, if successful
	 */
	private boolean checkCurrentRequestAlreadyCorrelated(Triplet<String, String, Date> to_check) {
		for(int i = 0; i < this.already_correlated.size(); i++) {
			if(this.already_correlated.get(i).equals(to_check)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Correlation test.
	 *
	 * @throws ParseException the parse exception
	 */
	public void correlationTest() throws ParseException {
		ArrayList<MarketValue> values1 = new ArrayList<MarketValue>();
		SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yy");
		values1.add(new MarketValue(21.4f, formater.parse("22.07.11")));
		values1.add(new MarketValue(21.71f, formater.parse("23.07.11")));
		values1.add(new MarketValue(21.2f, formater.parse("24.07.11")));
		
		values1.add(new MarketValue(21.34f, formater.parse("27.07.11")));
		values1.add(new MarketValue(21.49f, formater.parse("28.07.11")));
		values1.add(new MarketValue(21.39f, formater.parse("29.07.11")));
		values1.add(new MarketValue(22.16f, formater.parse("30.07.11")));
		values1.add(new MarketValue(22.53f, formater.parse("01.08.11")));
		
		values1.add(new MarketValue(22.44f, formater.parse("05.08.11")));
		values1.add(new MarketValue(22.75f, formater.parse("06.08.11")));
		values1.add(new MarketValue(23.23f, formater.parse("07.08.11")));
		values1.add(new MarketValue(23.09f, formater.parse("08.08.11")));
		
		values1.add(new MarketValue(22.85f, formater.parse("11.08.11")));
		values1.add(new MarketValue(22.45f, formater.parse("12.08.11")));
		values1.add(new MarketValue(22.48f, formater.parse("13.08.11")));
		values1.add(new MarketValue(22.27f, formater.parse("14.08.11")));
		values1.add(new MarketValue(22.37f, formater.parse("15.08.11")));

		values1.add(new MarketValue(22.28f, formater.parse("18.08.11")));
		values1.add(new MarketValue(23.06f, formater.parse("19.08.11")));
		values1.add(new MarketValue(22.99f, formater.parse("20.08.11")));
		
		System.out.println(values1.get(0).getDate());
		
		
		
		ArrayList<MarketValue> values2 = new ArrayList<MarketValue>();
		values2.add(new MarketValue(54.83f, formater.parse("22.07.11")));
		values2.add(new MarketValue(55.34f, formater.parse("23.07.11")));
		values2.add(new MarketValue(54.38f, formater.parse("24.07.11")));
		
		values2.add(new MarketValue(55.25f, formater.parse("27.07.11")));
		values2.add(new MarketValue(56.07f, formater.parse("28.07.11")));		
		values2.add(new MarketValue(56.30f, formater.parse("29.07.11")));
		values2.add(new MarketValue(57.05f, formater.parse("30.07.11")));
		values2.add(new MarketValue(57.91f, formater.parse("01.08.11")));
		
		values2.add(new MarketValue(58.20f, formater.parse("05.08.11")));
		values2.add(new MarketValue(58.39f, formater.parse("06.08.11")));
		values2.add(new MarketValue(59.19f, formater.parse("07.08.11")));
		values2.add(new MarketValue(59.03f, formater.parse("08.08.11")));
		
		values2.add(new MarketValue(57.96f, formater.parse("11.08.11")));
		values2.add(new MarketValue(57.52f, formater.parse("12.08.11")));
		values2.add(new MarketValue(57.76f, formater.parse("13.08.11")));
		values2.add(new MarketValue(57.09f, formater.parse("14.08.11")));
		values2.add(new MarketValue(57.85f, formater.parse("15.08.11")));

		values2.add(new MarketValue(57.54f, formater.parse("18.08.11")));
		values2.add(new MarketValue(58.85f, formater.parse("19.08.11")));
		values2.add(new MarketValue(58.60f, formater.parse("20.08.11")));
		
		System.out.println(this.calculateCorrelationCoefficient(values1, values2));
	}
	
	/**
	 * Creates the charts from correlations.
	 *
	 * @param title the title
	 * @param num_charts the num_charts
	 * @param type the type
	 */
	public void createChartsFromCorrelations(String title,
											 int num_charts,
											 MarketValueAnalyser.ChartType type) {
		
		ArrayList<Triplet<ArrayList<MarketValue>, ArrayList<MarketValue>, Double>> correlations;
		
		switch(type) {
			case LOW:
				correlations = this.low_correlations;
				break;
			case MID:
				correlations = this.mid_correlations;
				break;
			case HIGH:
				correlations = this.high_correlations;
				break;
			default:
				correlations = new ArrayList<Triplet<ArrayList<MarketValue>, 
													 ArrayList<MarketValue>, 
													 Double>>();
		}
		
		int charts_to_print = num_charts > correlations.size() ? correlations.size() : num_charts;
		
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		List<MarketValue> values1_list;
		List<MarketValue> values2_list;
		List<Date> dates = new ArrayList<Date>();
		
		double[] values1;
		double[] values2;

		String values1_des;
		String values2_des;
		
		for(int i = 0; i < charts_to_print; i++) {
			values1_list = correlations.get(i).getValue0();
			values2_list = correlations.get(i).getValue1();
			dates.clear();

			values1 = new double[values1_list.size()];
			values2 = new double[values1_list.size()];
			for(int j = 0; j < values1_list.size(); j++) {
				values1[j] = values1_list.get(i).getHigh();
				values2[j] = values2_list.get(i).getHigh();
				dates.add(values1_list.get(i).getDate());
			}
			from.setTime(values1_list.get(0).getDate());
			to.setTime(values1_list.get(values1_list.size() - 1).getDate());
			values1_des = values1_list.get(0).getCompany().getName() + 
						" (" + values1_list.get(0).getCompany().getIsin() + ")";
			
			values2_des = values2_list.get(0).getCompany().getName() + 
						" (" + values2_list.get(0).getCompany().getIsin() + ")";
			
			MarketValueChart mvc = new MarketValueChart(title, "Date", "Price Per Unit");
			
			mvc.execute(values1, values2, values1_des, values2_des, dates);
		}
		
	}
	
	/**
	 * Creates the correlation hist.
	 *
	 * @param title the title
	 * @param description the description
	 * @param values the values
	 */
	public void createCorrelationHist(String title, String description, double[] values) {
		NewsHistogram nh = new NewsHistogram(title, 
				 values, 
				 description,
			     "Correlation Coefficient",
			     "#News",
			     200);
		
		nh.execute();
	}
	
	/**
	 * Gets the high correlations.
	 *
	 * @return the high correlations
	 */
	public ArrayList<Triplet<ArrayList<MarketValue>, ArrayList<MarketValue>, Double>> getHighCorrelations() {
		return high_correlations;
	}
	
	/**
	 * Gets the low correlations.
	 *
	 * @return the low correlations
	 */
	public ArrayList<Triplet<ArrayList<MarketValue>, ArrayList<MarketValue>, Double>> getLowCorrelations() {
		return low_correlations;
	}
	
	/**
	 * Gets the mid correlations.
	 *
	 * @return the mid correlations
	 */
	public ArrayList<Triplet<ArrayList<MarketValue>, ArrayList<MarketValue>, Double>> getMidCorrelations() {
		return mid_correlations;
	}
	
	/**
	 * Gets the news with more than one linked company.
	 *
	 * @return the news with more than one linked company
	 */
	private List<News> getNewsWithMoreThanOneLinkedCompany() {
		HibernateSupport.beginTransaction();
		@SuppressWarnings("unchecked")
		List<News> news_list = HibernateSupport.getCurrentSession().createSQLQuery(
				"SELECT n.*, "
				+ "COUNT(ctn.md5_hash) AS news_entries "
				+ "FROM News n "
				+ "INNER JOIN CompanyToNews ctn "
				+ "ON n.md5_hash = ctn.md5_hash "
				+ "GROUP BY  "
				+ "n.md5_hash "
				+ "HAVING news_entries > 1;")// AND news_entries < 5;")
				.addEntity(News.class).list();
		HibernateSupport.commitTransaction();
		
		return news_list;
	}

	/**
	 * Normalize arrays.
	 *
	 * @param values_1 the values_1
	 * @param values_2 the values_2
	 * @return true, if successful
	 */
	public boolean normalizeArrays(ArrayList<MarketValue> values_1, ArrayList<MarketValue> values_2) {

		/*Collections.sort(values_1);
		Collections.sort(values_2);*/
		
		removeExcessMarketValuesFromFirstParameter(values_1, values_2);
		removeExcessMarketValuesFromFirstParameter(values_2, values_1);
		
		
		return true;
	}
	
	/**
	 * Removes the excess market values from first parameter.
	 *
	 * @param values_1 the values_1
	 * @param values_2 the values_2
	 * @return true, if successful
	 */
	private boolean removeExcessMarketValuesFromFirstParameter(ArrayList<MarketValue> values_1, 
																ArrayList<MarketValue> values_2) {
		
		MarketValue value_1;
		MarketValue value_2;
		boolean to_delete;
		
		for(int counter_1 = 0; counter_1 < values_1.size(); counter_1++) {
			to_delete = true;
			value_1 = values_1.get(counter_1);
			for(int counter_2 = 0; counter_2 < values_2.size(); counter_2++) {
				value_2 = values_2.get(counter_2);
				if(DateUtils.isSameDay(value_1.getDate(), value_2.getDate())) {
					to_delete = false;
					break;
				}
			}
			
			if(to_delete) {
				values_1.remove(counter_1--);
			}
		}
		
		return true;
	}
	
	/**
	 * Start correlation of news with more than one linked company.
	 *
	 * @return the double[]
	 */
	public double[] startCorrelationOfNewsWithMoreThanOneLinkedCompany() {
		List<News> news_list = this.getNewsWithMoreThanOneLinkedCompany();
		Calendar cal_from = Calendar.getInstance();
		Calendar cal_to = Calendar.getInstance();

		News news;
		ArrayList<Double> correlations = new ArrayList<Double>();
		ArrayList<Double> tmp = new ArrayList<Double>();
		
		int counter = 0;
		int size = news_list.size();
		while(news_list.size() > 0) {

			news = news_list.get(0);
			news_list.remove(0);
			System.out.println(++counter + "/" + size);
				
			cal_from.setTime(news.getDate());
			cal_to.setTime(news.getDate());

			//cal_from.set(Calendar.DAY_OF_YEAR, cal_from.get(Calendar.DAY_OF_YEAR) - 1);
			cal_to.set(Calendar.DAY_OF_YEAR, cal_to.get(Calendar.DAY_OF_YEAR) + this.days_to_correlate);
			tmp = this.calculateCorrelationOfMultipleCompanies(news.getCompanies(), 
							cal_from.getTime(), 
							cal_to.getTime(), this.min_days_to_correlate);
			
			if(tmp != null) {
				correlations.addAll(tmp);
			}
				
		}
		
		double[] target = new double[correlations.size()];
		double[] mapped = new double[correlations.size()];
		float target_average = 0;
		float mapped_average = 0;
		
    	for (int i = 0; i < target.length; i++) {
    		target[i] = correlations.get(i);
    		mapped[i] = MathUtils.mapValue(-1.0f, 1.0f, 0.0f, 1.0f, correlations.get(i));
			target_average += target[i];
			mapped_average += mapped[i];
    	}
		
		System.out.println("size = " + target.length);
		System.out.println("mapped_average = " + (mapped_average/target.length));
		System.out.println("target_average = " + (target_average/target.length));
		
		return target;
		
	}
	
	public double[] correlateAllCompanies() {
		
		List<Company> companies = HibernateSupport.readMoreObjects(Company.class, new ArrayList<Criterion>());

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy");

		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		try {
			from.setTime(format.parse("01.07.15"));
			to.setTime(format.parse("31.07.15"));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
		ArrayList<double[]> tmp = new ArrayList<double[]>();
		ArrayList<PolynomialSplineFunction> psf_list = new ArrayList<PolynomialSplineFunction>();
		PolynomialSplineFunction psf;
		Company current_company;
		int c_size = companies.size();

		for(int i = 0; i < c_size; i++) {
			System.out.println("adding: " + (i + 1) +"/" + c_size);

			current_company = companies.get(i);
			if((psf = getMarketValueSplineFromTo(current_company, from, to)) != null) {
				psf_list.add(psf);
			}			
		}
		
		
		for(int i = 0; i < psf_list.size(); i++) {
			tmp.add(getValuesFromPsf(psf_list.get(i), from, to));
		}

		double[] values1;
		double[] values2;
		ArrayList<Double> correlations = new ArrayList<Double>();
		double corr_tmp;
		for(int i = 0; i < tmp.size(); i++) {
			values1 = tmp.get(i);
			System.out.println("correlations: " + (i + 1) + "/" + tmp.size());

			for(int j = (i + 1); j < tmp.size(); j++) {
				values2 = tmp.get(j);
				if(Double.isFinite(corr_tmp = this.p_corr.correlation(values1, values2))) {
					correlations.add(corr_tmp);
				}
			}
		}
		
		double[] corr_result = new double[correlations.size()];
		
		for(int i = 0; i < correlations.size(); i++) {
			corr_result[i] = correlations.get(i);
			if(Double.isInfinite(corr_result[i]) || Double.isNaN(corr_result[i])) {
				assert false;
			}
		}
		
		Mean mean = new Mean();
		
		System.out.println("psf_list = " + psf_list.size());
		System.out.println("result = " + tmp.size());
		System.out.println("correlations = " + correlations.size());
		System.out.println("mean = " + mean.evaluate(corr_result));

		
		return corr_result;	
		
	}
	
	public List<Double> correlateCompanyWithMultipleCompanies(double[] values,
													  List<double[]> values_list) {
		
		int values_list_size = values_list.size();
		List<Double> result = new ArrayList<Double>();
		double correlation;
		
		for(int i = 0; i < values_list_size; i++) {
			if(Double.isFinite((correlation = this.p_corr.correlation(values, values_list.get(i))))) {
				result.add(correlation);
			}
		}
		
		return result;
	}
	
	public List<double[]> getMultipleMarketValuesFromCompanies(List<Company> companies,
													 Calendar from,
													 Calendar to) {
		
		List<double[]> result = new ArrayList<double[]>();
		PolynomialSplineFunction psf;
		
		Company company;
		int companies_size = companies.size();
		for(int i = 0; i < companies_size; i++) {
			company = companies.get(i);
			if((psf = this.getMarketValueSplineFromTo(company, from, to)) != null) {
				result.add(this.getValuesFromPsf(psf, from, to));
			}	
		}
		
		return result;
	}
	
	public List<Double> correlateTwoSectors(List<double[]> sector1, 
											List<double[]> sector2,
											Calendar from,
											Calendar to) {
		
		List<Double> result = new ArrayList<Double>();
		double[] values_sector1;

		List<Double> tmp;
		for(int i = 0; i < sector1.size(); i++) {
			values_sector1 = sector1.get(i);
			
			tmp = this.correlateCompanyWithMultipleCompanies(values_sector1, sector2);
			if(tmp != null) {
				result.addAll(tmp);
			}
		}
		
		return result;
		
	}
	
	public void correlateIndustrySectors(Calendar from, Calendar to) {
		List<IndustrySector> sectors = HibernateSupport.readMoreObjects(IndustrySector.class, new ArrayList<Criterion>());
		int sectors_size = sectors.size();
		IndustrySector sector1;
		IndustrySector sector2;
		
		List<Company> sector1_companies;
		List<Company> sector2_companies;
		
		List<double[]> values_list_sector1;
		List<double[]> values_list_sector2;
		
		List<Double> list_result;
		double[] double_result;
		Mean mean = new Mean();
		List<Double> total_result = new ArrayList<Double>();
		double mean_tmp;
		
		System.out.println(sectors.size());
		
		for(int i = 0; i < sectors_size; i++) {
			sector1 = sectors.get(i);
			sector1_companies = sector1.getCompanies();
			//System.out.println("sector1_companies size = " + sector1_companies.size());
			if(sector1_companies.size() < 200) {
				//continue;
			}
			
			values_list_sector1 = this.getMultipleMarketValuesFromCompanies(sector1_companies, from, to);
			
			for(int j = (i + 1); j < sectors_size; j++) {
				System.out.println((i + 1) + "/" + (j + 1) + "/" + sectors_size);
				sector2 = sectors.get(j);
				sector2_companies = sector2.getCompanies();
				//System.out.println("sector2_companies size = " + sector2_companies.size());
				if(sector2_companies.size() < 200) {
					//continue;
				}
				
				values_list_sector2 = this.getMultipleMarketValuesFromCompanies(sector2_companies, from, to);

				list_result = this.correlateTwoSectors(values_list_sector1, values_list_sector2, from, to);
				total_result.addAll(list_result);
				double_result = MathUtils.convertDoubleListToArray(list_result);
				mean_tmp = mean.evaluate(double_result);
				NewsHistogram nh = new NewsHistogram("Correlation " 
													+ sector1.getName()
													+ " correlated with "
													+ sector2.getName(), 
													double_result, 
													"Mean = " + mean_tmp,
													"Correlation Coefficient",
													"#Correlations",
													200);
				
				nh.execute();
				
				
			}
			
		}
		
		double[] total_result_arr = MathUtils.convertDoubleListToArray(total_result);
		mean_tmp = mean.evaluate(total_result_arr);
		System.out.println("Total mean = " + mean_tmp);
		
		NewsHistogram nh = new NewsHistogram("Total-Correlation", 
				total_result_arr, 
				"Mean = " + mean_tmp,
				"Correlation Coefficient",
				"#Correlations",
				200);

		nh.execute();
		
	}
	
	public double[] getValuesFromPsf(PolynomialSplineFunction psf, Calendar from, Calendar to) {
		
		int diff = (int) TimeUnit.DAYS.convert(to.getTimeInMillis() 
									   - from.getTimeInMillis(), 
									   TimeUnit.MILLISECONDS);
		
		double[] result = new double[diff];
		Calendar tmp_date = Calendar.getInstance();
		tmp_date.setTime(from.getTime());
		tmp_date.set(Calendar.DAY_OF_YEAR, from.get(Calendar.DAY_OF_YEAR) + 1);
		
		for(int i = 0; i < result.length; i++) {
			result[i] = psf.value(tmp_date.getTimeInMillis());
			if(Double.isInfinite(result[i]) || Double.isNaN(result[i])) {
				assert false;
			}

			//increment
			tmp_date.set(Calendar.DAY_OF_YEAR, tmp_date.get(Calendar.DAY_OF_YEAR) + 1);
		}
		
		return result;
	}
	
	public double[] getSharePriceDevelopement(PolynomialSplineFunction psf, Calendar from, Calendar to) {
		
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
		
		return result;
	}
	
	public PolynomialSplineFunction getMarketValueSplineFromTo(Company company, Calendar from, Calendar to) {
		
		List<MarketValue> values = company.getMarketValuesBetweenDatesFromDB(from.getTime(), to.getTime());
		
		if(values.size() == 0 || !normalizeMarketValues(company, values, from, to)) {
			return null;
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
	
	public boolean normalizeMarketValues(Company company, List<MarketValue> values, Calendar from, Calendar to) {
				
		MarketValue value;
		if(values.get(0).getDate().getTime() > from.getTime().getTime()) {

			value = getDateBeforeOrAfter(company, from, -1);
			if(value != null) {
				values.add(0, value);
			} else {
				return false;
			}
		}
		
		if(values.get(values.size() - 1).getDate().getTime() < to.getTime().getTime()) {
			
			value = getDateBeforeOrAfter(company, to, 1);
			if(value != null) {
				values.add(values.size(), value);
			} else {
				return false;
			}
		}
		
		return true;
		
	}
	
	public MarketValue getDateBeforeOrAfter(Company company, Calendar date, int dec_inc) {
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
		
		return null;
	}
	
	
}
