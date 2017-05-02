package edu.cwi.espionage.util;

import java.util.HashMap;
import java.util.Map;

import edu.cwi.espionage.model.ProcessCase;

public class FileLogManager {

	private static final Map<FileType, FileParser> PROJECT = new HashMap<FileType, FileParser>();
	
	static {
		PROJECT.put(FileType.FLOURITE_XML, new FlouriteXMLParser());
		PROJECT.put(FileType.MIMEC_CSV, new CSVUtils());
	}
	
	public Map<String, HashMap<String, ProcessCase>> getProject(FileType logs) {
		FileParser project = PROJECT.get(logs);
		project = project != null ? project : new FlouriteXMLParser();
		return project.getProject();
	}
}
