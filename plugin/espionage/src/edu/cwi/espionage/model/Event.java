package edu.cwi.espionage.model;

import java.util.Date;

public class Event implements Comparable<Event>{
	
	private Date timestamp;
	private String activity;
	private long elapstime;
	private String caseId;
	
	public Event(Date timestamp, long elapstime, String activity) {
		this.timestamp = timestamp;
		this.activity = activity;
		this.elapstime = elapstime;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	
	@Override
	public String toString() {
		return "EVENTS:: ["+getActivity()+" || "+getTimestamp().toString()+" ELAPSE = "+getElapstime()+"]";
	}

	public long getElapstime() {
		return elapstime;
	}

	public void setElapstime(long elapstime) {
		this.elapstime = elapstime;
	}


	@SuppressWarnings("deprecation")
	@Override
	public int compareTo(Event e) {
		if (this.getTimestamp().getYear() != e.getTimestamp().getYear()) 
	        return this.getTimestamp().getYear() - e.getTimestamp().getYear();
	    if (this.getTimestamp().getMonth() != e.getTimestamp().getMonth()) 
	        return this.getTimestamp().getMonth() - e.getTimestamp().getMonth();
	    return this.getTimestamp().getDate() - e.getTimestamp().getDate();
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	
	
}
