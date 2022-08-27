package engine.cameras;

import net.buttology.lwjgl.swt.InputHandler;

import org.lwjgl.util.vector.Matrix4f;

import engine.util.Utils;

public class TPCamera extends Camera {

	//==============================================================================
	// Private fields
	//==============================================================================
	
	private TPCameraPivot pivot;
	private float distance = 5;
	private float angle = 45;
	private InputHandler hid;
	
	//==============================================================================
	// Public methods
	//==============================================================================
	
	public TPCamera(InputHandler hid)
	{
		super();
		this.hid = hid;
		this.init();
	}
	
	public void update(float xoffset, float yoffset)
	{
		calcZoom(yoffset);
		calcPitch(yoffset);
		calcAngle(xoffset);
//		super.restrictPitch();
		calcCameraPosition(calcHDistance(), calcVDistance());
		super.yaw = 180 - (pivot.getRotation().y + angle);
	}
	
	@Override
	public Matrix4f getViewMatrix() {
		return Utils.createViewMatrix(this);
	}
	
	@Override
	public void increasePosition(float x, float y, float z)
	{
		pivot.increasePosition(x, y, z);
	}
	
	@Override
	public void setPosition(float x, float y, float z)
	{
		pivot.setPosition(x, y, z);
	}
	
	public void setAngle(float f)
	{
		angle = f;
	}

	//==============================================================================
	// Private methods
	//==============================================================================
	
	private void init()
	{
		this.pivot = new TPCameraPivot();
		super.sensitivity = 0.15f;
		super.tick();
	}
	
	private void calcCameraPosition(float hDistance, float vDistance)
	{
		float theta = pivot.getRotation().y + angle;
		float offsetX = (float) (hDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (hDistance * Math.cos(Math.toRadians(theta)));
		position.x = pivot.getPosition().x - offsetX;
		position.y = pivot.getPosition().y + vDistance;
		position.z = pivot.getPosition().z - offsetZ;
	}
	
	private float calcHDistance() {
		return (float) (distance * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calcVDistance() {
		return (float) (distance * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calcZoom(float yoffset)
	{
		float zoom = hid.getScrollAmount() * sensitivity;
		
		if(hid.getMouseButtonPressed(3))
		{
			zoom = yoffset * sensitivity;
		}
		
		distance -= zoom;
		
		if(distance < 0) distance = 0;
		if(distance > 200) distance = 200;
	}
	
	private void calcAngle(float xoffset)
	{
		if(hid.getMouseButtonPressed(1))
		{
			float delta = xoffset;
			angle -= delta;
		}
	}

	private void calcPitch(float xoffset)
	{
		if(hid.getMouseButtonPressed(1))
		{
			float delta = xoffset;
			pitch -= delta;
		}
	}
}
