package edu.cwi.espionage.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import edu.cwi.espionage.util.FileLogManager;
import edu.cwi.espionage.util.FileType;
import java.awt.Frame;

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
		Composite composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		Frame frame = SWT_AWT.new_Frame(composite);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				TreeDisplay.createAndShowGUI(frame, logManager.getProject(FileType.MIMEC_CSV));
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}
