package view.controller;

import java.util.List;

import fdp.GraphConfiguration;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class ChartWindowController {

	@FXML
	private LineChart<Double, Integer> chart;

	/**
	 * Called by the {@link FindOptimumController} to display the result of the cooling rate optimization.
	 * @param seriesList
	 * @param configs
	 */
	void showChart(List<List<Data<Double, Integer>>> seriesList, List<GraphConfiguration> configs) {
		
		chart.setCreateSymbols(false);

		for (int i = 0; i < seriesList.size(); i++) {
			XYChart.Series<Double, Integer> xySeries = new Series<>();
			xySeries.getData().addAll(seriesList.get(i));
			chart.getData().add(xySeries);
			xySeries.setName("Fa = " + configs.get(i).getParameter().getAttractiveForce() + "; Fr = " + configs.get(i).getParameter().getRepulsiveForce());
		}
	}
}
