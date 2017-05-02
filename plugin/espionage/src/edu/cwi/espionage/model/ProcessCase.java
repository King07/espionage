package edu.cwi.espionage.model;

import java.util.ArrayList;
import java.util.List;

import edu.cwi.espionage.util.DateManipulator;
import edu.cwi.espionage.util.Utils;

public class ProcessCase {
	private String caseId;
	private List<Event> events;
	private long startTime;
	private long idleTime;
	private long lastEventTime;
	
	public ProcessCase(String caseId) {
		this.events = new ArrayList<Event>();
		this.setCaseId(caseId);
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
	
	public void addEvents(Event event) {
		this.events.add(event);
	}

	public long getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(long idleTime) {
		this.idleTime = idleTime;
	}

	public long getTotalTime() {
//		Event event = events.get(events.size() - 1);
//		return event.getElapstime();
		System.out.println(getStartTime()+"-"+getLastEventTime()+"="+DateManipulator.diff(getStartTime(), getLastEventTime()));
		return DateManipulator.diff(getStartTime(), getLastEventTime())*1000;
	}
	
	public List<ProcessCase> getByDate() {
		List<ProcessCase> pc = new ArrayList<>();
		List<Event> eventsTemp = new ArrayList<>(events);
		while (!eventsTemp.isEmpty()) {
			Event topEvent = eventsTemp.get(0);
			ProcessCase pcTemp = new ProcessCase(getCaseId());
			for (Event e : events) {
				if(topEvent.compareTo(e) == 0){
					if(pcTemp.getEvents().isEmpty()){
						pcTemp.setStartTime(e.getTimestamp().getTime()/1000);
					}
					pcTemp.addEvents(e);
					pcTemp.setLastEventTime(e.getTimestamp().getTime()/1000);
					eventsTemp.remove(e);
				}
			}
			System.out.println("getTotalTime: ===> "+DateManipulator.getMinutesFromDiff(pcTemp.getTotalTime()));
//			System.out.println(pcTemp.getStartTime()+"<=====>"+pcTemp.getLastEventTime());
			pc.add(pcTemp);
		}
		System.out.println("getIdleTime: ===> "+DateManipulator.getMinutesFromDiff(getIdleTime()));
		return pc;
	}
	
	@Override
	public String toString() {
//		StringBuilder str = new StringBuilder("["+getCaseId()+" || ");
//		for (Event event : events) {
//			str.append(event.toString());
//		}
//		return str.toString();
		return Utils.getClassName(getCaseId(), "/");
	}

	public long getLastEventTime() {
		return lastEventTime;
	}

	public void setLastEventTime(long lastEventTime) {
		this.lastEventTime = lastEventTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
}
