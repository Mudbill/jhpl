package engine.objects;

import engine.core.Cube;
import engine.core.LowLevelTexture;
import engine.main.Application;

public class Skybox {

	private Model shape;
	
	public Skybox(String path) {
		Cube c = new Cube(Application.getEngine().getTextureLoader().loadCubemapTexture(path)) {
			@Override
			public float[] getTexcoords() {
				return getPositions();
			}
		};
		this.shape = Application.getEngine().getLoader().loadPrimitive(c);
	}
	
	public Skybox(LowLevelTexture texture) {
		this.shape = Application.getEngine().getLoader().loadPrimitive(new Cube(texture) {
			@Override
			public float[] getTexcoords() {
				return getPositions();
			}
		});
	}
	
	public Mesh getShape() {
		return shape.getFirstMesh();
	}
	
}
