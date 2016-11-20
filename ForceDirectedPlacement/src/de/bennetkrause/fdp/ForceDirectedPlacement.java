package de.bennetkrause.fdp;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;
import javax.vecmath.Vector2d;

import de.bennetkrause.fdp.graph.Edge;
import de.bennetkrause.fdp.graph.Graph;
import de.bennetkrause.fdp.graph.Vertex;

public class ForceDirectedPlacement implements Runnable {

	private Graph g;

	// command line arguments
	private int frameWidth;
	private int frameHeight;
	private boolean equi;
	private double netForceThreshold;
	private int iterations;
	private double coolingRate;
	private int delay;
	private boolean chart;

	private int iteration = 0;

	private int area;
	private double k;
	private double t;

	private boolean equilibriumReached = false;

	public ForceDirectedPlacement(Graph g, int frameWidth, int frameHeight, boolean equi, double netForceThreshold,
			int iterations, double coolingRate, int delay, boolean chart) {
		this.g = g;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		this.equi = equi;
		this.netForceThreshold = netForceThreshold;
		this.iterations = iterations;
		this.coolingRate = coolingRate;
		this.delay = delay;
		this.chart = chart;
	}

	/**
	 * Starts the simulation.
	 * 
	 * @return number of iterations used
	 */
	private int startSimulation(boolean forceAttrLog) {

		iteration = 0;
		equilibriumReached = false;

		area = frameWidth * frameHeight;
		k = Math.sqrt(area / g.getVertices().size());
		t = frameWidth / 10;

		// assign random initial positions to all vertices
		for (Vertex v : g.getVertices()) {
			v.randomPos(frameWidth, frameHeight);
		}

		if (equi) {
			// simulate until mechanical equilibrium
			while (!equilibriumReached && iteration < 5000) {
				simulateStep(forceAttrLog);
			}
		} else {
			// simulate iterations-steps
			for (int i = 0; i < iterations; i++) {
				simulateStep(forceAttrLog);
			}
		}
		return iteration;
	}

	/**
	 * Simulates a single step.
	 */
	private void simulateStep(boolean forceAttrLog) {

		// calculate repulsive forces (from every vertex to every other)
		for (Vertex v : g.getVertices()) {
			// reset displacement vector for new calculation
			v.getDisp().set(0, 0);
			for (Vertex u : g.getVertices()) {
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
		for (Edge e : g.getEdges()) {

			// normalized difference position vector of v and u
			Vector2d deltaPos = new Vector2d();
			deltaPos.sub(e.getV().getPos(), e.getU().getPos());
			double length = deltaPos.length();
			deltaPos.normalize();

			// displacements depending on attractive force
			if (forceAttrLog) {
				deltaPos.scale(this.forceAttractiveLog(length, k));
			} else {
				deltaPos.scale(this.forceAttractive(length, k));
			}
			e.getV().getDisp().sub(deltaPos);
			e.getU().getDisp().add(deltaPos);
		}

		// assume equilibrium
		equilibriumReached = true;

		for (Vertex v : g.getVertices()) {

			Vector2d disp = new Vector2d(v.getDisp());
			double length = disp.length();

			// no equilibrium if one vertex has too high net force
			if (length > netForceThreshold) {
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

	private double forceAttractiveLog(double d, double k) {
		return k * Math.log10(d);
	}

	private double forceRepulsive(double d, double k) {
		return (k * k) / d;
	}

	private Map<Double, Integer> optimizeCoolingRate(boolean forceAttrLog) {
		Map<Double, Integer> res = new HashMap<>();
		for (coolingRate = 0.005; coolingRate <= 1; coolingRate += 0.001) {
			int iters = 0;
			for (int i = 0; i < 25; i++) {
				iters += startSimulation(forceAttrLog);
			}
			iters /= 25;
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
		if (chart) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					new CoolingChartJFrame(optimizeCoolingRate(false), optimizeCoolingRate(true)).setVisible(true);
				}
			});
		} else {
			startSimulation(false);
		}
	}
}
