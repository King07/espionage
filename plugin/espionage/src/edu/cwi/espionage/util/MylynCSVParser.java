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

public class MylynCSVParser extends FileParser {
	
	private static final String MYLYN_LOGS_PATH = "/.metadata/.plugins/mylyn/VIT/";
	private String[] files;

	public MylynCSVParser() {
		super(MYLYN_LOGS_PATH);
	}

	@Override
	public Map<String, HashMap<String, ProcessCase>> getProject() {
		Map<String, HashMap<String, ProcessCase>> projects = new HashMap<String, HashMap<String, ProcessCase>>();
		if(files == null){
			return projects;
		}
		HashMap<String, ProcessCase> cases = null;
		for (int n = 0; n < this.files.length; n++) {
			String aLogFile = Utils.getFullPath(this.files[n], MYLYN_LOGS_PATH);
			Scanner scanner = null;
			try {
				scanner = new Scanner(new File(aLogFile));
				while (scanner.hasNext()) {
					List<String> line = CSVUtils.parseLine(scanner.nextLine(),';');
					if(line.size() < 3){
						continue;
					}
					if(line.get(4).contains("explorer")){
						continue;
					}
//					if(line.get(4).contains("editor")){
//						continue;
//					}
					String startDate = "2013-12-13 "+line.get(0);
					String endDate = "2013-12-13 "+line.get(1);
					
					long fDate = new Long(0);
					long fEndDate = new Long(0);
					
					String typeKind = line.get(6).trim();
					String caseId = Utils.regexChecker("\\{\\w+\\.java", line.get(2)).replace("{", "");
					String projectName = this.files[n].split("\\_")[0];
					if(caseId.isEmpty()){
						caseId = Utils.regexChecker("\\/\\w+\\.java", line.get(2)).replace("/", "");
					}
					ProcessCase processCase = null;
					if (!caseId.isEmpty() && !projectName.isEmpty()) {
						cases = getProjectCase(projects, projectName);

					    fDate = (DateManipulator.getDateFromString(startDate, "yyyy-MM-dd HH:mm:ss").getTime() / 1000);
					    fEndDate = (DateManipulator.getDateFromString(endDate, "yyyy-MM-dd HH:mm:ss").getTime() / 1000);
						if (projects.containsKey(projectName) && projects.get(projectName).containsKey(caseId)) {

							processCase = projects.get(projectName).get(caseId);
							cases = projects.get(projectName);
							processCase.setIdleTime(new Long(0));
							///
							processCase.getIdleTimeTable().add(DateManipulator.getFormatedDate(Date.from(Instant.ofEpochSecond(fDate)), "dd/MM/yyyy"),new Long(0));
							
						} else {
							processCase = new ProcessCase(caseId);
							processCase.setStartTime(fDate);
							processCase.setIdleTime(0);
							//
							processCase.getIdleTimeTable().add(DateManipulator.getFormatedDate(Date.from(Instant.ofEpochSecond(fDate)), "dd/MM/yyyy"), new Long("0"));
						}
						

						long elapstime = DateManipulator.diff(fEndDate, fDate) ;
						Event event = new Event(Date.from(Instant.ofEpochSecond(fDate)), elapstime, typeKind);
						processCase.addEvents(event);
						processCase.setLastEvent(event);
						cases.put(processCase.getCaseId(), processCase);
						projects.put(projectName, cases);
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
		String pNameRaw = Utils.regexChecker("org(.*?)\\.java", line).replace("/", ".");
		System.out.println(pNameRaw);
		return pNameRaw != "" ? pNameRaw.split("\\.")[1] : pNameRaw;
	}

}