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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.javatuples.Pair;

import analysers.MarketValueAnalyser;

// TODO: Auto-generated Javadoc
/**
 * The Class MyDateUtils.
 */
public class MyDateUtils {

	
	/**
	 * Gets the list of dates from to calendar.
	 *
	 * @param from the from
	 * @param to the to
	 * @return the list of dates from to calendar
	 */
	public static ArrayList<Date> getListOfDatesFromToCalendar(Calendar from, Calendar to) {		
		
		ArrayList<Date> dates = new ArrayList<Date>();
		
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
	
	/**
	 * Gets the max and min of month.
	 *
	 * @param date the date
	 * @return the max and min of month
	 */
	public static Pair<Calendar, Calendar> getMaxAndMinOfMonth(Date date) {
		return new Pair<Calendar, Calendar>(getMinOfMonth(date), getMaxOfMonth(date));
	}
	
	/**
	 * Gets the max of month.
	 *
	 * @param date the date
	 * @return the max of month
	 */
	private static Calendar getMaxOfMonth(Date date) {
		
		Calendar to = Calendar.getInstance();
		to.setTime(date);		
		to.set(Calendar.DAY_OF_MONTH, to.getActualMaximum(Calendar.DAY_OF_MONTH));
		to.set(Calendar.HOUR_OF_DAY, to.getActualMaximum(Calendar.HOUR_OF_DAY));
		to.set(Calendar.MINUTE, to.getActualMaximum(Calendar.MINUTE));
		to.set(Calendar.SECOND, to.getActualMaximum(Calendar.SECOND));
		to.set(Calendar.MILLISECOND, to.getActualMaximum(Calendar.MILLISECOND));
		return to;
	}
	
	/**
	 * Gets the min of month.
	 *
	 * @param date the date
	 * @return the min of month
	 */
	private static Calendar getMinOfMonth(Date date) {
		
		Calendar from = Calendar.getInstance();
		from.setTime(date);		
		from.set(Calendar.DAY_OF_MONTH, from.getActualMinimum(Calendar.DAY_OF_MONTH));
		from.set(Calendar.HOUR_OF_DAY, from.getActualMinimum(Calendar.HOUR_OF_DAY));
		from.set(Calendar.MINUTE, from.getActualMinimum(Calendar.MINUTE));
		from.set(Calendar.SECOND, from.getActualMinimum(Calendar.SECOND));
		from.set(Calendar.MILLISECOND, from.getActualMinimum(Calendar.MILLISECOND));
		return from;
	}
	
	/**
	 * Gets the from to calendars.
	 *
	 * @param period the period
	 * @param date the date
	 * @param days_to_examine the days_to_examine
	 * @return the from to calendars
	 */
	public static Pair<Calendar, Calendar> getFromToCalendars(MarketValueAnalyser.Analyse period, 
			Date date, int days_to_examine) {
		
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		
		if(period == MarketValueAnalyser.Analyse.AFTER) {
			from.setTime(date);
			from.set(Calendar.HOUR, 0);
			from.set(Calendar.MINUTE, 0);
			from.set(Calendar.SECOND, 0);
			
			to.setTime(from.getTime());
			to.set(Calendar.DAY_OF_YEAR, from.get(Calendar.DAY_OF_YEAR) + days_to_examine);
		} else if(period == MarketValueAnalyser.Analyse.BEFORE) {
			
			to.setTime(date);
			to.set(Calendar.HOUR, 0);
			to.set(Calendar.MINUTE, 0);
			to.set(Calendar.SECOND, 0);
			
			from.setTime(to.getTime());
			from.set(Calendar.DAY_OF_YEAR, to.get(Calendar.DAY_OF_YEAR) - days_to_examine);
			
		} else if(period == MarketValueAnalyser.Analyse.BEFORE_AFTER) {
			int days_before = (int) Math.floor(days_to_examine/2.0);
			int days_after = (int) Math.ceil(days_to_examine/2.0);
			
			Calendar tmp = Calendar.getInstance();
			tmp.setTime(date);
			tmp.set(Calendar.HOUR, 0);
			tmp.set(Calendar.MINUTE, 0);
			tmp.set(Calendar.SECOND, 0);
			from.setTime(tmp.getTime());
			to.setTime(tmp.getTime());
			
			from.set(Calendar.DAY_OF_YEAR, tmp.get(Calendar.DAY_OF_YEAR) - days_before);
			to.set(Calendar.DAY_OF_YEAR, tmp.get(Calendar.DAY_OF_YEAR) + days_after);
			
		
		} else {
			System.out.println("Period Must be before or after.");
			assert false;
			return null;
		}
		
		return new Pair<Calendar,Calendar>(from, to);
	}
}
