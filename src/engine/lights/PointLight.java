package engine.lights;

import org.lwjgl.util.vector.Vector3f;

public class PointLight extends Light {

//	public static LowLevelTexture icon = Application.getInstance().getTextureLoader().loadTexture("resources/icons/BillboardLightPoint.png");
	
	private float radius = 1.0f;
	
	public PointLight(Vector3f pos, Vector3f color) 
	{
		super(pos, color);
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

}
