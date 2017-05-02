package edu.cwi.espionage.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.cwi.espionage.model.Event;
import edu.cwi.espionage.model.ProcessCase;

public class CSVUtils extends FileParser {

	private static final char DEFAULT_SEPARATOR = ',';
	private static final char DEFAULT_QUOTE = '"';
	private static final String MIMEC_LOGS_PATH = "/.metadata/.plugins/mimec/";
	private String[] files;

	public static List<String> parseLine(String cvsLine) {
		return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
	}

	public static List<String> parseLine(String cvsLine, char separators) {
		return parseLine(cvsLine, separators, DEFAULT_QUOTE);
	}

	public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

		List<String> result = new ArrayList<>();

		// if empty, return!
		if (cvsLine == null || cvsLine.isEmpty()) {
			return result;
		}

		if (customQuote == ' ') {
			customQuote = DEFAULT_QUOTE;
		}

		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuffer curVal = new StringBuffer();
		boolean inQuotes = false;
		boolean startCollectChar = false;
		boolean doubleQuotesInColumn = false;

		char[] chars = cvsLine.toCharArray();

		for (char ch : chars) {

			if (inQuotes) {
				startCollectChar = true;
				if (ch == customQuote) {
					inQuotes = false;
					doubleQuotesInColumn = false;
				} else {

					// Fixed : allow "" in custom quote enclosed
					if (ch == '\"') {
						if (!doubleQuotesInColumn) {
							curVal.append(ch);
							doubleQuotesInColumn = true;
						}
					} else {
						curVal.append(ch);
					}

				}
			} else {
				if (ch == customQuote) {

					inQuotes = true;

					// Fixed : allow "" in empty quote enclosed
					if (chars[0] != '"' && customQuote == '\"') {
						curVal.append('"');
					}

					// double quotes in column will hit this!
					if (startCollectChar) {
						curVal.append('"');
					}

				} else if (ch == separators) {

					result.add(curVal.toString());

					curVal = new StringBuffer();
					startCollectChar = false;

				} else if (ch == '\r') {
					// ignore LF characters
					continue;
				} else if (ch == '\n') {
					// the end, break!
					break;
				} else {
					curVal.append(ch);
				}
			}

		}

		result.add(curVal.toString());

		return result;
	}

	public CSVUtils() {
		// create a file that is really a directory
		File aDirectory = new File(Utils.getFullPath(MIMEC_LOGS_PATH));
		this.files = aDirectory.list();
	}

	@Override
	public Map<String, HashMap<String, ProcessCase>> getProject() {
		// TODO Auto-generated method stub
		//String csvFile = Utils.getFullPath(MIMEC_LOGS_PATH);
		Map<String, HashMap<String, ProcessCase>> projects = new HashMap<String, HashMap<String, ProcessCase>>();

		HashMap<String, ProcessCase> cases = null;
		for (int n = 0; n < this.files.length; n++) {
			String aLogFile = Utils.getFullPath(this.files[n], MIMEC_LOGS_PATH);
			Scanner scanner = null;
			try {
				scanner = new Scanner(new File(aLogFile));
				while (scanner.hasNext()) {
					List<String> line = parseLine(scanner.nextLine());
					System.out.println(this.files[n]+ " :POOOOPPPSSSS: "+line.get(0));
//					String date = line.get(0).substring(0, 19);
					String date = line.get(1).substring(7);//replace("CEST", "CST");
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
							long idleTime = DateManipulator.diff(processCase.getLastEventTime(), fDate);
							long incrIdleTime = DateManipulator.add(processCase.getIdleTime(), idleTime);
							processCase.setIdleTime(incrIdleTime);
						} else {
							// long idleTime = DateManipulator.diff(initDate,
							// fDate);
							processCase = new ProcessCase(caseId);
							processCase.setStartTime(fDate);
							processCase.setIdleTime(0);
						}
						processCase.setLastEventTime(fDate);
						 System.out.println("Date:====> "+date);
						 System.out.println("Long Date:====> "+fDate);
						System.out.println(caseId);

						long elapstime = DateManipulator.diff(processCase.getStartTime(), fDate) * 1000;
						// System.out.println(DateManipulator.getDateFromString(date,
						// "yyyy-MM-dd
						// HH:mm:ss")+"-"+Date.from(Instant.ofEpochSecond(processCase.getStartTime())).toString()+"="+DateManipulator.getMinutesFromDiff(elapstime)+"[]"+elapstime);
						Event event = new Event(Date.from(Instant.ofEpochSecond(fDate)), elapstime, typeKind);
						processCase.addEvents(event);
						cases.put(processCase.getCaseId(), processCase);
						projects.put(projectName, cases);
					}
					// System.out.println("Event [date= " + date + ", kind= " +
					// typeKind + " , sourceHandle=" + caseId + " ,
					// projectName=" + projectName + "]");

				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
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
