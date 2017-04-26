package edu.cwi.espionage.util;

public class Utils {
	
	public static String getClassName(String pathName, String regex){
		String[] pathSplit = pathName.split(regex);
		return pathSplit[pathSplit.length-1];
	}

}
