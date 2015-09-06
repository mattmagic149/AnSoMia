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

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class MathUtils.
 */
public class MathUtils {

	/**
	 * Convert double list to array.
	 *
	 * @param to_convert the to_convert
	 * @return the double[]
	 */
	public static double[] convertDoubleListToArray(List<Double> to_convert) {
		if(to_convert == null) {
			return null;
		}
		
		int size = to_convert.size();
		double[] result = new double[size];
		for(int i = 0; i < size; i++) {
			result[i] = to_convert.get(i);
		}
		
		return result;
	}
	
	/**
	 * Map value.
	 *
	 * @param old_min the old_min
	 * @param old_max the old_max
	 * @param new_min the new_min
	 * @param new_max the new_max
	 * @param value the value
	 * @return the double
	 */
	public static double mapValue(double old_min, double old_max, double new_min, double new_max, double value) {
		double old_range = old_max - old_min;
		double new_range = new_max - new_min;
		return (((value - old_min) * new_range) / old_range) + new_min;
	}
}
