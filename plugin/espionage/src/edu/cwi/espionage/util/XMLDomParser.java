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

import org.eclipse.core.runtime.Platform;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.cwi.espionage.model.Event;
import edu.cwi.espionage.model.ProcessCase;

public class XMLDomParser {

	private String file;

	public XMLDomParser(String file) {
		String workspace = Platform.getLocation().toFile().getAbsolutePath();
		this.file = workspace + file;
		
	}

	public Map<String, HashMap<String, ProcessCase>> getProject() {
		Map<String, HashMap<String, ProcessCase>> projects = new HashMap<String, HashMap<String,ProcessCase>>();
		HashMap<String,ProcessCase> cases = null;
//		Cases cases = new Cases();
		try {
			// Document doc = DomParserExample.get(this.file);
			Document document = this.getDocument();
			Element eventsElem = document.getDocumentElement();
			String startTime = eventsElem.getAttribute("startTimestamp").substring(0, 10);

			// String root = doc.getDocumentElement().getNodeName();
			NodeList commands = document.getElementsByTagName("Command");
			ProcessCase processCase = null;
			String projectName = "";
			for (int i = 0; i < commands.getLength(); i++) {
				Node aNode = commands.item(i);
				String caseId = "";
				if (aNode.hasChildNodes()) {
					NodeList comCh = aNode.getChildNodes();
					Node cNode = comCh.item(1);
					if (cNode.getNodeType() == Node.ELEMENT_NODE) {
						Element ceNode = (Element) cNode;
						Element comChElem = (Element) comCh;
						caseId = Utils.getClassName(getCharacterDataFromElement(ceNode),"/");
						if (caseId.compareTo("") != 0) {
							long ts = Long.parseLong(comChElem.getAttribute("timestamp"));
							long initDate = Long.parseLong(startTime);
							long currDate = edu.cwi.espionage.util.DateManipulator.add(initDate, ts);
							projectName = comChElem.getAttribute("projectName");
							if (projects.containsKey(projectName)) {
								cases = projects.get(projectName);
							}else{
								cases = new HashMap<String,ProcessCase>();
							}
							//TODO Find idle time between file
							if (projects.containsKey(projectName) && projects.get(projectName).containsKey(caseId)) {
								//TODO I think the idle time already calculated in the timestamp as well YES.....
								processCase = projects.get(projectName).get(caseId);
								cases = projects.get(projectName);
								long idleTime = edu.cwi.espionage.util.DateManipulator.diff(processCase.getLastEventTime(), currDate);
								long incrIdleTime = edu.cwi.espionage.util.DateManipulator.add(processCase.getIdleTime(), idleTime);
								processCase.setIdleTime(incrIdleTime);
							} else {
								//TODO I don't need all that, because the elapse time already available
								
								long idleTime = edu.cwi.espionage.util.DateManipulator.diff(initDate, currDate);
								String testDate = edu.cwi.espionage.util.DateManipulator.getFormatedDiff(idleTime);
								System.out.println(testDate);
								processCase = new ProcessCase(caseId);
		
								processCase.setIdleTime(idleTime);
							}
							processCase.setLastEventTime(currDate);
							System.out.println(caseId);
						}
					}
				}

				if (aNode.getNodeType() == Node.ELEMENT_NODE && processCase != null) {
					Element theNode = (Element) aNode;
					String activity = theNode.getAttribute("_type");
					
					if(activity.contains("ShellBoundsCommand")){
						continue;
					}
					String timestamp = theNode.getAttribute("timestamp");
					System.out.println(timestamp+"+"+startTime);
					long epochSecond = (Long.parseLong(timestamp) + Long.parseLong(startTime));
					Date processTimestamp = Date.from(Instant.ofEpochSecond(epochSecond));
					System.out.println("==="+epochSecond);
					Event event = new Event(processTimestamp, Long.parseLong(timestamp), activity);
					System.out.println(timestamp);
					processCase.addEvents(event);
					cases.put(processCase.getCaseId(),processCase);
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
		return projects;
	}

	private Document getDocument() throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Load the input XML document, parse it and return an instance of the
		// Document class.
		Document document = builder.parse(new File(this.file));
		return document;
	}

	private String getCharacterDataFromElement(Element e) {
		//TODO Remove pathname and make it more generic
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
