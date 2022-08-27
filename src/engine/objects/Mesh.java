package engine.objects;

import engine.core.LowLevelMesh;
import engine.core.LowLevelTexture;
import engine.main.Application;
import engine.materials.ETextureUnitType;
import engine.materials.Material;

public class Mesh {

	private LowLevelMesh mesh;
	private Material material;
	private Model parent;
	
	public Mesh(LowLevelMesh mesh, Material material) {
		this.mesh = mesh;
		this.material = material;
	}
	
	public void setParent(Model parent) {
		this.parent = parent;
	}
	
	public LowLevelMesh getMesh() {
		return mesh;
	}
	
	public Material getMaterial() {
		return this.material;
	}
	
	public void setTexture(ETextureUnitType unitName, String filePath) {
		if(filePath == null || filePath.isEmpty()) {
			this.material.setTextureUnit(unitName, null);
			return;
		}
		LowLevelTexture llt = Application.getEngine().getTextureLoader().loadTexture(filePath);
		material.setTextureUnit(unitName, llt);
	}
	
	public Model getParent() {
		return parent;
	}
	
}
