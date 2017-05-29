package edu.cwi.espionage.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import edu.cwi.espionage.model.ProcessCase;

public class FileUtils {

	// Create the file and the PrintWriter that will write to the file
	
	private static final String DEFAULT_SEPARATOR = ",";

	public static PrintWriter createFile(String fileName){
			
			try{
				
				// Creates a File object that allows you to work with files on the hardrive
				
				File listOfNames = new File(fileName);
				// if file doesnt exists, then create it
				
				
				if (!listOfNames.getParentFile().exists()){
					listOfNames.getParentFile().mkdirs();
				}
				if (!listOfNames.exists()){
					listOfNames.createNewFile();
				}
				
				// FileWriter is used to write streams of characters to a file
				// BufferedWriter gathers a bunch of characters and then writes
				// them all at one time (Speeds up the Program)
				// PrintWriter is used to write characters to the console, file
		
				PrintWriter infoToWrite = new PrintWriter(
				new BufferedWriter(
						new FileWriter(listOfNames)));
				return infoToWrite;
			}
		
			// You have to catch this when you call FileWriter
			
			catch(IOException e){
				
				System.out.println("An I/O Error Occurred");
				System.out.println(e.getMessage());
				
				// Closes the program
				
				System.exit(0);
			
			}
			return null;
			
		}
		
		// Create a string with the project info and write it to the file
		
	public static void createProject(Map<String, HashMap<String, ProcessCase>> project, PrintWriter projectOutput){
			projectOutput.println("Project"+DEFAULT_SEPARATOR+"EntityName"+DEFAULT_SEPARATOR+"Time(milli)"+DEFAULT_SEPARATOR+"Time(mins)");
			Iterator<Entry<String, HashMap<String, ProcessCase>>> pit = project.entrySet().iterator();
		    while (pit.hasNext()) {
		        Map.Entry<String, HashMap<String, ProcessCase>> ppair = pit.next();
				
				Iterator<Entry<String, ProcessCase>> cit = ppair.getValue().entrySet().iterator();
			    while (cit.hasNext()) {
			        Map.Entry<String, ProcessCase> cpair = cit.next();
					long totalTime = cpair.getValue().getTotalTime();
					String minutesFromDiff = Integer.toString(DateManipulator.getMinutesFromDiff(totalTime));
					String processCaseInfo = ppair.getKey()+DEFAULT_SEPARATOR+cpair.getKey()+DEFAULT_SEPARATOR+totalTime+DEFAULT_SEPARATOR+minutesFromDiff;
					projectOutput.println(processCaseInfo);
					
			    }
			}
			
		}
		
}
