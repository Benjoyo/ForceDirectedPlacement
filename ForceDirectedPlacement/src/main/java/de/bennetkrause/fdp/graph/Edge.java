package de.bennetkrause.fdp.graph;

public class Edge {
	
	public Edge(Vertex v, Vertex u) {
		this.v = v;
		this.u = u;
	}

	private final Vertex v;
	private final Vertex u;
	
	public Vertex getV() {
		return v;
	}

	public Vertex getU() {
		return u;
	}
}
