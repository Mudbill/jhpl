package engine.lights;

import org.lwjgl.util.vector.Vector3f;

import engine.objects.WorldObject;

public class Light extends WorldObject {

	private Vector3f color;
	
	public Light(Vector3f pos, Vector3f color) {
		super(pos);
		this.color = color;
	}
	
	public Vector3f getColor() {
		return color;
	}

}
