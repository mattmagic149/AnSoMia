package utils;

import java.util.List;

public class MathUtils {

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
	
	public static double mapValue(double old_min, double old_max, double new_min, double new_max, double value) {
		double old_range = old_max - old_min;
		double new_range = new_max - new_min;
		return (((value - old_min) * new_range) / old_range) + new_min;
	}
}
