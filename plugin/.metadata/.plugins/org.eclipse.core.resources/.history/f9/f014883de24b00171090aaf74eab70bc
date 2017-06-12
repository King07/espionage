package edu.cwi.espionage.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.cwi.espionage.model.Event;
import edu.cwi.espionage.model.ProcessCase;

public class MimecCSVParser extends FileParser {
	
	private static final String MIMEC_LOGS_PATH = "/.metadata/.plugins/mimec/";

	public MimecCSVParser() {
		super(MIMEC_LOGS_PATH);
	}

	@Override
	public Map<String, HashMap<String, ProcessCase>> getProject() {
		Map<String, HashMap<String, ProcessCase>> projects = new HashMap<String, HashMap<String, ProcessCase>>();

		HashMap<String, ProcessCase> cases = null;
		String lastCaseId = "";
		for (int n = 0; n < this.files.length; n++) {
			String aLogFile = Utils.getFullPath(this.files[n], MIMEC_LOGS_PATH);
			System.out.println(aLogFile);
			Scanner scanner = null;
			String startDate = "";
			try {
				scanner = new Scanner(new File(aLogFile));
				while (scanner.hasNext()) {
					List<String> line = CSVUtils.parseLine(scanner.nextLine());
					if(line.size() < 3){
						continue;
					}
					String sCurrDate = line.get(1).substring(7);
					if(startDate.equals("")){
						startDate = sCurrDate;
					}
					long currDate = (DateManipulator.getDateFromString(sCurrDate, "EEE MMM dd HH:mm:ss Z yyyy").getTime() / 1000);
					long fDate = (DateManipulator.getDateFromString(startDate, "EEE MMM dd HH:mm:ss Z yyyy").getTime()/ 1000);
					String typeKind = line.get(2).substring(6).trim();
					String caseId = Utils.regexChecker("\\{\\w+\\.java", line.get(3)).replace("{", "");
					String projectName = getProjectName(line.get(3));
					ProcessCase processCase = null;
					if (!caseId.isEmpty() && !projectName.isEmpty()) {
						cases = getProjectCase(projects, projectName);

						
						Date formatCurrDate = Date.from(Instant.ofEpochSecond(currDate));
						Date formatFDate = Date.from(Instant.ofEpochSecond(fDate));
						if (projects.containsKey(projectName) && projects.get(projectName).containsKey(caseId)) {

							processCase = projects.get(projectName).get(caseId);
							cases = projects.get(projectName);
							//TODO verify that (if statement) working fine 
							if(!lastCaseId.equals(caseId)){
								
								long nDate = processCase.getLastEvent().getTimestamp().getTime() / 1000;
								long idleTime = DateManipulator.diff(nDate, currDate);
								
								long incrIdleTime = DateManipulator.add(processCase.getIdleTime(), idleTime);
								processCase.getIdleTimeTable().add(DateManipulator.getFormatedDate(formatCurrDate, "dd/MM/yyyy"),DateManipulator.getHourFromDate(formatCurrDate), idleTime);
								processCase.setIdleTime(incrIdleTime);
								
								
							}
						} else {
							long idleTime = DateManipulator.diff(fDate, currDate);
							processCase = new ProcessCase(caseId);
							processCase.setStartTime(fDate);
							processCase.setIdleTime(new Long(0));
							processCase.getIdleTimeTable().add(DateManipulator.getFormatedDate(formatFDate, "dd/MM/yyyy"),DateManipulator.getHourFromDate(formatFDate), idleTime);
//							
						}
						
						if (processCase != null) {

							long elapseTime = DateManipulator.diff(fDate, currDate);
							Date processTimestamp = formatCurrDate;

							Event event = new Event(processTimestamp, elapseTime, typeKind);
							
							event.setCaseId(caseId);
							lastCaseId = caseId;
							if (!processCase.getEvents().isEmpty()) {
								long cElapseTime = DateManipulator.diff(processCase.getLastEvent().getTimestamp().getTime(),formatFDate.getTime()) / 1000;
								event.setElapstime(DateManipulator.diff(cElapseTime, elapseTime));
							}
							
							processCase.addEvents(event);
							processCase.setLastEvent(event);
							cases.put(processCase.getCaseId(), processCase);
							projects.put(projectName, cases);

						}
					}

				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (scanner != null) {
					scanner.close();
				}
			}
		}

		return projects;
	}

	private HashMap<String, ProcessCase> getProjectCase(Map<String, HashMap<String, ProcessCase>> projects,
			String projectName) {
		HashMap<String, ProcessCase> cases;
		if (projects.containsKey(projectName)) {
			cases = projects.get(projectName);
		} else {
			cases = new HashMap<String, ProcessCase>();
		}
		return cases;
	}

	public String getProjectName(String line) {
		String pNameRaw = Utils.regexChecker("no(.*?)\\{", line).replace("{", "");
		return pNameRaw != "" ? pNameRaw.split("\\.")[1] : pNameRaw;
	}

}
