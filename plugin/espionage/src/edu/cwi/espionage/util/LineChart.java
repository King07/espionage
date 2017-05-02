package edu.cwi.espionage.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.time.Instant;
import java.util.Date;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
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
		CategoryAxis axis = chart.getCategoryPlot().getDomainAxis();
		axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		
		addPointToXY(chart);
		return chart;

	}
	public void addPointToXY(JFreeChart chart) {
		final CategoryPlot plot = (CategoryPlot) chart.getPlot(); 
		plot.setBackgroundPaint(Color.white); 
		plot.setRangeGridlinePaint(Color.lightGray); 
		plot.setDomainGridlinesVisible(true); 
		plot.setRangeGridlinesVisible(true); 

		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis(); 
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); 
		rangeAxis.setAutoRangeIncludesZero(true); 

		final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer(); 
		renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator()); 
		renderer.setSeriesStroke(0, new BasicStroke(2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
		renderer.setShapesVisible(true);
	}
	
	public ChartPanel getLineChartPanel(String title, ProcessCase processCase) {
		return new ChartPanel(createChart(title, processCase));
	}
}
