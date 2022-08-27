package engine.core;

import collada.ColladaModel;
import collada.ColladaParser;

public class Cylinder extends Primitive {
	
	private static final ColladaModel model;
	
	static
	{
		model = ColladaParser.loadModel("resources/core/models/editor_shape_cylinder.dae");
	}

	public Cylinder(LowLevelTexture texture) {
		super(texture);
		this.positions = model.getMeshes()[0].getPositions();
		this.indices = model.getMeshes()[0].getIndices();
		this.normals = model.getMeshes()[0].getNormals();
		this.tangents = model.getMeshes()[0].getTangents();
		this.texcoords = model.getMeshes()[0].getTexcoords();
	}

}
