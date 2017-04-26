package edu.cwi.espionage.util;

import java.text.SimpleDateFormat;

public class DateManipulator {
	
	public static long add(long initialDate, long nextDate){
		return initialDate + nextDate;
	}
	
	public static long diff(long initialDate, long nextDate){
		return nextDate - initialDate;
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
	
	public static Integer getHoursFromDiff(long diff){
		int diffHours = (int) (diff / (60 * 60 * 1000));
		Integer format = new Integer(diffHours);
		return format;
	}
	
	public static String getFormatedDate(java.util.Date date, String pattern) {
//		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String fd = simpleDateFormat.format(date);
		
		return fd;
	}
	
	
	
	

}
