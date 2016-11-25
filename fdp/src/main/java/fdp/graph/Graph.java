package fdp.graph;

import java.util.Set;

public class Graph {
	
	public Graph(Set<Vertex> vertices, Set<Edge> edges) {
		this.vertices = vertices;
		this.edges = edges;
	}

	private final Set<Vertex> vertices;
	private final Set<Edge> edges;
	
	public Set<Vertex> getVertices() {
		return vertices;
	}

	public Set<Edge> getEdges() {
		return edges;
	}
}
