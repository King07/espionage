package edu.cwi.espionage.util;

import java.util.HashMap;
import java.util.Map;

public class IdleTimeTable {
	private Map<String, Long> idleTimeTable;

	public IdleTimeTable() {
		idleTimeTable = new HashMap<String, Long>();
	}

	public boolean contains(String date) {
		return idleTimeTable.containsKey(date);
	}

	public void add(String date, Long idleTime) {
		if(contains(date)){
			idleTimeTable.put(date, DateManipulator.add(lookupIdleTime(date), idleTime));
		}else{
			idleTimeTable.put(date, idleTime);
		}
	}

	public Long lookupIdleTime(String date) {
		if(idleTimeTable.get(date) != null){
			return idleTimeTable.get(date);
		}
		return new Long(0);
	}

	public Boolean isEmpty() {
		return idleTimeTable.isEmpty();
	}
	
	public long total() {
		return idleTimeTable.values().stream().mapToLong(Long::longValue).sum();
	}

	@Override
	public String toString() {
		return idleTimeTable.toString();
	}

}
