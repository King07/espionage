package edu.cwi.espionage.model;

import java.util.ArrayList;
import java.util.List;

import edu.cwi.espionage.util.DateManipulator;
import edu.cwi.espionage.util.IdleTimeTable;
import edu.cwi.espionage.util.Utils;

public class ProcessCase implements Comparable<ProcessCase>, Cloneable{
	private static final long MINIMUM_IDLE_TIME = 600000; //10 minutes as default.
	private String caseId;
	private List<Event> events;
	private long startTime;
	private long idleTime;
	private Event lastEvent;
	private IdleTimeTable idleTimeTable;
	
	public ProcessCase(String caseId) {
		this.events = new ArrayList<Event>();
		this.setCaseId(caseId);
		this.setIdleTimeTable(new IdleTimeTable());
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

	public long getDateTotalTime() {
		Long processTime = new Long(0);
		for (Event event : events) {
			processTime += event.getElapstime();
		}
		return DateManipulator.diff(processTime,getIdleTime()) * 1000;
	}
	
	public long getTotalIdleTime() {
		return getIdleTimeTable().total();
	}
	
	public long getTotalTime() {
		long total = new Long(0);
		for (ProcessCase p : getByDate()) {
			total +=p.getDateTotalTime();
			
		}
		return total;
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
					else{
						calculateIdleTime(pcTemp, e);
						
					}
					pcTemp.addEvents(e);
					pcTemp.setLastEvent(e);
					eventsTemp.remove(e);
				}
			}
			String formatedDate = DateManipulator.getFormatedDate(pcTemp.getLastEvent().getTimestamp(), "dd/MM/yyyy");
			Long lookupIdleTime = this.getIdleTimeTable().lookupIdleTime(formatedDate);
			pcTemp.setIdleTime(lookupIdleTime);
			pc.add(pcTemp);
		}
		return pc;
	}

	private void calculateIdleTime(ProcessCase pcTemp, Event e) {
		Event event2 = pcTemp.getEvents().get(pcTemp.getEvents().size()-1);
		if (IsInactive(event2, e)) {
			long idleTime = getInactiveTime(e, event2);
			pcTemp.getIdleTimeTable().add(DateManipulator.getFormatedDate(e.getTimestamp(), "dd/MM/yyyy"),idleTime);
		}
	}
	
	/**
	 * To calculate inactive time:
	 * CONTEXT: The fluorite log all developers events. 
	 * METHOD : Visualizing Developer Interactions by  Roberto Minelli, Andrea Mocci, Michele Lanza and Lorenzo Baracchi
	 * 			idle time = event2 - event1 => if idle time is more than “minimum  idle  time (10 minutes)”, Then it suggest
	 * 			that the user is inactive.
	 * {@link http://conferences.computer.org/vissoft/2014/papers/6150a147.pdf } 
	 * @param Event e1
	 * @param Event e2
	 * 
	 * @return
	 */
	private long getInactiveTime(Event e1, Event e2){
		return Math.abs(e2.getTimestamp().getTime() - e1.getTimestamp().getTime());
	}
	
	private boolean IsInactive(Event e1, Event e2){
		boolean isInactive = false;
		if(getInactiveTime(e1,e2) > MINIMUM_IDLE_TIME){
			isInactive = true;
		}
		return isInactive;
	}
	
	@Override
	public String toString() {
		return Utils.getClassName(getCaseId(), "/");
	}

	public Event getLastEvent() {
		return lastEvent;
	}

	public void setLastEvent(Event e) {
		this.lastEvent = e;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public IdleTimeTable getIdleTimeTable() {
		return idleTimeTable;
	}

	public void setIdleTimeTable(IdleTimeTable idleTimeTable) {
		this.idleTimeTable = idleTimeTable;
	}

	@Override
	public int compareTo(ProcessCase p) {
		long ans = p.getTotalTime() - this.getTotalTime() ;
		return new Long(ans).intValue();
	}
	
	public Object clone()throws CloneNotSupportedException{  
		return super.clone();  
	}
	
	
}
