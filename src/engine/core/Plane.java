package engine.core;


public class Plane extends Primitive {

	private float[] positions = {
			-1, 0, 1,
			1, 0, 1,
			-1, 0, -1,
			1, 0, -1
	};
	
	private int[] indices = {
			0, 1, 2,
			2, 1, 3
	};
	
	private float[] texcoords = {
			0, 1,
			1, 1,
			0, 0,
			1, 0
	};
	
	private float[] normals = {
			0, 1, 0,
			0, 1, 0,
			0, 1, 0,
			0, 1, 0
	};
	
	private float[] tangents = {
			1.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f
	};
	
	public Plane(LowLevelTexture texture) {
		super(texture);
		super.positions = this.positions;
		super.indices = this.indices;
		super.texcoords = this.texcoords;
		super.normals = this.normals;
		super.tangents = this.tangents;
	}
}
