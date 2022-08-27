package engine.objects;

import engine.materials.ETextureUnitType;


public class Model extends WorldObject {

	private Mesh[] meshes;

	public Model(Mesh[] meshes) {
		this.meshes = meshes;
		for(Mesh mesh : meshes) mesh.setParent(this);
	}

	public Mesh[] getMeshes() {
		return meshes;
	}
	
	public Mesh getFirstMesh() {
		return meshes[0];
	}
	
	public void setTextureMap(ETextureUnitType type, String filePath) {
		for(Mesh mesh : meshes) mesh.setTexture(type, filePath);
	}
	
}
