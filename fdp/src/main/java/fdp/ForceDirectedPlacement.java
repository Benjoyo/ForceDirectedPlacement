package fdp;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.vecmath.Vector2d;

import org.jgrapht.Graph;
import org.quark.jasmine.Compile;
import org.quark.jasmine.Expression;

import fdp.graph.Edge;
import fdp.graph.Vertex;

public class ForceDirectedPlacement implements Runnable {

	private Graph<Vertex, Edge> g;

	// command line arguments
	private int frameWidth;
	private int frameHeight;
	private boolean equi;
	private double criterion;
	private double coolingRate;
	private int delay;

	private int iteration = 0;

	private int area;
	private double k;
	private double t;
	private Expression fa;
	private Expression fr;

	private boolean equilibriumReached = false;

	public ForceDirectedPlacement(Graph<Vertex, Edge> g, int frameWidth, int frameHeight, String attractiveForce, String repulsiveForce, boolean equi, double criterion,
			double coolingRate, int delay) {
		this.g = g;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		this.equi = equi;
		this.criterion = criterion;
		this.coolingRate = coolingRate;
		this.delay = delay;
		System.out.println(attractiveForce + repulsiveForce);
		this.fa = Compile.expression(attractiveForce, false);
		this.fr = Compile.expression(repulsiveForce, false);
	}

	/**
	 * Starts the simulation.
	 * 
	 * @return number of iterations used
	 */
	private int startSimulation() {

		iteration = 0;
		equilibriumReached = false;

		area = Math.min(frameWidth * frameWidth, frameHeight * frameHeight);
		k = Math.sqrt(area / g.vertexSet().size());
		t = frameWidth / 10;

		// assign random initial positions to all vertices
		for (Vertex v : g.vertexSet()) {
			v.randomPos(frameWidth, frameHeight);
		}

		if (equi) {
			// simulate until mechanical equilibrium
			while (!equilibriumReached && iteration < 5000) {
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
		for (Vertex v : g.vertexSet()) {
			// reset displacement vector for new calculation
			v.getDisp().set(0, 0);
			for (Vertex u : g.vertexSet()) {
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
		for (Edge e : g.edgeSet()) {

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

		for (Vertex v : g.vertexSet()) {

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
	private double forceAttractive(double d, double k) {
		return (d * d) / k;
	}
	
	private double forceRepulsive(double d, double k) {
		return (k * k) / d;
	}


//	private double forceAttractive(double d, double k) {
//		return fa.eval(d, k).answer().toDouble();
//	}
//
//	private double forceRepulsive(double d, double k) {
//		return fr.eval(d, k).answer().toDouble();
//	}

	private Map<Double, Integer> optimizeCoolingRate() {
		Map<Double, Integer> res = new HashMap<>();
		for (coolingRate = 0.005; coolingRate <= 0.4; coolingRate += 0.001) {
			int iters = 0;
			for (int i = 0; i < 5; i++) {
				iters += startSimulation();
			}
			iters /= 5;
			res.put(coolingRate, iters);
			System.out.println("r: " + (float) coolingRate + " : " + iters);
		}
		Entry<Double, Integer> min = null;
		for (Entry<Double, Integer> entry : res.entrySet()) {
			if (min == null || min.getValue() > entry.getValue()) {
				min = entry;
			}
		}
		System.out.println("Best result: r=" + min.getKey().floatValue() + "; " + min.getValue());
		return res;
	}

	@Override
	public void run() {
//		if (chart) {
//			SwingUtilities.invokeLater(new Runnable() {
//				@Override
//				public void run() {
//					new CoolingChartJFrame(optimizeCoolingRate(false), optimizeCoolingRate(true)).setVisible(true);
//				}
//			});
//		} else {
			startSimulation();
		
	}
}
