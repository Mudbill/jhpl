package engine.core;

import engine.util.Log;

public class LowLevelMesh {

	private int vaoID;
	private int vertexCount;
	
	public LowLevelMesh(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		Log.info("Created LowLevelMesh with ID %d", vaoID);
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
}
