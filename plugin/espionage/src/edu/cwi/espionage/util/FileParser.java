package edu.cwi.espionage.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import edu.cwi.espionage.model.ProcessCase;

public abstract class FileParser {
	public String[] files;

	public FileParser(String logPath) {
		// create a file that is really a directory
	    File aDirectory = new File(Utils.getFullPath(logPath));
		this.files = aDirectory.list();
		System.out.println(files);

	}
	
	public abstract Map<String, HashMap<String, ProcessCase>> getProject();
}
