package fdp.graph;

public class EdgeFactory implements org.jgrapht.EdgeFactory<Vertex, Edge> {

	@Override
	public Edge createEdge(Vertex v, Vertex u) {
		return new Edge(v, u);
	}
}
