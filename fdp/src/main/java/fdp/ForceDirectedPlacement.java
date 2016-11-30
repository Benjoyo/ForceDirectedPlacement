package fdp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import org.jgrapht.Graph;

import fdp.graph.Edge;
import fdp.graph.Vertex;
import parsii.tokenizer.ParseException;

public class ForceDirectedPlacement {

	public static void simulate(Graph<Vertex, Edge> graph, Parameter p) throws ParseException {
		Executors.newSingleThreadExecutor().submit(new Simulation(graph, p));
	}
	
	public static Map<Double, Integer> optimizeCoolingRate(GraphConfiguration config, double from, double to, double stepSize, int sampleSize) throws ParseException {
		
		Parameter p = config.getParameter();
		Map<Double, Integer> chartValues = new HashMap<>();
		ExecutorService exec = Executors.newFixedThreadPool(sampleSize);
		List<Future<Integer>> currResults = new ArrayList<>();
		config.getParameter().setEquilibriumCriterion(true);
		p.setFrameDelay(0);
		
		List<Graph<Vertex, Edge>> graphs = new ArrayList<>();
		IntStream.range(0, sampleSize).forEach(i -> graphs.add(config.generateGraph()));
		
		for (double currentCoolingRate = from; currentCoolingRate <= to; currentCoolingRate += stepSize) {
			int iterationsUsed = 0;
			p.setCoolingRate(currentCoolingRate);
			for (int i = 0; i < sampleSize; i++) {
				currResults.add(exec.submit(new Simulation(graphs.get(i), p)));
			}
			for (Future<Integer> result : currResults) {
				try {
					iterationsUsed += result.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
			iterationsUsed /= sampleSize;
			chartValues.put(currentCoolingRate, iterationsUsed);
			
			currResults.clear();
		}
		
		return chartValues;
	}
}
