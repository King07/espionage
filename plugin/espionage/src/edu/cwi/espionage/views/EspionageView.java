package edu.cwi.espionage.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;

import edu.cwi.espionage.model.ProcessCase;
import edu.cwi.espionage.util.DateManipulator;
import edu.cwi.espionage.util.FileLogManager;
import edu.cwi.espionage.util.FileType;
import edu.cwi.espionage.util.FileUtils;
import edu.cwi.espionage.util.Utils;

import java.awt.Frame;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;

public class EspionageView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.cwi.espionage.views.SampleView";

	/**
	 * The constructor.
	 */
	public EspionageView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		FileLogManager logManager = new FileLogManager();
		Map<String, HashMap<String, ProcessCase>> project = logManager.getProject(FileType.MYLYN_XML);
		writeProjectToFile(project,FileType.MYLYN_XML);
		Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		Frame frame = SWT_AWT.new_Frame(composite);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				TreeDisplay.createAndShowGUI(frame, project);
			}
		});
	}

	private void writeProjectToFile(Map<String, HashMap<String, ProcessCase>> project, FileType fileType) {
		String currentDate = DateManipulator.getFormatedDate(Calendar.getInstance().getTime(), "yyyy-MM-dd_HH-mm-ss");
		PrintWriter projectOutput = FileUtils.createFile(Utils.getFullPath("/dataset/"+fileType+"_"+currentDate+".csv"));
		FileUtils.createProject(project, projectOutput);
		projectOutput.close();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}
