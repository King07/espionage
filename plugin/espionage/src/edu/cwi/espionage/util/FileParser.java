package edu.cwi.espionage.util;

import java.util.HashMap;
import java.util.Map;

import edu.cwi.espionage.model.ProcessCase;

public abstract class FileParser {
	public abstract Map<String, HashMap<String, ProcessCase>> getProject();
}
