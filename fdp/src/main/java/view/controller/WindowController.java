package view.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.vecmath.Vector2d;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jgrapht.Graph;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.generate.GridGraphGenerator;
import org.jgrapht.generate.HyperCubeGraphGenerator;
import org.jgrapht.generate.LinearGraphGenerator;
import org.jgrapht.generate.RingGraphGenerator;
import org.jgrapht.generate.StarGraphGenerator;
import org.jgrapht.generate.WheelGraphGenerator;
import org.jgrapht.graph.SimpleGraph;
import org.quark.jasmine.Compile;

import fdp.ForceDirectedPlacement;
import fdp.graph.Edge;
import fdp.graph.EdgeFactory;
import fdp.graph.Vertex;
import fdp.graph.VertexFactory;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class WindowController {

	private static final Color VERTEX_FILL_COLOR = Color.WHITESMOKE;
	private static final Color VERTEX_CIRCLE_COLOR = Color.BLACK;
	private static final Color EDGE_COLOR = Color.BLACK;
	private static final int VERTEX_WIDTH = 16;

	@FXML
	private Pane pane;
	@FXML
	private HBox hbox;
	@FXML
	private RadioButton iterationRadioButton;
	@FXML
	private RadioButton mechEquiRadioButton;
	@FXML
	private TextField criterionTextField;
	private float criterionValue;
	@FXML
	private Label criterionLabel;
	@FXML
	private TextField attractiveForcesTextField;
	@FXML
	private TextField repulsiveForcesTextField;
	@FXML
	private TextField coolingRateTextField;
	private float coolingRateValue;
	@FXML
	private ChoiceBox<String> graphChoiceBox;
	@FXML
	private TextField graphSizeTextField;
	private int graphSizeValue;
	@FXML
	private TextField frameDelayTextField;
	private int frameDelayValue;
	
	private GraphicsContext gc;

	@FXML
	private void initialize() {
		
		// init Jasmine compiler
		Compile.init();

		Canvas canvas = new Canvas(pane.getPrefWidth(), pane.getPrefHeight());
		pane.getChildren().add(canvas);
		pane.widthProperty().addListener(o -> {
			canvas.setWidth(pane.getWidth());
			reset(canvas);
		});
		pane.heightProperty().addListener(o -> {
			canvas.setHeight(pane.getHeight());
			reset(canvas);
		});

		gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(2);
		reset(canvas);

		graphChoiceBox.getSelectionModel().select(0);
		criterionTextField.setText("15");
		attractiveForcesTextField.setText("(d * d) / k");
		repulsiveForcesTextField.setText("(k * k) / d");
		coolingRateTextField.setText("0.01");
		graphSizeTextField.setText("3");
		frameDelayTextField.setText("25");
	}

	private void reset(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.LIGHTSTEELBLUE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	private void drawGraph(GraphicsContext gc, Graph<Vertex, Edge> g) {
		reset(gc.getCanvas());
		for (Edge e : g.edgeSet()) {
			Vector2d uPos = e.getU().getPos();
			Vector2d vPos = e.getV().getPos();
			int d = VERTEX_WIDTH / 2;
			gc.setFill(EDGE_COLOR);
			gc.strokeLine(uPos.x + d, uPos.y + d, vPos.x + d, vPos.y + d);
		}
		for (Vertex v : g.vertexSet()) {
			gc.setFill(VERTEX_FILL_COLOR);
			gc.fillOval(v.getPos().x, v.getPos().y, VERTEX_WIDTH, VERTEX_WIDTH);
			gc.setFill(VERTEX_CIRCLE_COLOR);
			gc.strokeOval(v.getPos().x, v.getPos().y, VERTEX_WIDTH, VERTEX_WIDTH);
		}
	}

	private GraphGenerator<Vertex, Edge, ?> getSelectedGraphGenerator() {
		switch (graphChoiceBox.getSelectionModel().getSelectedItem()) {
		case "Random":
			return new GnmRandomGraphGenerator<>(graphSizeValue, graphSizeValue);
		case "Linear":
			return new LinearGraphGenerator<>(graphSizeValue);
		case "Grid":
			return new GridGraphGenerator<>(graphSizeValue, graphSizeValue);
		case "Ring":
			return new RingGraphGenerator<>(graphSizeValue);
		case "Star":
			return new StarGraphGenerator<>(graphSizeValue);
		case "Wheel":
			return new WheelGraphGenerator<>(graphSizeValue);
		case "Hyper Cube":
			return new HyperCubeGraphGenerator<>(graphSizeValue);
		}
		return null;
	}

	private void showErrorDialog(String title, String msg) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(title);
		alert.setContentText(msg);
		alert.showAndWait();
	}

	@FXML
	protected void stopCriterionSelected(ActionEvent e) {
		if (iterationRadioButton.isSelected()) {
			criterionLabel.setText("Iterations ");
			criterionTextField.setText("100");
		} else {
			criterionLabel.setText("Threshold");
			criterionTextField.setText("15");
		}
	}

	@FXML
	protected void findOptimumClicked(ActionEvent e) {

	}

	@FXML
	protected void simulateClicked(ActionEvent e) {
		if (!checkFields()) {
			showErrorDialog("Are you sure that all fields are filled with correct values?", "");
			return;
		}

		GraphGenerator<Vertex, Edge, ?> gen = getSelectedGraphGenerator();
		Map<String, String> forceFunctionMap = parseForceFunctions();
		System.out.println(Arrays.toString(forceFunctionMap.entrySet().toArray()));
		List<Graph<Vertex, Edge>> graphs = new ArrayList<>();

		for (Entry<String, String> entry : forceFunctionMap.entrySet()) {
			Graph<Vertex, Edge> g = new SimpleGraph<>(new EdgeFactory());
			gen.generateGraph(g, new VertexFactory(), null);
			graphs.add(g);

			int w = (int) pane.getWidth() - VERTEX_WIDTH;
			int h = (int) pane.getHeight() - VERTEX_WIDTH;
			boolean equi = mechEquiRadioButton.isSelected();
			new Thread(new ForceDirectedPlacement(g, w, h, entry.getKey(), entry.getValue(), equi, criterionValue, coolingRateValue, frameDelayValue)).start();
		}

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				for (Graph<Vertex, Edge> g : graphs) {
					drawGraph(gc, g);
				}
			}
		}.start();
	}

	private Map<String, String> parseForceFunctions() {
		String[] fas = attractiveForcesTextField.getText().split(";");
		String[] frs = repulsiveForcesTextField.getText().split(";");

		// fill up the shorter array to length of the longer one using its last
		// element
		if (fas.length > frs.length) {
			Arrays.fill(frs, frs.length - 1, fas.length, frs[frs.length - 1]);
		} else if (frs.length > fas.length) {
			Arrays.fill(fas, fas.length - 1, frs.length, fas[fas.length - 1]);
		}

		Map<String, String> map = new HashMap<String, String>();
		for (int index = 0; index < fas.length; index++) {
			map.put(fas[index], frs[index]);
		}

		return map;
	}

	private boolean checkFields() {
		if ((graphSizeValue = NumberUtils.toInt(graphSizeTextField.getText())) == 0) {
			return false;
		}
		if ((criterionValue = NumberUtils.toFloat(criterionTextField.getText(), -1)) < 0) {
			return false;
		}
		if ((coolingRateValue = NumberUtils.toFloat(coolingRateTextField.getText())) == 0) {
			return false;
		}
		if ((frameDelayValue = NumberUtils.toInt(frameDelayTextField.getText(), -1)) < 0) {
			return false;
		}
		if (StringUtils.isBlank(attractiveForcesTextField.getText())) {
			return false;
		}
		if (StringUtils.isBlank(repulsiveForcesTextField.getText())) {
			return false;
		}
		return true;
	}
}
