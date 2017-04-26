/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
package edu.cwi.espionage.views;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.jfree.chart.ChartPanel;
import edu.cwi.espionage.model.ProcessCase;
import edu.cwi.espionage.util.LineChart;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.Map.Entry;

public class TreeDisplay extends JPanel
                      implements TreeSelectionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ChartPanel infoPane;
    private LineChart lineChart;
    private JTree tree;
    
    //Optionally set the look and feel.
    private static boolean useSystemLookAndFeel = false;

    public TreeDisplay(Map<String, HashMap<String, ProcessCase>> project) {
        super(new GridLayout(1,0));

        //Create the nodes.
        DefaultMutableTreeNode top =
            new DefaultMutableTreeNode("Espionage");
        createNodes(top,project);

        //Create a tree that allows one selection at a time.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);
        //Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(tree);
        lineChart = new LineChart();
        infoPane = lineChart.getLineChartPanel("", new ProcessCase(""));
        JScrollPane infoView = new JScrollPane(infoPane);
        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(infoView);

        Dimension minimumSize = new Dimension(100, 50);
        infoPane.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(100); 
        splitPane.setPreferredSize(new Dimension(500, 300));

        //Add the split pane to this panel.
        add(splitPane);
    }

    /** Required by TreeSelectionListener interface. */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            ProcessCase processCase = (ProcessCase)nodeInfo;
            infoPane.setChart(lineChart.createChart(processCase.getCaseId(), processCase));
        }
    }


    private void createNodes(DefaultMutableTreeNode top, Map<String, HashMap<String, ProcessCase>> project) {
        DefaultMutableTreeNode pName = null;
        DefaultMutableTreeNode cName = null;

		
		Iterator<Entry<String, HashMap<String, ProcessCase>>> pit = project.entrySet().iterator();
	    while (pit.hasNext()) {
	        Map.Entry<String, HashMap<String, ProcessCase>> ppair = pit.next();
	        System.out.println(ppair.getKey());
	        pName = new DefaultMutableTreeNode(ppair.getKey());
			top.add(pName);
			
			Iterator<Entry<String, ProcessCase>> cit = ppair.getValue().entrySet().iterator();
		    while (cit.hasNext()) {
		        Map.Entry<String, ProcessCase> cpair = cit.next();
		        cName = new DefaultMutableTreeNode(cpair.getValue());
				pName.add(cName);
		    }
		}
		
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    public static void createAndShowGUI(Frame frame, Map<String, HashMap<String, ProcessCase>> project) {
        if (useSystemLookAndFeel) {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Couldn't use system look and feel.");
            }
        }

        //Add content to the window.
        frame.add(new TreeDisplay(project));

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

}