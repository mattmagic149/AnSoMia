package analysers;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang.time.DateUtils;

import database.*;

public class MarketValueAnalyser {
	
	public float calculateCorrelationCoefficient(ArrayList<MarketValue> first_values,
												ArrayList<MarketValue> second_values) {
		
		float correlation_coefficient = 0;
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
			return 0;
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
		
		first_values_variance = first_values_squared_average - first_values_average * first_values_average;
		second_values_variance = second_values_squared_average - second_values_average * second_values_average;
		covariance = first_values_times_second_values_average - first_values_average * second_values_average;
		
		correlation_coefficient = (float) (covariance / (Math.sqrt(first_values_variance * second_values_variance)));
		
		return correlation_coefficient;
	}
	
	public boolean normalizeArrays(ArrayList<MarketValue> values_1, ArrayList<MarketValue> values_2) {

		Collections.sort(values_1);
		Collections.sort(values_2);
		
		removeExcessMarketValuesFromFirstParameter(values_1, values_2);
		removeExcessMarketValuesFromFirstParameter(values_2, values_1);
		
		
		return true;
	}
	
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
}
