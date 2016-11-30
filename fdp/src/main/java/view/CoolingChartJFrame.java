package view;

import java.awt.BorderLayout;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CoolingChartJFrame extends JFrame {

	private static final long serialVersionUID = -8440221437031065544L;

	public CoolingChartJFrame(Map<Double, Integer> map) {
        super("Cooling Rate Experiment");
 
        JPanel chartPanel = createChartPanel(map);
        add(chartPanel, BorderLayout.CENTER);
 
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
 
    private JPanel createChartPanel(Map<Double, Integer> map) {
    	String chartTitle = "Necessary Iterations for Different Cooling Rates";
        String xAxisLabel = "Cooling Rate";
        String yAxisLabel = "Iterations";
     
        XYDataset dataset = createDataset(map);
     
        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, rootPaneCheckingEnabled, rootPaneCheckingEnabled, rootPaneCheckingEnabled);
     
        return new ChartPanel(chart);
    }
 
    private XYDataset createDataset(Map<Double, Integer> map) {
    	XYSeriesCollection dataset = new XYSeriesCollection();
    	XYSeries series = new XYSeries("f_a = d^2 / k");
    	XYSeries seriesLog = new XYSeries("f_a = k log d");
    	 
    	for (Entry<Double, Integer> e : map.entrySet()) {
    		series.add(e.getKey(), e.getValue());
    	}
    	
//    	for (Entry<Double, Integer> e : logMap.entrySet()) {
//    		seriesLog.add(e.getKey(), e.getValue());
//    	}
    	 
    	dataset.addSeries(series);
    	//dataset.addSeries(seriesLog);
    	return dataset;
    }
}