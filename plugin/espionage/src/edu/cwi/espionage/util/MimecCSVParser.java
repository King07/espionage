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
	private String[] files;

	public MimecCSVParser() {
		// create a file that is really a directory
		File aDirectory = new File(Utils.getFullPath(MIMEC_LOGS_PATH));
		this.files = aDirectory.list();
	}

	@Override
	public Map<String, HashMap<String, ProcessCase>> getProject() {
		Map<String, HashMap<String, ProcessCase>> projects = new HashMap<String, HashMap<String, ProcessCase>>();

		HashMap<String, ProcessCase> cases = null;
		for (int n = 0; n < this.files.length; n++) {
			String aLogFile = Utils.getFullPath(this.files[n], MIMEC_LOGS_PATH);
			Scanner scanner = null;
			try {
				scanner = new Scanner(new File(aLogFile));
				while (scanner.hasNext()) {
					List<String> line = CSVUtils.parseLine(scanner.nextLine());
					String date = line.get(1).substring(7);
					String typeKind = line.get(2).substring(6).trim();
					String caseId = Utils.regexChecker("\\{\\w+\\.java", line.get(3)).replace("{", "");
					String projectName = getProjectName(line.get(3));
					ProcessCase processCase = null;
					if (!caseId.isEmpty() && !projectName.isEmpty()) {
						if (projects.containsKey(projectName)) {
							cases = projects.get(projectName);
						} else {
							cases = new HashMap<String, ProcessCase>();
						}

						long fDate = (DateManipulator.getDateFromString(date, "EEE MMM dd HH:mm:ss Z yyyy").getTime() / 1000);
						if (projects.containsKey(projectName) && projects.get(projectName).containsKey(caseId)) {

							processCase = projects.get(projectName).get(caseId);
							cases = projects.get(projectName);
							long nDate = processCase.getLastEvent().getTimestamp().getTime()/1000;
							long idleTime = DateManipulator.diff(nDate, fDate);
							long incrIdleTime = DateManipulator.add(processCase.getIdleTime(), idleTime);
							processCase.setIdleTime(incrIdleTime);
							///
							processCase.getIdleTimeTable().add(DateManipulator.getFormatedDate(Date.from(Instant.ofEpochSecond(fDate)), "dd/MM/yyyy"),idleTime);
							
						} else {
							processCase = new ProcessCase(caseId);
							processCase.setStartTime(fDate);
							processCase.setIdleTime(0);
							//
							processCase.getIdleTimeTable().add(DateManipulator.getFormatedDate(Date.from(Instant.ofEpochSecond(fDate)), "dd/MM/yyyy"), new Long("0"));
						}
						

						long elapstime = DateManipulator.diff(processCase.getStartTime(), fDate) * 1000;
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

	public String getProjectName(String line) {
		String pNameRaw = Utils.regexChecker("no(.*?)\\{", line).replace("{", "");
		return pNameRaw != "" ? pNameRaw.split("\\.")[1] : pNameRaw;
	}

}