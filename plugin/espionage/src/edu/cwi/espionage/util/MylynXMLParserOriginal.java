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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import edu.cwi.espionage.model.Event;
import edu.cwi.espionage.model.ProcessCase;

public class MylynXMLParserOriginal extends FileParser {

	private static final String MYLYN_LOGS_PATH = "/.metadata/.plugins/mylyn/Logs/";
	private String[] files;

	public MylynXMLParserOriginal() {
		super(MYLYN_LOGS_PATH);
		
	}
	@Override
	public Map<String, HashMap<String, ProcessCase>> getProject() {
		Map<String, HashMap<String, ProcessCase>> projects = new HashMap<String, HashMap<String, ProcessCase>>();
		if(this.files == null){
			return projects;
		}
		HashMap<String, ProcessCase> cases = null;
		for (int n = 0; n < this.files.length; n++) {
			String aLogFile = Utils.getFullPath(this.files[n], MYLYN_LOGS_PATH);
			System.out.println(aLogFile);
			try {
				
				Document document = this.getDocument(aLogFile);

				NodeList commands = document.getElementsByTagName("InteractionEvent");
				
				for (int i = 0; i < commands.getLength(); i++) {
					Node aNode = commands.item(i);

					if (aNode.getNodeType() == Node.ELEMENT_NODE) {
						Element theNode = (Element) aNode;
//						if(theNode.getAttribute("OriginId").contains("PackageExplorer")){
//							continue;
//						}
						if(theNode.getAttribute("OriginId").contains("propagation")){
							continue;
						}
//						if(theNode.getAttribute("OriginId").contains("CompilationUnitEditor")){
//							continue;
//						}
						
						String startDate = theNode.getAttribute("StartDate");
						long fDate = (DateManipulator.getDateFromString(startDate, "yyyy-MM-dd HH:mm:ss.S Z").getTime() / 1000);
						String endDate = theNode.getAttribute("EndDate");
						long fEndDate = (DateManipulator.getDateFromString(endDate, "yyyy-MM-dd HH:mm:ss.S Z").getTime() / 1000);
						String typeKind = theNode.getAttribute("Kind");
						String caseId = Utils.regexChecker("\\{\\w+\\.java", theNode.getAttribute("StructureHandle")).replace("{", "");
						if(caseId.isEmpty()){
							caseId = Utils.regexChecker("\\/\\w+\\.java", theNode.getAttribute("StructureHandle")).replace("/", "");
						}
						String projectName = this.files[n].split("\\_")[0];
						ProcessCase processCase = null;
						
						if (!caseId.isEmpty() && !projectName.isEmpty()) {
							cases = getProjectCase(projects, projectName);
							
							if (projects.containsKey(projectName) && projects.get(projectName).containsKey(caseId)) {

								processCase = projects.get(projectName).get(caseId);
								cases = projects.get(projectName);
								long idleTime = getOverlap(processCase, Date.from(Instant.ofEpochSecond(fDate)),Date.from(Instant.ofEpochSecond(fEndDate)));
								processCase.getIdleTimeTable().add(DateManipulator.getFormatedDate(Date.from(Instant.ofEpochSecond(fDate)), "dd/MM/yyyy"),idleTime);
								
							} else {
								processCase = new ProcessCase(caseId);
								processCase.setStartTime(fDate);
								processCase.setIdleTime(0);
								//
								processCase.getIdleTimeTable().add(DateManipulator.getFormatedDate(Date.from(Instant.ofEpochSecond(fDate)), "dd/MM/yyyy"), new Long("0"));
							}
							
							
							long elapstime = DateManipulator.diff(fEndDate, fDate);
							Event event = new Event(Date.from(Instant.ofEpochSecond(fEndDate)), elapstime, typeKind);
							processCase.addEvents(event);
							processCase.setLastEvent(event);
							cases.put(processCase.getCaseId(), processCase);
							projects.put(projectName, cases);
							
						}

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
	
	private Long getOverlap(ProcessCase processCase, Date aStartDate, Date aEndDate){
		Long res = new Long(0);
		for (Event e : processCase.getEvents()) {
			Date eEndDate = e.getTimestamp();
			Long elapse = e.getElapstime();
			Date eStartDate = Date.from(Instant.ofEpochSecond((eEndDate.getTime()/1000) - elapse));
			if(elapse.equals(new Long(0))){
				continue;
			}
			//1
			if(aStartDate.equals(eStartDate) && aEndDate.before(eEndDate)){
				Long currentRes = DateManipulator.diff(aEndDate.getTime(), aStartDate.getTime())/1000;
				res = updateTime(res, currentRes);
			}
			//2
			if(aStartDate.equals(eStartDate) && aEndDate.equals(eEndDate)){
				Long currentRes = elapse;
				res = updateTime(res, currentRes);
			}
			//3
			if(aStartDate.after(eStartDate) && aEndDate.before(eEndDate)){
				Long currentRes = DateManipulator.diff(aEndDate.getTime(),aStartDate.getTime())/1000;
				 res = updateTime(res, currentRes);
			}
			//4
			if(aStartDate.before(eStartDate) && aEndDate.before(eEndDate) && aEndDate.after(eStartDate)){
				Long currentRes = DateManipulator.diff(eStartDate.getTime(),aEndDate.getTime())/1000;
				 res = updateTime(res, currentRes);
			}
			//5
			if(aStartDate.before(eStartDate) && aEndDate.equals(eEndDate)){
				Long currentRes = elapse;
				res = updateTime(res, currentRes);
			}
			
			//6
			if(aStartDate.before(eStartDate) && aEndDate.after(eEndDate) ){
				Long currentRes = DateManipulator.diff(eStartDate.getTime(),eEndDate.getTime())/1000;
				res = updateTime(res, currentRes);
			}
			
			//7
			if(aStartDate.after(eStartDate) && aEndDate.after(eEndDate) && aStartDate.before(eEndDate)){
				Long currentRes = DateManipulator.diff(eEndDate.getTime(),aStartDate.getTime())/1000;
			    res = updateTime(res, currentRes);
			}
			
			
			
		}
		return res;
	}
	private Long updateTime(Long res, Long currentRes) {
		if(currentRes > res){
			res = currentRes;
		}
		return res;
	}
	

	

}