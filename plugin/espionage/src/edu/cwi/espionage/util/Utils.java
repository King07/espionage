package edu.cwi.espionage.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;

public class Utils {
	
	public static final String Y_HOURS = "Hours";
	public static final String Y_MINUTES = "Minutes";

	public static String getClassName(String pathName, String regex) {
		String[] pathSplit = pathName.split(regex);
		return pathSplit[pathSplit.length - 1];
	}
	
	public static Integer getYValue(Long time, String yValue) {
		Map<String, Integer> values = new HashMap<String, Integer>();
		values.put(Y_HOURS, DateManipulator.getHoursFromDiff(time));
		values.put(Y_MINUTES, DateManipulator.getMinutesFromDiff(time));
		return values.get(yValue);
	}

	public static String getFullPath(String file) {
		String workspace = Platform.getLocation().toFile().getAbsolutePath();
		return workspace + file;

	}

	public static String getFullPath(String file, String logPath) {
		String workspace = Platform.getLocation().toFile().getAbsolutePath();
		return workspace + logPath + file;

	}

	public static String regexChecker(String theRegex, String str2Check) {
		Pattern checkRegex = Pattern.compile(theRegex);
		Matcher regexMatcher = checkRegex.matcher(str2Check);
		while (regexMatcher.find()) {

			if (regexMatcher.group().length() != 0) {
				return regexMatcher.group().trim();

			}

		}
		return "";
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> unsortMap) {

	    List<Map.Entry<K, V>> list =
	            new LinkedList<Map.Entry<K, V>>(unsortMap.entrySet());

	    Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
	        public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
	            return (o1.getValue()).compareTo(o2.getValue());
	        }
	    });

	    Map<K, V> result = new LinkedHashMap<K, V>();
	    for (Map.Entry<K, V> entry : list) {
	        result.put(entry.getKey(), entry.getValue());
	    }

	    return result;

	}

}
