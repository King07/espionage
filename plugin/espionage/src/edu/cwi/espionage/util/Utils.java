package edu.cwi.espionage.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;

public class Utils {

	public static String getClassName(String pathName, String regex) {
		String[] pathSplit = pathName.split(regex);
		return pathSplit[pathSplit.length - 1];
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

}
