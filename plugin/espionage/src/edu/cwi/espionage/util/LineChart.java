package edu.cwi.espionage.util;

import java.time.Instant;
import java.util.Date;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import edu.cwi.espionage.model.ProcessCase;

public class LineChart extends JFrame {

	private static final long serialVersionUID = 1L;

	public LineChart() {
		this("");

	}
	public LineChart(String applicationTitle) {
		super(applicationTitle);

	}

	/**
	 * Creates a sample dataset
	 */
	private CategoryDataset createDataset(ProcessCase processCase) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		for (ProcessCase p : processCase.getByDate()) {
			result.addValue(DateManipulator.getMinutesFromDiff(p.getTotalTime()), "Amount of Time", DateManipulator
					.getFormatedDate(Date.from(Instant.ofEpochSecond(p.getLastEventTime())), "yyyy-MM-dd"));
		}
		return result;

	}

	/**
	 * Creates a chart
	 */
	public JFreeChart createChart(String title, ProcessCase processCase) {
		CategoryDataset dataset = createDataset(processCase);
		JFreeChart chart = ChartFactory.createLineChart(title, "DAYS", "EFFORT TIME (mins)", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		return chart;

	}
	
	public ChartPanel getLineChartPanel(String title, ProcessCase processCase) {
		return new ChartPanel(createChart(title, processCase));
	}
}