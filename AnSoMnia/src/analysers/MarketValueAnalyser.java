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

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang.time.DateUtils;

import database.*;

// TODO: Auto-generated Javadoc
/**
 * The Class MarketValueAnalyser.
 */
public class MarketValueAnalyser {
	
	/**
	 * Calculate correlation coefficient.
	 *
	 * @param first_values the first_values
	 * @param second_values the second_values
	 * @return the float
	 */
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
	
	/**
	 * Normalize arrays.
	 *
	 * @param values_1 the values_1
	 * @param values_2 the values_2
	 * @return true, if successful
	 */
	public boolean normalizeArrays(ArrayList<MarketValue> values_1, ArrayList<MarketValue> values_2) {

		Collections.sort(values_1);
		Collections.sort(values_2);
		
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
}
