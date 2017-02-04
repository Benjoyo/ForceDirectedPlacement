package fdp;

import java.util.concurrent.Callable;

import javax.vecmath.Vector2d;

import org.jgrapht.Graph;

import fdp.graph.Edge;
import fdp.graph.Vertex;
import parsii.eval.Expression;
import parsii.eval.Parser;
import parsii.eval.Scope;
import parsii.eval.Variable;
import parsii.tokenizer.ParseException;

public class Simulation implements Callable<Integer> {

	private Graph<Vertex, Edge> graph;

	private int frameWidth;
	private int frameHeight;
	private boolean equi;
	private double criterion;
	private double coolingRate;
	private int delay;
	
	private static final double C = 0.4;

	private int iteration = 0;

	private int area;
	private double k;
	private double t;

	private Scope scope = Scope.create();
	private Variable varD = scope.getVariable("d");
	private Variable varK = scope.getVariable("k");
	private Expression attractiveForceExpr;
	private Expression repulsiveForceExpr;

	private boolean equilibriumReached = false;

	/**
	 * Creates a new Simulation.
	 * 
	 * @param graph
	 * @param p
	 * @throws ParseException
	 */
	public Simulation(Graph<Vertex, Edge> graph, Parameter p) throws ParseException {
		this.graph = graph;
		this.frameWidth = p.getFrameWidth();
		this.frameHeight = p.getFrameHeight();
		this.equi = p.isEquilibriumCriterion();
		this.criterion = p.getCriterion();
		this.coolingRate = p.getCoolingRate();
		this.delay = p.getFrameDelay();

		// parse the force strings into Expressions that can be evaluated multiple times
		attractiveForceExpr = Parser.parse(p.getAttractiveForce(), scope);
		repulsiveForceExpr = Parser.parse(p.getRepulsiveForce(), scope);
	}

	/**
	 * Starts the simulation.
	 * 
	 * @return number of iterations used until criterion is met
	 */
	private int startSimulation() {

		iteration = 0;
		equilibriumReached = false;

		area = Math.min(frameWidth * frameWidth, frameHeight * frameHeight);
		k = C * Math.sqrt(area / graph.vertexSet().size());
		t = frameWidth / 10;

		// assign random initial positions to all vertices
		for (Vertex v : graph.vertexSet()) {
			v.randomPos(frameWidth, frameHeight);
		}

		if (equi) {
			// simulate until mechanical equilibrium
			while (!equilibriumReached && iteration < 1000) {
				simulateStep();
			}
		} else {
			// simulate iterations-steps
			for (int i = 0; i < criterion; i++) {
				simulateStep();
			}
		}
		return iteration;
	}

	/**
	 * Simulates a single step.
	 */
	private void simulateStep() {

		// calculate repulsive forces (from every vertex to every other)
		for (Vertex v : graph.vertexSet()) {
			// reset displacement vector for new calculation
			v.getDisp().set(0, 0);
			for (Vertex u : graph.vertexSet()) {
				if (!v.equals(u)) {
					// normalized difference position vector of v and u
					Vector2d deltaPos = new Vector2d();
					deltaPos.sub(v.getPos(), u.getPos());
					double length = deltaPos.length();
					deltaPos.normalize();

					// displacement depending on repulsive force
					deltaPos.scale(this.forceRepulsive(length, k));
					v.getDisp().add(deltaPos);
				}
			}
		}

		// calculate attractive forces (only between neighbors)
		for (Edge e : graph.edgeSet()) {

			// normalized difference position vector of v and u
			Vector2d deltaPos = new Vector2d();
			deltaPos.sub(e.getV().getPos(), e.getU().getPos());
			double length = deltaPos.length();
			deltaPos.normalize();

			// displacements depending on attractive force
			deltaPos.scale(this.forceAttractive(length, k));

			e.getV().getDisp().sub(deltaPos);
			e.getU().getDisp().add(deltaPos);
		}

		// assume equilibrium
		equilibriumReached = true;

		for (Vertex v : graph.vertexSet()) {

			Vector2d disp = new Vector2d(v.getDisp());
			double length = disp.length();

			// no equilibrium if one vertex has too high net force
			if (length > criterion) {
				equilibriumReached = false;
			}
			// System.out.print((int)length + "; ");
			// limit maximum displacement by temperature t
			disp.normalize();
			disp.scale(Math.min(length, t));
			v.getPos().add(disp);

			// prevent being displaced outside the frame
			v.getPos().x = Math.min(frameWidth, Math.max(0.0, v.getPos().x));
			v.getPos().y = Math.min(frameHeight, Math.max(0.0, v.getPos().y));
		}
		// System.out.println();
		// reduce the temperature as the layout approaches a better
		// configuration but always let vertices move at least 1px
		t = Math.max(t * (1 - coolingRate), 1);

		// System.out.println("t: " + (float) t);

		try {
			Thread.sleep(delay);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		iteration++;
	}

	/**
	 * Calculates the amount of the attractive force between vertices using the
	 * expression entered by the user.
	 * 
	 * @param d
	 *            the distance between the two vertices
	 * @param k
	 * @return amount of force
	 */
	private double forceAttractive(double d, double k) {
		varD.setValue(d);
		varK.setValue(k);
		return attractiveForceExpr.evaluate();
	}

	/**
	 * Calculates the amount of the repulsive force between vertices using the
	 * expression entered by the user.
	 * 
	 * @param d
	 *            the distance between the two vertices
	 * @param k
	 * @return amount of force
	 */
	private double forceRepulsive(double d, double k) {
		varD.setValue(d);
		varK.setValue(k);
		return repulsiveForceExpr.evaluate();
	}

	@Override
	public Integer call() throws Exception {
		return startSimulation();
	}
}
