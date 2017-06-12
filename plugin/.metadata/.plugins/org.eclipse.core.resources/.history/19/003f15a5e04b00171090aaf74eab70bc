package edu.cwi.espionage.util;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import edu.cwi.espionage.model.Event;
import edu.cwi.espionage.model.ProcessCase;

public class FlouriteXMLParser extends FileParser {

	private static final String FLUORITE_LOGS_PATH = "/.metadata/.plugins/edu.cmu.scs.fluorite/Logs/";

	public FlouriteXMLParser() {
		super(FLUORITE_LOGS_PATH);

	}
	@Override
	public Map<String, HashMap<String, ProcessCase>> getProject() {
		Map<String, HashMap<String, ProcessCase>> projects = new HashMap<String, HashMap<String, ProcessCase>>();
		if(this.files == null){
			return projects;
		}
		HashMap<String, ProcessCase> cases = null;
		for (int n = 0; n < this.files.length; n++) {
			String aLogFile = Utils.getFullPath(this.files[n], FLUORITE_LOGS_PATH);
			System.out.println(aLogFile);
			try {
				
				Document document = this.getDocument(aLogFile);
				Element eventsElem = document.getDocumentElement();
				if (!eventsElem.hasAttribute("startTimestamp")) {
					continue;
				}
				String startTime = eventsElem.getAttribute("startTimestamp").substring(0, 10);

				NodeList commands = document.getElementsByTagName("Command");
				ProcessCase processCase = null;
				String projectName = "";
				for (int i = 0; i < commands.getLength(); i++) {
					System.out.println("Progess: ==> "+i+"/"+commands.getLength());
					Node aNode = commands.item(i);
					String caseId = "";
					if (aNode.hasChildNodes()) {
						NodeList comCh = aNode.getChildNodes();
						Node cNode = comCh.item(1);
						if (cNode.getNodeType() == Node.ELEMENT_NODE) {
							Element ceNode = (Element) cNode;
							Element comChElem = (Element) comCh;
							caseId = Utils.getClassName(getCharacterDataFromElement(ceNode), "/");
							if (caseId.compareTo("") != 0) {
								String tString = comChElem.getAttribute("timestamp");
								long ts = Long.parseLong(tString.substring(0, tString.length() - 3));
								long initDate = Long.parseLong(startTime);
								long currDate = DateManipulator.add(initDate, ts);
								projectName = comChElem.getAttribute("projectName");
								if (projects.containsKey(projectName)) {
									cases = projects.get(projectName);
								} else {
									cases = new HashMap<String, ProcessCase>();
								}
								
								if (projects.containsKey(projectName)
										&& projects.get(projectName).containsKey(caseId)) {
									
									processCase = projects.get(projectName).get(caseId);
									cases = projects.get(projectName);
									long nDate = processCase.getLastEvent().getTimestamp().getTime()/1000;
									long idleTime = DateManipulator.diff(nDate, currDate);
									long incrIdleTime = DateManipulator.add(processCase.getIdleTime(), idleTime);
									Date formatCurrDate = Date.from(Instant.ofEpochSecond(currDate));
									processCase.getIdleTimeTable().add(DateManipulator.getFormatedDate(formatCurrDate, "dd/MM/yyyy"), DateManipulator.getHourFromDate(formatCurrDate),idleTime);
									processCase.setIdleTime(incrIdleTime);
								} else {
									long idleTime = DateManipulator.diff(initDate, currDate);
									processCase = new ProcessCase(caseId);
									processCase.setStartTime(initDate);
									processCase.setIdleTime(idleTime);
									Date formatInitDate = Date.from(Instant.ofEpochSecond(initDate));
									processCase.getIdleTimeTable().add(DateManipulator.getFormatedDate(formatInitDate, "dd/MM/yyyy"),DateManipulator.getHourFromDate(formatInitDate), idleTime);
								}
								
								System.out.println(caseId);
							}
						}
					}
						
					if (aNode.getNodeType() == Node.ELEMENT_NODE && processCase != null) {
						Element theNode = (Element) aNode;
						String activity = theNode.getAttribute("_type");

						if (activity.contains("ShellBoundsCommand")) {
							continue;
						}
						String timestamp = theNode.getAttribute("timestamp");
						long elapseTime = Long.parseLong(timestamp.substring(0, timestamp.length() - 3));
						long epochSecond = (elapseTime + Long.parseLong(startTime));
						Date processTimestamp = Date.from(Instant.ofEpochSecond(epochSecond));
					
						Event event = new Event(processTimestamp, elapseTime, activity);
						if(!processCase.getEvents().isEmpty()){
							long cElapseTime = DateManipulator.diff(processCase.getLastEvent().getTimestamp().getTime(), Date.from(Instant.ofEpochSecond(Long.parseLong(startTime))).getTime())/1000;
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

	private String getCharacterDataFromElement(Element e) {
		// TODO Remove pathname and make it more generic
		String filePath = e.getNodeName();
		if (filePath.compareTo("filePath") != 0) {
			return "";
		}
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}
	

}
