package edu.cwi.espionage.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import edu.cwi.espionage.model.ProcessCase;

public class FileLogManager {

	private static final Map<FileType, FileParser> PROJECT = new HashMap<FileType, FileParser>();
	
	static {
		PROJECT.put(FileType.FLOURITE_XML, new FlouriteXMLParser());
		PROJECT.put(FileType.MIMEC_CSV, new MimecCSVParser());
	}
	
	public Map<String, HashMap<String, ProcessCase>> getProject(FileType logs) {
		FileParser project = PROJECT.get(logs);
		project = project != null ? project : new FlouriteXMLParser();
		HashMap<String, HashMap<String, ProcessCase>> sortedProject = new HashMap<String, HashMap<String, ProcessCase>>();
		
		Iterator<Entry<String, HashMap<String, ProcessCase>>> pit = project.getProject().entrySet().iterator();
	    while (pit.hasNext()) {
	    	Map.Entry<String, HashMap<String, ProcessCase>> pair = pit.next();
	    	HashMap<String, ProcessCase> cases = (HashMap<String, ProcessCase>) Utils.sortByValue(pair.getValue());
			sortedProject.put(pair.getKey(), cases);
	    }
		return sortedProject;
	}
}
