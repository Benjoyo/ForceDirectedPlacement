package fdp.graph;

public class VertexFactory implements org.jgrapht.VertexFactory<Vertex> {

	@Override
	public Vertex createVertex() {
		return new Vertex();
	}
}
