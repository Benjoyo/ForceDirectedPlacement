package de.bennetkrause.fdp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import javax.vecmath.Vector2d;

import de.bennetkrause.fdp.graph.Edge;
import de.bennetkrause.fdp.graph.Graph;
import de.bennetkrause.fdp.graph.Vertex;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainWindow extends Application {

	private static final Color VERTEX_FILL_COLOR = Color.WHITESMOKE;
	private static final Color VERTEX_CIRCLE_COLOR = Color.BLACK;
	private static final Color EDGE_COLOR = Color.BLACK;
	private static final int VERTEX_WIDTH = 16;

	private int width = 500;
	private int height = 500;

	private boolean equi = true;
	private double netForceThreshold = 10;
	private int iterations = 100;
	private double coolingRate = 0.03;
	private int delay = 10;
	private boolean chart = false;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		parseParameters();

		primaryStage.setTitle("Force-directed Placement Demo");
		Group root = new Group();
		Canvas canvas = new Canvas(width + VERTEX_WIDTH, height + VERTEX_WIDTH);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(2);

		Graph g = createGraph();

		Runnable fdp = new ForceDirectedPlacement(g, width, height, equi, netForceThreshold, iterations, coolingRate,
				delay, chart);

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				drawGraph(gc, g);
			}
		}.start();

		root.getChildren().add(canvas);
		primaryStage.setScene(new Scene(root));
		primaryStage.setOnCloseRequest(e -> System.exit(0));
		primaryStage.show();

		new Thread(fdp).start();
	}

	private void drawGraph(GraphicsContext gc, Graph g) {
		reset(gc.getCanvas());
		for (Edge e : g.getEdges()) {
			Vector2d uPos = e.getU().getPos();
			Vector2d vPos = e.getV().getPos();
			int d = VERTEX_WIDTH / 2;
			gc.setFill(EDGE_COLOR);
			gc.strokeLine(uPos.x + d, uPos.y + d, vPos.x + d, vPos.y + d);
		}
		for (Vertex v : g.getVertices()) {
			gc.setFill(VERTEX_FILL_COLOR);
			gc.fillOval(v.getPos().x, v.getPos().y, VERTEX_WIDTH, VERTEX_WIDTH);
			gc.setFill(VERTEX_CIRCLE_COLOR);
			gc.strokeOval(v.getPos().x, v.getPos().y, VERTEX_WIDTH, VERTEX_WIDTH);
		}
	}

	private Graph createGraph() {
		return createGraph1();
		// return createTree1();
	}

	private Graph createGraph1() {
		Vertex a = new Vertex();
		Vertex b = new Vertex();
		Edge e1 = new Edge(a, b);

		Vertex c = new Vertex();
		Edge e2 = new Edge(c, b);

		Vertex d = new Vertex();
		Edge e3 = new Edge(d, b);

		Vertex e = new Vertex();
		Edge e4 = new Edge(e, b);

		Vertex f = new Vertex();
		Edge e5 = new Edge(f, b);

		Edge e6 = new Edge(a, c);
		Edge e7 = new Edge(c, d);
		Edge e8 = new Edge(d, e);
		Edge e9 = new Edge(e, f);
		Edge e10 = new Edge(f, a);

		return new Graph(new HashSet<>(Arrays.asList(a, b, c, d, e, f)),
				new HashSet<>(Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10)));
	}

	private Graph createGraph2() {

		Vertex a = new Vertex();
		Vertex b = new Vertex();
		Vertex c = new Vertex();
		Vertex d = new Vertex();
		Vertex e = new Vertex();
		Vertex f = new Vertex();
		Vertex g = new Vertex();
		Vertex h = new Vertex();

		Edge e1 = new Edge(a, b);
		Edge e2 = new Edge(a, c);
		Edge e3 = new Edge(a, e);

		Edge e4 = new Edge(b, d);
		Edge e5 = new Edge(b, f);

		Edge e6 = new Edge(c, d);
		Edge e7 = new Edge(c, g);

		Edge e8 = new Edge(d, h);

		Edge e9 = new Edge(e, f);
		Edge e10 = new Edge(e, g);

		Edge e11 = new Edge(f, h);

		Edge e12 = new Edge(g, h);

		return new Graph(new HashSet<>(Arrays.asList(a, b, c, d, e, f, g, h)),
				new HashSet<>(Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12)));
	}
	
	private Graph createGraph3() {

		Vertex a = new Vertex();
		Vertex b = new Vertex();
		Vertex c = new Vertex();

		Edge e1 = new Edge(a, b);
		Edge e2 = new Edge(a, c);
		Edge e3 = new Edge(b, c);

		return new Graph(new HashSet<>(Arrays.asList(a, b, c)),
				new HashSet<>(Arrays.asList(e1, e2, e3)));
	}

	private Graph createTree1() {
		Vertex a = new Vertex();
		Vertex b = new Vertex();
		Edge e1 = new Edge(a, b);

		Vertex c = new Vertex();
		Edge e2 = new Edge(a, c);

		Vertex d = new Vertex();
		Edge e3 = new Edge(d, b);

		Vertex e = new Vertex();
		Edge e4 = new Edge(e, b);

		Vertex h = new Vertex();
		Edge e5 = new Edge(h, d);

		Vertex i = new Vertex();
		Edge e6 = new Edge(i, d);

		Vertex f = new Vertex();
		Edge e7 = new Edge(f, c);

		Vertex g = new Vertex();
		Edge e8 = new Edge(g, c);

		Vertex j = new Vertex();
		Edge e9 = new Edge(j, f);

		Vertex k = new Vertex();
		Edge e10 = new Edge(k, f);

		Vertex l = new Vertex();
		Edge e11 = new Edge(l, k);

		Vertex m = new Vertex();
		Edge e12 = new Edge(m, k);

		return new Graph(new HashSet<>(Arrays.asList(a, b, c, d, e, f, g, h, i, j, k, l, m)),
				new HashSet<>(Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12)));
	}

	private void reset(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.LIGHTSTEELBLUE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	private void parseParameters() {
		Map<String, String> params = getParameters().getNamed();
		// frame size specified?
		if (params.containsKey("width") && params.containsKey("height")) {
			try {
				width = Integer.parseInt(params.get("width"));
				height = Integer.parseInt(params.get("height"));
			} catch (NumberFormatException e) {
				System.err.println("Invalid number as parameter.");
			}
		}
		// find optimal cooling rate instead of single simulation?
		if (params.containsKey("coolingRateChart")) {
			try {
				chart = Boolean.parseBoolean(params.get("coolingRateChart"));
			} catch (NumberFormatException e) {
				System.err.println("Invalid boolean as parameter.");
			}
		}
		// stop criterion specified?
		if (params.containsKey("iterations") ^ params.containsKey("threshold")) {
			if (params.containsKey("iterations")) {
				try {
					iterations = Integer.parseInt(params.get("iterations"));
				} catch (NumberFormatException e) {
					System.err.println("Invalid number as parameter.");
				}
				equi = false;
			} else {
				try {
					netForceThreshold = Double.parseDouble(params.get("threshold"));
				} catch (NumberFormatException e) {
					System.err.println("Invalid number as parameter.");
				}
			}
		}
		// cooling rate specified?
		if (params.containsKey("coolingRate")) {
			try {
				coolingRate = Double.parseDouble(params.get("coolingRate"));
			} catch (NumberFormatException e) {
				System.err.println("Invalid number as parameter.");
			}
		}
		// step delay specified?
		if (params.containsKey("delay")) {
			try {
				delay = Integer.parseInt(params.get("delay"));
			} catch (NumberFormatException e) {
				System.err.println("Invalid number as parameter.");
			}
		}
	}
}
