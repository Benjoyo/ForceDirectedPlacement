package fdp;

import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.SimpleGraph;

import fdp.graph.Edge;
import fdp.graph.EdgeFactory;
import fdp.graph.Vertex;
import fdp.graph.VertexFactory;

/**
 * Container class that represents a kind of Graph and parameters later used in the simulation.
 * @author Bennet
 */
public class GraphConfiguration {

	private GraphGenerator<Vertex, Edge, ?> generator;
	private Parameter param;
	
	public GraphConfiguration(GraphGenerator<Vertex, Edge, ?> generator, Parameter param) {
		this.generator = generator;
		this.setParameter(param);
	}

	/**
	 * Generates a new Graph using the GraphGenerator.
	 * @return
	 */
	public Graph<Vertex, Edge> generateGraph() {
		Graph<Vertex, Edge> graph = new SimpleGraph<>(new EdgeFactory());
		this.generator.generateGraph(graph, new VertexFactory(), null);
		return graph;
	}

	public Parameter getParameter() {
		return param;
	}

	public void setParameter(Parameter param) {
		this.param = param;
	}
}
