package view.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.vecmath.Vector2d;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
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

import fdp.ForceDirectedPlacement;
import fdp.GraphConfiguration;
import fdp.Parameter;
import fdp.graph.Edge;
import fdp.graph.EdgeFactory;
import fdp.graph.Vertex;
import fdp.graph.VertexFactory;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import parsii.tokenizer.ParseException;

public class MainWindowController {

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
	private List<Graph<Vertex, Edge>> graphs = new ArrayList<>();

	private AnimationTimer animation = new AnimationTimer() {
		@Override
		public void handle(long now) {
			reset(gc.getCanvas());
			for (Graph<Vertex, Edge> g : graphs) {
				drawGraph(gc, g);
			}
		}
	};

	@FXML
	private void initialize() {

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
		frameDelayTextField.setText("5");

		// permanently draw any graphs
		animation.start();
	}

	private void reset(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.LIGHTSTEELBLUE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	private void drawGraph(GraphicsContext gc, Graph<Vertex, Edge> g) {
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

	static void showErrorDialog(String title, String msg) {
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
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/cooling_rate_settings.fxml"));
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Stage stage = new Stage();
		stage.initStyle(StageStyle.UTILITY);
		stage.setTitle("Optimal Cooling Rate");
		stage.setScene(new Scene(root));
		stage.setResizable(false);
		
		FindOptimumController controller = loader.<FindOptimumController>getController();
		controller.setGraphConfigurations(getGraphConfigurations());
		
		stage.showAndWait();
		if (FindOptimumController.bestCoolingRate != 0) {
			coolingRateTextField.setText(Double.toString(FindOptimumController.bestCoolingRate));
		}
	}

	@FXML
	protected void simulateClicked(ActionEvent e) {
		for (GraphConfiguration config : getGraphConfigurations()) {
			Graph<Vertex, Edge> graph = config.generateGraph();
			try {
				ForceDirectedPlacement.simulate(graph, config.getParameter());
			} catch (ParseException e1) {
				MainWindowController.showErrorDialog("Parsing Error", "Please make sure that the entered expressions are correct.");
			}
			this.graphs.add(graph);
		}
	}

	private List<Pair<String, String>> parseForceFunctions() {
		List<String> fas = new ArrayList<>(Arrays.asList(attractiveForcesTextField.getText().split(";")));
		List<String> frs = new ArrayList<>(Arrays.asList(repulsiveForcesTextField.getText().split(";")));
		return zipLists(fas, frs);
	}

	private List<Pair<String, String>> zipLists(List<String> xs, List<String> ys) {
		if (xs.size() >= ys.size()) {
			return IntStream.range(0, xs.size()).mapToObj(i -> Pair.of(xs.get(i), ys.get(Math.min(i, ys.size() - 1))))
					.collect(Collectors.toList());
		} else {
			return IntStream.range(0, ys.size()).mapToObj(i -> Pair.of(xs.get(Math.min(i, xs.size() - 1)), ys.get(i)))
					.collect(Collectors.toList());
		}
	}

	private List<GraphConfiguration> getGraphConfigurations() {
		
		List<GraphConfiguration> graphConfigs = new ArrayList<>();
		
		if (!parseAndCheckFields()) {
			showErrorDialog("Are you sure that all fields are filled with correct values?", "");
			return graphConfigs;
		}
		
		List<Pair<String, String>> forceFunctions = parseForceFunctions();
		this.graphs.clear();
		
		for (Pair<String, String> forces : forceFunctions) {
			
			Parameter p = new Parameter();
			p.setFrameWidth((int) pane.getWidth() - VERTEX_WIDTH);
			p.setFrameHeight((int) pane.getHeight() - VERTEX_WIDTH);
			p.setEquilibriumCriterion(mechEquiRadioButton.isSelected());
			p.setAttractiveForce(forces.getLeft());
			p.setRepulsiveForce(forces.getRight());
			p.setCriterion(criterionValue);
			p.setCoolingRate(coolingRateValue);
			p.setFrameDelay(frameDelayValue);
			
			graphConfigs.add(new GraphConfiguration(getSelectedGraphGenerator(), p));
		}
		return graphConfigs;
	}

	private boolean parseAndCheckFields() {
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
