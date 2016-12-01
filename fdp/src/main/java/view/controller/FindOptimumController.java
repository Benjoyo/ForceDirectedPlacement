package view.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import fdp.ForceDirectedPlacement;
import fdp.GraphConfiguration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import parsii.tokenizer.ParseException;

public class FindOptimumController {

	@FXML
	private TextField fromTextField;
	private double fromValue;
	@FXML
	private TextField toTextField;
	private double toValue;
	@FXML
	private TextField stepSizeTextField;
	private double stepSizeValue;
	@FXML
	private TextField sampleSizeTextField;
	private int sampleSizeValue;
	@FXML
	private CheckBox chartCheckBox;

	private List<GraphConfiguration> graphConfigurations;

	private double bestCoolingRate;

	@FXML
	private void initialize() {
		fromTextField.setText("0.005");
		toTextField.setText("0.99");
		stepSizeTextField.setText("0.001");
		sampleSizeTextField.setText("25");
	}

	@FXML
	private void findOptimumClicked(ActionEvent e) {
		if (!parseAndCheckFields()) {
			MainWindowController.showErrorDialog("Are you sure that all fields are filled with correct values?", "");
			return;
		}

		List<List<Data<Double, Integer>>> chartValuesList = new ArrayList<>();
		for (GraphConfiguration config : this.graphConfigurations) {
			try {
				chartValuesList.add(ForceDirectedPlacement.optimizeCoolingRate(config, fromValue, toValue, stepSizeValue, sampleSizeValue));
			} catch (ParseException e1) {
				MainWindowController.showErrorDialog("Parsing Error",
						"Please make sure that the entered expressions are correct.");
			}
		}
	
		// get the cooling rate that used the least iterations
		Collections.sort(chartValuesList.get(0), (x, y) -> x.getYValue().compareTo(y.getYValue()));
		bestCoolingRate = round(chartValuesList.get(0).get(0).getXValue(), 3);
		
		toTextField.getScene().getWindow().hide();
		
		if (chartCheckBox.isSelected()) {
			showChartWindow(chartValuesList);
		}
	}
	
	/**
	 * Shows the chart windows that displays the result in a chart.
	 * @param chartValuesList
	 */
	private void showChartWindow(List<List<Data<Double, Integer>>> chartValuesList) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/chart_window.fxml"));
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Stage stage = new Stage();
		stage.setTitle("Cooling Rate Chart");
		stage.setScene(new Scene(root));
		stage.setResizable(false);
		
		ChartWindowController controller = loader.<ChartWindowController>getController();
		controller.showChart(chartValuesList, graphConfigurations);
		stage.show();
	}

	/**
	 * Rounds a double value to places places.
	 * @param value the value to round
	 * @param places places to round to
	 * @return the rounded value
	 */
	private double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	/**
	 * Parses the TextFields into variables and checks if their values make sense.
	 * @return true if all fields are filled with correct values
	 */
	private boolean parseAndCheckFields() {
		if ((fromValue = NumberUtils.toDouble(fromTextField.getText(), -1)) <= 0 || fromValue >= 1) {
			return false;
		}
		if ((toValue = NumberUtils.toDouble(toTextField.getText(), -1)) <= 0 || toValue >= 1) {
			return false;
		}
		if ((stepSizeValue = NumberUtils.toDouble(stepSizeTextField.getText())) <= 0 || stepSizeValue >= 1) {
			return false;
		}
		if ((sampleSizeValue = NumberUtils.toInt(sampleSizeTextField.getText())) < 1) {
			return false;
		}
		return true;
	}
	
	public double getBestCoolingRate() {
		return bestCoolingRate;
	}

	void setGraphConfigurations(List<GraphConfiguration> graphConfigurations) {
		this.graphConfigurations = graphConfigurations;
	}
}
