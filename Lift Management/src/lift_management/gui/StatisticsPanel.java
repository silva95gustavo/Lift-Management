package lift_management.gui;

import java.awt.EventQueue;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.GradientPaintTransformType;

import java.awt.*;
import javax.swing.*;

import org.jfree.chart.plot.dial.*;
import org.jfree.ui.StandardGradientPaintTransformer;

import lift_management.agents.Lift;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

public class StatisticsPanel {
	//Requests by lift
	XYSeriesCollection datasetRequestsByLift = new XYSeriesCollection();

	//Load by Lift
	DefaultValueDataset datasetLiftLoad = new DefaultValueDataset(0);
	JLabel lbAvgLoad = new JLabel("Average load: 0");
	JSpinner spinnerLoadLift = new JSpinner();

	private static StatisticsPanel window = null;
	List<Lift> lifts;
	long ticks = 0;
	//TODO it should be used something else
	Color colors[] = {Color.BLACK, Color.BLUE, Color.CYAN, Color.GRAY, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.ORANGE, Color.RED};

	private StatisticsPanel() {
	}

	public static StatisticsPanel getInstance() {
		if (window == null) {
			window = new StatisticsPanel();
		}

		return window;
	}

	/**
	 * Launch the application.
	 * @param lifts 
	 */
	public void run(List<Lift> lifts) {
		this.lifts = lifts;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 * @wbp.parser.entryPoint
	 */
	private void initialize() {
		JFrame frmPerformance = new JFrame();
		frmPerformance.setTitle("Performance");
		frmPerformance.setBounds(100, 100, 450, 493);
		frmPerformance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmPerformance.getContentPane().add(tabbedPane, BorderLayout.NORTH);

		tabbedPane.addTab("Requests by Lift", null, requestsByLiftPane(), null);

		tabbedPane.addTab("Load by Lift", null, loadByLiftPane(), null);

		//TODO show the mean waiting time (also max and min)
		tabbedPane.addTab("Wait time", null, waitTimePane(), null);

		//TODO show current and total number of requests, (no) use time of the lift, distance traveled, min/max/avg load
		tabbedPane.addTab("Information by lift", null, informationByLiftPane(), null);

		frmPerformance.setVisible(true);
	}

	private Component loadByLiftPane() {
		DialPlot dialplot = new DialPlot();

		dialplot.setView(0.0D, -0.05D, 1.0D, 1.0D);
		dialplot.setDataset(0, datasetLiftLoad);

		StandardDialFrame standarddialframe = new StandardDialFrame();
		standarddialframe.setBackgroundPaint(Color.lightGray);
		standarddialframe.setForegroundPaint(Color.darkGray);
		dialplot.setDialFrame(standarddialframe);

		GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(170, 170, 220));
		DialBackground dialbackground = new DialBackground(gradientpaint);

		dialbackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
		dialplot.setBackground(dialbackground);

		DialTextAnnotation dialtextannotation = new DialTextAnnotation("Lift Load");
		dialtextannotation.setFont(new Font("Dialog", 1, 14));
		dialtextannotation.setRadius(0.69999999999999996D);
		dialplot.addLayer(dialtextannotation);

		DialValueIndicator dialvalueindicator = new DialValueIndicator(0);
		dialvalueindicator.setFont(new Font("Dialog", 0, 10));
		dialvalueindicator.setOutlinePaint(Color.darkGray);
		dialvalueindicator.setRadius(0.59999999999999998D);
		dialvalueindicator.setAngle(-103D);
		dialplot.addLayer(dialvalueindicator);

		StandardDialScale standarddialscale = new StandardDialScale(0D, lifts.get(0).getMaxWeight(), -120D, -300D, lifts.get(0).getMaxWeight() / 10, 4);
		standarddialscale.setTickRadius(0.88D);
		standarddialscale.setTickLabelOffset(0.14999999999999999D);
		standarddialscale.setTickLabelFont(new Font("Dialog", 0, 14));
		dialplot.addScale(0, standarddialscale);

		org.jfree.chart.plot.dial.DialPointer.Pin pin = new org.jfree.chart.plot.dial.DialPointer.Pin(1);
		pin.setRadius(0.55000000000000004D);
		dialplot.addPointer(pin);

		org.jfree.chart.plot.dial.DialPointer.Pointer pointer = new org.jfree.chart.plot.dial.DialPointer.Pointer(0);
		dialplot.addPointer(pointer);

		DialCap dialcap = new DialCap();
		dialcap.setRadius(0.10000000000000001D);
		dialplot.setCap(dialcap);

		JFreeChart jfreechart = new JFreeChart(dialplot);

		ChartPanel chartpanel = new ChartPanel(jfreechart);
		chartpanel.setPreferredSize(new Dimension(560, 370));
		chartpanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.anchor = GridBagConstraints.NORTH;
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 0;
		spinnerLoadLift.setModel(new SpinnerNumberModel(0, 0, lifts.size()-1, 1));
		chartpanel.add(spinnerLoadLift, gbc_spinner);

		chartpanel.add(lbAvgLoad);

		return chartpanel;
	}

	private Component informationByLiftPane() {
		return null;
	}

	private Component waitTimePane() {
		return null;
	}

	private Component requestsByLiftPane() {
		//each lift has a series
		for (int i = 0; i < lifts.size(); i++) {
			datasetRequestsByLift.addSeries(new XYSeries("Lift " + i));
		}

		//create the chart
		final JFreeChart chart = ChartFactory.createXYLineChart(
				"Lift Performance",
				"Ticks",
				"Number of Tasks",
				datasetRequestsByLift,             
				PlotOrientation.VERTICAL, true, false, false);

		final XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		//different colors for different lifts
		for (int i = 0; i < lifts.size(); i++) {
			renderer.setSeriesPaint(i, colors[i % colors.length]);
		}
		plot.setRenderer(renderer);

		//chart pane size
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 370));
		chartPanel.setMouseZoomable(true, false);

		return chartPanel;
	}

	private void updateRequestsByLiftDataset() {
		for (int i = 0; i < lifts.size(); i++) {
			datasetRequestsByLift.getSeries(i).add(ticks, lifts.get(i).getTasks().size());
		}
	}

	private void updateLoadByLift() {
		int liftIndex = (Integer) spinnerLoadLift.getValue();
		datasetLiftLoad.setValue(lifts.get(liftIndex).getCurrentWeight());
		lbAvgLoad.setText("Average load: " + lifts.get(liftIndex).getAvgLoad());
	}

	public void incTick(long ticksToNextRun) {
		ticks += ticksToNextRun;
		//TODO update info of the dataset
		updateRequestsByLiftDataset();
		updateLoadByLift();
	}

}
