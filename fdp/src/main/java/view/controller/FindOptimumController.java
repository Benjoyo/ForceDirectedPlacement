package view.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.apache.commons.lang3.math.NumberUtils;

import fdp.ForceDirectedPlacement;
import fdp.GraphConfiguration;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import parsii.tokenizer.ParseException;
import view.CoolingChartJFrame;

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

	static double bestCoolingRate;

	@FXML
	private void initialize() {
		fromTextField.setText("0.005");
		toTextField.setText("0.99");
		stepSizeTextField.setText("0.001");
		sampleSizeTextField.setText("25");
	}

	@FXML
	private void findOptimumClicked(ActionEvent e) {
		if (!checkFields()) {
			MainWindowController.showErrorDialog("Are you sure that all fields are filled with correct values?", "");
			return;
		}

		List<Map<Double, Integer>> chartValuesList = new ArrayList<>();
		for (GraphConfiguration config : this.graphConfigurations) {
			try {
				chartValuesList.add(
						ForceDirectedPlacement.optimizeCoolingRate(config, fromValue, toValue, stepSizeValue, sampleSizeValue));
			} catch (ParseException e1) {
				MainWindowController.showErrorDialog("Parsing Error",
						"Please make sure that the entered expressions are correct.");
			}
		}

		bestCoolingRate = round(
				chartValuesList.get(0).entrySet().stream().sorted(Map.Entry.comparingByValue()).findFirst().get().getKey(), 3);
		System.out.println(bestCoolingRate);
		toTextField.getScene().getWindow().hide();

		if (chartCheckBox.isSelected()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					new CoolingChartJFrame(chartValuesList.get(0)).setVisible(true);
				}
			});
		}
	}

	private double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	private boolean checkFields() {
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

	void setGraphConfigurations(List<GraphConfiguration> graphConfigurations) {
		this.graphConfigurations = graphConfigurations;
	}
}
