package engine.core;


public class Cube extends Primitive {
	
	private int[] indices = {
			0, 1, 2, 
			2, 1, 3, 
			4, 5, 6, 
			6, 5, 7, 
			8, 9, 10, 
			10, 9, 11, 
			12, 13, 14, 
			14, 13, 15, 
			16, 17, 18, 
			18, 17, 19, 
			20, 21, 22, 
			22, 21, 23
	};

	private float[] positions = {
			-0.5f, -0.5f, 0.5f, 
			0.5f, -0.5f, 0.5f, 
			-0.5f, 0.5f, 0.5f, 
			0.5f, 0.5f, 0.5f, 
			-0.5f, 0.5f, 0.5f, 
			0.5f, 0.5f, 0.5f, 
			-0.5f, 0.5f, -0.5f, 
			0.5f, 0.5f, -0.5f, 
			-0.5f, 0.5f, -0.5f, 
			0.5f, 0.5f, -0.5f, 
			-0.5f, -0.5f, -0.5f, 
			0.5f, -0.5f, -0.5f, 
			-0.5f, -0.5f, -0.5f, 
			0.5f, -0.5f, -0.5f, 
			-0.5f, -0.5f, 0.5f, 
			0.5f, -0.5f, 0.5f, 
			0.5f, -0.5f, 0.5f, 
			0.5f, -0.5f, -0.5f, 
			0.5f, 0.5f, 0.5f, 
			0.5f, 0.5f, -0.5f, 
			-0.5f, -0.5f, -0.5f, 
			-0.5f, -0.5f, 0.5f, 
			-0.5f, 0.5f, -0.5f, 
			-0.5f, 0.5f, 0.5f
	};
	
	private float[] texcoords = {
			0.0f, 1.0f, 
			1.0f, 1.0f, 
			0.0f, 0.0f, 
			1.0f, 0.0f, 
			0.0f, 1.0f, 
			1.0f, 1.0f, 
			0.0f, 0.0f, 
			1.0f, 0.0f, 
			0.0f, 0.0f, 
			1.0f, 0.0f, 
			0.0f, 1.0f, 
			1.0f, 1.0f, 
			0.0f, 0.0f, 
			1.0f, 0.0f, 
			0.0f, 1.0f, 
			1.0f, 1.0f, 
			0.0f, 1.0f, 
			1.0f, 1.0f, 
			0.0f, 0.0f, 
			1.0f, 0.0f, 
			1.0f, 1.0f, 
			0.0f, 1.0f, 
			1.0f, 0.0f, 
			0.0f, 0.0f
	};
	
	private float[] normals = {
			0.0f, 0.0f, 1.0f, 
			0.0f, 0.0f, 1.0f, 
			0.0f, 0.0f, 1.0f, 
			0.0f, 0.0f, 1.0f, 
			0.0f, 1.0f, 0.0f, 
			0.0f, 1.0f, 0.0f, 
			0.0f, 1.0f, 0.0f, 
			0.0f, 1.0f, 0.0f, 
			0.0f, 0.0f, -1.0f, 
			0.0f, 0.0f, -1.0f, 
			0.0f, 0.0f, -1.0f, 
			0.0f, 0.0f, -1.0f, 
			0.0f, -1.0f, 0.0f, 
			0.0f, -1.0f, 0.0f, 
			0.0f, -1.0f, 0.0f, 
			0.0f, -1.0f, 0.0f, 
			1.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f, 
			-1.0f, 0.0f, 0.0f, 
			-1.0f, 0.0f, 0.0f, 
			-1.0f, 0.0f, 0.0f, 
			-1.0f, 0.0f, 0.0f
	};
	
	private float[] tangents = {
			1.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f, 
			1.0f, -0.0f, 0.0f, 
			1.0f, -0.0f, 0.0f, 
			1.0f, -0.0f, 0.0f, 
			1.0f, -0.0f, -0.0f, 
			1.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f, 
			1.0f, -0.0f, 0.0f, 
			0.0f, 0.0f, -1.0f, 
			0.0f, 0.0f, -1.0f, 
			0.0f, 0.0f, -1.0f, 
			0.0f, 0.0f, -1.0f, 
			-0.0f, -0.0f, -1.0f, 
			-0.0f, -0.0f, -1.0f, 
			-0.0f, -0.0f, -1.0f, 
			-0.0f, -0.0f, -1.0f
	};
	
	public Cube(LowLevelTexture texture) {
		super(texture);
		super.positions = this.positions;
		super.indices = this.indices;
		super.texcoords = this.texcoords;
		super.normals = this.normals;
		super.tangents = this.tangents;
	}
	
}
