package main.java.de.bennetkrause.fdp.view.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultEdge;

import de.bennetkrause.fdp.graph.Vertex;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class WindowController implements Initializable {

	@FXML
	Pane pane;
	@FXML
	CheckBox iterationCheckBox;
	@FXML
	CheckBox mechEquiCheckBox;
	@FXML
	TextField criterionTextField;
	@FXML
	TextField attractiveForcesTextField;
	@FXML
	TextField repulsiveForcesTextField;
	@FXML
	TextField coolingRateTextField;
	@FXML
	ChoiceBox<GraphGenerator<Vertex, DefaultEdge, ?>> graphChoiceBox;
	@FXML
	TextField graphSizeTextField;
	@FXML
	TextField frameDelayTextField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Canvas canvas = new Canvas();
		pane.getChildren().add(canvas);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(2);

	}

	@FXML
	protected void stopCriterionSelected(ActionEvent e) {
		System.out.println("stop");
	}

	@FXML
	protected void findOptimumClicked(ActionEvent e) {
		System.out.println("opti");
	}

	@FXML
	protected void simulateClicked(ActionEvent e) {
		System.out.println("sim");
	}
}
