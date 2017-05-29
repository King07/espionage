package edu.cwi.espionage.util;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import edu.cwi.espionage.model.Event;
import edu.cwi.espionage.model.ProcessCase;

public class MylynXMLParser extends FileParser {

	private static final String MYLYN_LOGS_PATH = "/.metadata/.plugins/mylyn/Logs/";

	public MylynXMLParser() {
		super(MYLYN_LOGS_PATH);

	}

	@Override
	public Map<String, HashMap<String, ProcessCase>> getProject() {
		Map<String, HashMap<String, ProcessCase>> projects = new HashMap<String, HashMap<String, ProcessCase>>();
		if (this.files == null) {
			return projects;
		}
		HashMap<String, ProcessCase> cases = null;
		String lastCaseId = "";
		for (int n = 0; n < this.files.length; n++) {
			String aLogFile = Utils.getFullPath(this.files[n], MYLYN_LOGS_PATH);
			System.out.println(aLogFile);
			try {

				Document document = this.getDocument(aLogFile);

				NodeList cmds = document.getElementsByTagName("InteractionEvent");
				List<Node> commands = sortNodeList(cmds);
				System.out.println("commands.size() = " + commands.size());
				System.out.println("cmds.getLength() = " + cmds.getLength());
				ProcessCase processCase = null;
				for (int i = 0; i < commands.size(); i++) {
					Element theNode = (Element) commands.get(i);

					String startDate = ((Element) commands.get(0)).getAttribute("StartDate");
					long fDate = (DateManipulator.getDateFromString(startDate, "yyyy-MM-dd HH:mm:ss.S Z").getTime()/ 1000);
					String sCurrDate = theNode.getAttribute("StartDate");
					long currDate = (DateManipulator.getDateFromString(sCurrDate, "yyyy-MM-dd HH:mm:ss.S Z").getTime()/ 1000);
					String typeKind = theNode.getAttribute("Kind");
					String caseId = Utils.regexChecker("\\{\\w+\\.java", theNode.getAttribute("StructureHandle")).replace("{", "");
					if (caseId.isEmpty()) {
						caseId = Utils.regexChecker("\\/\\w+\\.java", theNode.getAttribute("StructureHandle")).replace("/", "");
					}
					String projectName = this.files[n].split("\\_")[0];
					// ProcessCase processCase = null;

					if (!caseId.isEmpty() && !projectName.isEmpty()) {
						cases = getProjectCase(projects, projectName);

						if (projects.containsKey(projectName) && projects.get(projectName).containsKey(caseId)) {

							processCase = projects.get(projectName).get(caseId);
							cases = projects.get(projectName);
							//TODO verify that (if statement) working fine 
							if(!lastCaseId.equals(caseId)){
								long nDate = processCase.getLastEvent().getTimestamp().getTime() / 1000;
								long idleTime = DateManipulator.diff(nDate, currDate);
								long incrIdleTime = DateManipulator.add(processCase.getIdleTime(), idleTime);
								processCase.getIdleTimeTable().add(DateManipulator.getFormatedDate(Date.from(Instant.ofEpochSecond(currDate)), "dd/MM/yyyy"), idleTime);
								processCase.setIdleTime(incrIdleTime);
							}
							
						} else {
							long idleTime = DateManipulator.diff(fDate, currDate);
							processCase = new ProcessCase(caseId);
							processCase.setStartTime(fDate);
							processCase.setIdleTime(idleTime);
							processCase.getIdleTimeTable().add(DateManipulator.getFormatedDate(Date.from(Instant.ofEpochSecond(fDate)), "dd/MM/yyyy"), idleTime);
						}
					}

					if (processCase != null) {

						long elapseTime = DateManipulator.diff(fDate, currDate);
						Date processTimestamp = Date.from(Instant.ofEpochSecond(currDate));

						Event event = new Event(processTimestamp, elapseTime, typeKind);
						event.setCaseId(caseId);
						lastCaseId = caseId;
						if (!processCase.getEvents().isEmpty()) {
							long cElapseTime = DateManipulator.diff(processCase.getLastEvent().getTimestamp().getTime(),Date.from(Instant.ofEpochSecond(fDate)).getTime()) / 1000;
							event.setElapstime(DateManipulator.diff(cElapseTime, elapseTime));
						}
						processCase.addEvents(event);
						processCase.setLastEvent(event);
						cases.put(processCase.getCaseId(), processCase);
						projects.put(projectName, cases);

					}

				}

			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			} catch (SAXException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
		return projects;
	}

	private Document getDocument(String file) throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Load the input XML document, parse it and return an instance of the
		// Document class.
		Document document = builder.parse(new File(file));
		return document;
	}

	public String getProjectName(String line) {
		String pNameRaw = Utils.regexChecker("org(.*?)\\.java", line).replace("/", ".");
		return pNameRaw != "" ? pNameRaw.split("\\.")[2] : pNameRaw;
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

	private List<Node> sortNodeList(NodeList commands) {
		Map<Date, List<Node>> tempNodeList = new HashMap<Date, List<Node>>();
		for (int i = 0; i < commands.getLength(); i++) {
			Node aNode = commands.item(i);

			if (aNode.getNodeType() == Node.ELEMENT_NODE) {
				Element theNode = (Element) aNode;

				String startDate = theNode.getAttribute("StartDate");
				long fDate = (DateManipulator.getDateFromString(startDate, "yyyy-MM-dd HH:mm:ss.S Z").getTime() / 1000);
				Date fStartDate = Date.from(Instant.ofEpochSecond(fDate));

				if (tempNodeList.containsKey(fStartDate)) {
					List<Node> tempNodes = tempNodeList.get(fStartDate);
					tempNodes.add(theNode);
					tempNodeList.put(fStartDate, tempNodes);
				} else {
					List<Node> tempNodes = new ArrayList<Node>();
					tempNodes.add(theNode);
					tempNodeList.put(fStartDate, tempNodes);
				}
			}
		}

		SortedSet<Date> keys = new TreeSet<Date>(tempNodeList.keySet());

		List<Node> newCommands = new ArrayList<>();
		for (Date key : keys) {
			List<Node> tenpCommands = tempNodeList.get(key);
			newCommands.addAll(tenpCommands);
		}
		return newCommands;
	}

}