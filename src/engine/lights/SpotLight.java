package engine.lights;

import org.lwjgl.util.vector.Vector3f;

public class SpotLight extends Light 
{
	private Vector3f angle;
	private float aspectRatio = 1.0f;
	private float fov = 45.0f;
	
	public SpotLight(Vector3f pos, Vector3f color) 
	{
		super(pos, color);
		this.angle = new Vector3f(0, 0, 0);
	}
	
	public SpotLight(Vector3f pos, Vector3f color, Vector3f angle)
	{
		super(pos, color);
		this.angle = angle;
	}

	public Vector3f getAngle() {
		return angle;
	}

	public void setAngle(Vector3f angle) {
		this.angle = angle;
	}

	public float getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(float aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	public float getFov() {
		return fov;
	}

	public void setFov(float fov) {
		this.fov = fov;
	}

}
