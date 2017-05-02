package edu.cwi.espionage.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import edu.cwi.espionage.model.ProcessCase;
import edu.cwi.espionage.util.FileLogManager;
import edu.cwi.espionage.util.FileType;
import edu.cwi.espionage.util.FlouriteXMLParser;
import edu.cwi.espionage.util.Utils;
import java.awt.Frame;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;



/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.cwi.espionage.views.SampleView";

	/**
	 * The constructor.
	 */
	public SampleView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		FileLogManager logManager = new FileLogManager();
//		Map<String, HashMap<String, ProcessCase>> mimec = logManager.getProject(FileType.MIMEC_CSV);
//		System.out.println(mimec);
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
