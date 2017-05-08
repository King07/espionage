package edu.cwi.espionage.tests;

import static org.hamcrest.MatcherAssert.*;

import java.util.Date;

import org.hamcrest.core.IsNull;
import org.junit.Test;
import static org.hamcrest.core.Is.*;
import edu.cwi.espionage.model.Event;
import edu.cwi.espionage.model.ProcessCase;
import edu.cwi.espionage.util.DateManipulator;

public class ProcessCaseTest {

	@Test
	public void test_total_time() {
		ProcessCase p = getDefaultData();
		
		assertThat(p.getTotalTime(), IsNull.notNullValue());
		assertThat(p.getTotalTime(), is(DateManipulator.getLongFromMinutes(10)));
		assertThat(p.getByDate().size(), is(1));
		

	}
	
	@Test
	public void test_idle_time() {
		ProcessCase p = getDefaultData();
		p.addEvents(new Event(DateManipulator.getDateFromString("10/02/2001 12:40:00", "dd/MM/yyyy HH:mm:ss"), DateManipulator
				.getLongFromMinutes(30), "Read"));
		for (ProcessCase pr : p.getByDate()) {
			assertThat(pr.getTotalIdleTime(), is(DateManipulator.getLongFromMinutes(20)));
		}
		

	}

	private ProcessCase getDefaultData() {
		ProcessCase p  = new ProcessCase("Football.java");
		Date startTime = DateManipulator.getDateFromString("10/02/2001 12:10:00", "dd/MM/yyyy HH:mm:ss");
		p.setStartTime(startTime.getTime());
		p.setIdleTime(0);
		p.getIdleTimeTable().add(DateManipulator.getFormatedDate(startTime, "dd/MM/yyyy"), new Long("0"));
		p.addEvents(new Event(DateManipulator.getDateFromString("10/02/2001 12:10:00", "dd/MM/yyyy HH:mm:ss"), DateManipulator
				.getLongFromMinutes(2), "Edit"));
		
		p.addEvents(new Event(DateManipulator.getDateFromString("10/02/2001 12:15:00", "dd/MM/yyyy HH:mm:ss"), DateManipulator
				.getLongFromMinutes(5), "Modify"));
		
		p.addEvents(new Event(DateManipulator.getDateFromString("10/02/2001 12:17:00", "dd/MM/yyyy HH:mm:ss"), DateManipulator
				.getLongFromMinutes(7), "Scroll"));
		
		p.addEvents(new Event(DateManipulator.getDateFromString("10/02/2001 12:20:00", "dd/MM/yyyy HH:mm:ss"), DateManipulator
				.getLongFromMinutes(10), "Read"));
		return p;
	}
}
