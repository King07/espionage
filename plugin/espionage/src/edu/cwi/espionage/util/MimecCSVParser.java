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
	//TODO This should be in the configuration / setting page
	private static final String MIMEC_LOGS_PATH = "/.metadata/.plugins/mimec/dev5/";

	public MimecCSVParser() {
		super(MIMEC_LOGS_PATH);
	}

	@Override
	public Map<String, HashMap<String, ProcessCase>> getProject() {
		Map<String, HashMap<String, ProcessCase>> projects = new HashMap<String, HashMap<String, ProcessCase>>();

		HashMap<String, ProcessCase> cases = null;
		String lastCaseId = "";
		String projectName = "";
		for (int n = 0; n < this.files.length; n++) {
			String aLogFile = Utils.getFullPath(this.files[n], MIMEC_LOGS_PATH);
			System.out.println(aLogFile);
			if(aLogFile.contains("DS_Store")){
				continue;
			}
			Scanner scanner = null;
			String startDate = "";
			try {
				scanner = new Scanner(new File(aLogFile));
				ProcessCase processCase = null;
				while (scanner.hasNextLine()) {
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
					Date formatCurrDate = Date.from(Instant.ofEpochSecond(currDate));
					Date formatFDate = Date.from(Instant.ofEpochSecond(fDate));
					String typeKind = line.get(2).substring(6).trim();
					String caseId = Utils.regexChecker("\\{\\w+\\.java", line.get(3)).replace("{", "");
					if(!getProjectName(line.get(3)).isEmpty()){
						projectName = getProjectName(line.get(3));
					}
					if (!caseId.isEmpty() && !projectName.isEmpty()) {
						cases = getProjectCase(projects, projectName);
						
						if (projects.containsKey(projectName) && projects.get(projectName).containsKey(caseId)) {

							processCase = projects.get(projectName).get(caseId);
							cases = projects.get(projectName);
							//TODO verify that (if statement) working fine 
							if(!lastCaseId.equals(caseId)){
								long idleTime = DateManipulator.diff(processCase.getStartTime(), currDate);
								
								long incrIdleTime = DateManipulator.add(processCase.getIdleTime(), idleTime);
								processCase.getIdleTimeTable().add(DateManipulator.getFormatedDate(formatCurrDate, "dd/MM/yyyy"),DateManipulator.getHourFromDate(formatCurrDate), idleTime);
								processCase.setIdleTime(incrIdleTime);
								
								
							}
						} else {
							long idleTime = DateManipulator.diff(fDate, currDate);
							processCase = new ProcessCase(caseId);
							processCase.setStartTime(fDate);
							processCase.setIdleTime(idleTime);
							processCase.getIdleTimeTable().add(DateManipulator.getFormatedDate(formatCurrDate, "dd/MM/yyyy"),DateManipulator.getHourFromDate(formatCurrDate), idleTime);
//							if("TableRenderer.java".equals(processCase.getCaseId()) && "halogen".equals(projectName)){
//								System.out.println(startDate);
//								System.out.println(formatFDate);
//								System.out.println(formatCurrDate);
//								System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!");
//						}
						}
					}
						
						if (processCase != null ) {
							long elapseTime = DateManipulator.diff(fDate, currDate);
							Date processTimestamp = formatCurrDate;

							Event event = new Event(processTimestamp, elapseTime, typeKind);
							
							event.setCaseId(caseId);
							if (!processCase.getEvents().isEmpty()) {
								long cElapseTime = DateManipulator.diff(processCase.getLastEvent().getTimestamp().getTime(),formatFDate.getTime()) / 1000;
								event.setElapstime(DateManipulator.diff(cElapseTime, elapseTime));
							}
							
							if(!processCase.getEvents().isEmpty() && !lastCaseId.equals(caseId) & !caseId.isEmpty()){
								long elapstime = DateManipulator.diff(processCase.getStartTime(), currDate);
								event.setElapstime(elapstime);
									
							}
							if(processCase.getEvents().size() > 0){
//								if("TableRenderer.java".equals(processCase.getCaseId()) && "halogen".equals(projectName)){
//									System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!");
//									System.out.println(processCase.getCaseId());
//									System.out.println("Last => "+processCase.getEvents().get(processCase.getEvents().size()-1));
//									System.out.println("Current => "+event);
//									System.out.println(processCase.getIdleTimeTable());
//									System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!");
//								}
								
								long inactiveIdle = this.calculateIdleInactiveTime(processCase.getEvents().get(processCase.getEvents().size()-1), event);
								String formatedDate = DateManipulator.getFormatedDate(event.getTimestamp(), "dd/MM/yyyy");
								Integer hourFromDate = DateManipulator.getHourFromDate(event.getTimestamp());
								processCase.getIdleTimeTable().add(formatedDate, hourFromDate, inactiveIdle);
//								if("TableRenderer.java".equals(processCase.getCaseId()) && "halogen".equals(projectName)){
//									
//									System.out.println(formatedDate);
//									System.out.println(hourFromDate);
//									System.out.println(inactiveIdle);
//									System.out.println(processCase.getIdleTimeTable());
//									System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!");
//								}
							}
							
							processCase.addEvents(event);
							processCase.setLastEvent(event);
							cases.put(processCase.getCaseId(), processCase);
							projects.put(projectName, cases);
							if(!caseId.isEmpty()){
								lastCaseId = caseId;
							}

						}
					

				}

			} catch (FileNotFoundException e) {
//				e.printStackTrace();
			} 
//			finally {
//				if (scanner != null) {
//					scanner.close();
//				}
//			}
			
			lastCaseId = "";
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
//		System.out.println("@@@@@@@@@@@@@");
//		System.out.println(line);
		line = line.replace("\\/", ".");
		line = line.replace("/", ".");
		line = line.replace(".src.", "/src<");
		line = line.replace(".src<", "/src<");
		line = line.replace(".Des", "/Des");
//		System.out.println(line);
		String pNameRaw = Utils.regexChecker("src(.*?)\\{", line).replace("{", "");
//		System.out.println(pNameRaw.split("\\.").length > 1 ? pNameRaw.split("\\.")[1] : "");
		return pNameRaw.split("\\.").length > 1 ? pNameRaw.split("\\.")[1] : "";
	}

}
