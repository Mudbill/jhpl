package engine.gui;

import org.lwjgl.util.vector.Vector3f;

import engine.core.LowLevelTexture;

public class Billboard {

	private LowLevelTexture texture;
	private Vector3f position;
	
	public Billboard(LowLevelTexture texture, Vector3f position) {
		this.texture = texture;
		this.position = position;
	}
	
	public LowLevelTexture getTexture() {
		return texture;
	}
	
	public Vector3f getPosition() {
		return position;
	}
}
