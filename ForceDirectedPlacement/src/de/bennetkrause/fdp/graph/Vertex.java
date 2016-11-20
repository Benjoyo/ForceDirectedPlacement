package de.bennetkrause.fdp.graph;

import javax.vecmath.Vector2d;

public class Vertex {
	
	private Vector2d pos = new Vector2d();
	private Vector2d disp = new Vector2d();
	
	public void randomPos(int width, int height) {
		this.pos.x = Math.random() * width;
		this.pos.y = Math.random() * height;
	}

	public Vector2d getPos() {
		return pos;
	}

	public void setPos(Vector2d pos) {
		this.pos = pos;
	}

	public Vector2d getDisp() {
		return disp;
	}

	public void setDisp(Vector2d disp) {
		this.disp = disp;
	}

}
