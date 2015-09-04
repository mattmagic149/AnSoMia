package utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyDateUtils {

	
	public static List<Date> getListOfDatesFromToCalendar(Calendar from, Calendar to) {		
		
		List<Date> dates = new ArrayList<Date>();
		
		Calendar tmp_date = Calendar.getInstance();
		tmp_date.setTime(from.getTime());
		tmp_date.set(Calendar.DAY_OF_YEAR, from.get(Calendar.DAY_OF_YEAR));
		
		while(tmp_date.getTimeInMillis() <= to.getTimeInMillis()) {
			dates.add(tmp_date.getTime());
			
			//increment
			tmp_date.set(Calendar.DAY_OF_YEAR, tmp_date.get(Calendar.DAY_OF_YEAR) + 1);
		}
		
		return dates;
		
	}
}
