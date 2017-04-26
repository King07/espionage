package edu.cwi.espionage.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import edu.cwi.espionage.util.DateManipulator;

public class Cases {
	Map<String,ProcessCase> processCases;

	public Cases() {
		processCases = new HashMap<String,ProcessCase>();
	}
	
	public boolean exist(String caseId){
		return processCases.containsKey(caseId);
	}
	
	public void add(ProcessCase processCase){
		processCases.put(processCase.getCaseId(), processCase);
	}
	
	public ProcessCase get(String caseId){
		return processCases.get(caseId);
	}
	
	public Map<String, ProcessCase> getProcessCases() {
		return Collections.unmodifiableMap(processCases);
	}
	public void printCases(){
		Iterator<Entry<String, ProcessCase>> it = getProcessCasesIterator();
	    while (it.hasNext()) {
	        Map.Entry<String, ProcessCase> pair = (Map.Entry<String, ProcessCase>)it.next();
	        System.out.println(pair.getKey());
	        System.out.println("RAW === " + pair.getValue().getTotalTime());
	        System.out.println("FORMATED === " + DateManipulator.getFormatedDiff(pair.getValue().getTotalTime()));
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}

	public Iterator<Entry<String, ProcessCase>> getProcessCasesIterator() {
		Iterator<Entry<String, ProcessCase>> it = processCases.entrySet().iterator();
		return it;
	}
	@Override
	public String toString() {
		return processCases.toString();
	}
	

}
