package engine.core;


public abstract class Primitive {
	
	private LowLevelTexture texture;
	protected float[] positions;
	protected int[] indices;
	protected float[] texcoords;
	protected float[] normals;
	protected float[] tangents;
	
	public Primitive(LowLevelTexture texture) {
		this.texture = texture;
	}
	
	public float[] 	getPositions()	{ return positions; };
	public int[] 	getIndices()	{ return indices; };
	public float[] 	getTexcoords()	{ return texcoords; };
	public float[] 	getNormals()	{ return normals; };
	public float[] 	getTangents()	{ return tangents; };
	
	public LowLevelTexture getTexture() { return texture; }
	
}