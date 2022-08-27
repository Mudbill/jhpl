package engine;

import engine.core.LowLevelMesh;
import engine.core.LowLevelTexture;

public class TextureUnitData {

	private LowLevelTexture texture;
	private LowLevelMesh quad;
	
	public void setMesh(LowLevelMesh mesh) {
		this.quad = mesh;
	}
	
	public LowLevelMesh getMesh() {
		return this.quad;
	}
	
	public void setTexture(LowLevelTexture texture) {
		this.texture = texture;
	}
	
	public LowLevelTexture getTexture() {
		return this.texture;
	}
	
}
