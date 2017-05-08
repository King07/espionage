package edu.cwi.espionage.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class DateManipulator {
	
	public static long add(long initialDate, long nextDate){
		return initialDate + nextDate;
	}
	
	public static long diff(long initialDate, long nextDate){
		return Math.abs(nextDate - initialDate);
	}
	
	public static String getFormatedDiff(long diff){
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		return diffDays + " days, "+diffHours + " hours, "+diffMinutes + " minutes, "+diffSeconds + " seconds.";
	}
	
	public static Integer getMinutesFromDiff(long diff){
		int diffHours = (int) (diff / (60 * 1000));
		Integer format = new Integer(diffHours);
		return format;
	}
	
	public static Long getLongFromMinutes(int minutes){
		int longFromMins = minutes * 60 * 1000;
		return new Long(longFromMins);
	}
	
	public static Long getLongFromSeconds(int seconds){
		int longFromSeconds = seconds * 1000;
		return new Long(longFromSeconds);
	}
	
	public static Long getLongFromHours(int hours){
		int longFromHours = hours * 60 * 60 * 1000;
		return new Long(longFromHours);
	}
	
	public static Long getLongFromDays(int days){
		int longFromDays = days * 24 * 60 * 60 * 1000;
		return new Long(longFromDays);
	}
	
	public static Integer getSecondsFromDiff(long diff){
		int diffMins = (int) (diff / 1000);
		Integer format = new Integer(diffMins);
		return format;
	}
	
	public static Date getDateFromString(String aDate, String format){
		SimpleDateFormat formatter = new SimpleDateFormat(format);

        try {

            Date date = formatter.parse(aDate);
            return date;

        } catch (ParseException e) {
            e.printStackTrace();
        }
		return null;
	}
	
	public static Integer getHoursFromDiff(long diff){
		int diffHours = (int) (diff / (60 * 60 * 1000));
		Integer format = new Integer(diffHours);
		return format;
	}
	
	public static String getFormatedDate(Date date, String pattern) {
//		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String fd = simpleDateFormat.format(date);
		
		return fd;
	}
	
	
	
	

}
