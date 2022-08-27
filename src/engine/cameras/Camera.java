package engine.cameras;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.objects.WorldObject;
import engine.util.Utils;

public abstract class Camera extends WorldObject 
{
	//==============================================================================
	// Protected fields
	//==============================================================================
	
	protected float yaw = -90.0f;
	protected float pitch = 0.0f;
	protected float roll = 0.0f;
	protected float sensitivity = 0.1f;
	protected float moveSpeed = 2.5f;
	protected float fov = 90.0f;
	
	protected Vector3f front;
	protected Vector3f up;
	protected Vector3f right;
	protected Vector3f worldUp = new Vector3f(0, 1, 0);
	
	//==============================================================================
	// Abstract methods
	//==============================================================================

	protected abstract void update(float xoffset, float yoffset);
	
	//==============================================================================
	// Public methods
	//==============================================================================

	public Camera() 
	{ 
		super(); 
		this.updateVectors();
	}
	
	public Camera(Vector3f position) 
	{
		super(position);
		this.updateVectors();
	}
	
	public Camera(Vector3f position, float yaw, float pitch) 
	{
		super(position);
		this.yaw = yaw;
		this.pitch = pitch;
		this.updateVectors();
	}
	
	public void tick(float xoffset, float yoffset)
	{
		xoffset *= sensitivity;
		yoffset *= sensitivity;
		this.update(xoffset, yoffset);
		this.restrictYaw();
		this.updateVectors();
	}
	
	public void tick()
	{
		this.update(0, 0);
		this.updateVectors();
	}
	
	public Matrix4f getViewMatrix() {
		return Utils.createLookAtMatrix(position, Utils.add(position, front), up);
	}
	
	public float getFOV() {
		return fov;
	}
	
	public Vector3f getLookDirection() {
		return front;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getRoll() {
		return roll;
	}
	
	public Camera setYaw(float yaw) {
		this.yaw = yaw;
		this.tick();
		return this;
	}
	
	public Camera setPitch(float pitch) {
		this.pitch = pitch;
		return this;
	}
	
	public Camera setRoll(float roll) {
		this.roll = roll;
		return this;
	}
	
	//==============================================================================
	// Protected methods
	//==============================================================================
	
	protected void restrictYaw() {
		while(yaw < 0.0f) yaw += 360.0f;
		while(yaw > 360.0f) yaw -= 360.0f;
	}
	
	protected void restrictPitch() {
		if(pitch < -89.0f) pitch = -89.0f;
		if(pitch > 89.0f) pitch = 89.0f;
	}
	
	protected void updateVectors()
	{
		Vector3f front = new Vector3f();
		front.x = (float) Math.cos(Math.toRadians(pitch)) * (float) Math.cos(Math.toRadians(yaw));
		front.y = (float) Math.sin(Math.toRadians(pitch));
		front.z = (float) Math.cos(Math.toRadians(pitch)) * (float) Math.sin(Math.toRadians(yaw));
		this.front = Utils.normalize(front);
		this.right = Utils.normalize(Utils.cross(this.front, this.worldUp));
		this.up = Utils.normalize(Utils.cross(this.right, this.front));
	}
}
