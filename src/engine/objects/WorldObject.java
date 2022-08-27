package engine.objects;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.util.Utils;

public abstract class WorldObject 
{
	protected Vector3f position;
	protected Vector3f rotation;
	protected Vector3f scale;
	
	protected WorldObject()
	{
		this.position = new Vector3f(0, 0, 0);
		this.rotation = new Vector3f(0, 0, 0);
		this.scale = new Vector3f(1, 1, 1);
	}
	
	protected WorldObject(Vector3f pos)
	{
		this.position = pos;
		this.rotation = new Vector3f(0, 0, 0);
		this.scale = new Vector3f(1, 1, 1);
	}
	
	protected WorldObject(Vector3f pos, Vector3f rot, Vector3f scale)
	{
		this.position = pos;
		this.rotation = rot;
		this.scale = scale;
	}

	public void increasePosition(float x, float y, float z) {
		position.x += x;
		position.y += y;
		position.z += z;
	}
	
	public void increaseRotation(float x, float y, float z) {
		rotation.x += x;
		rotation.y += y;
		rotation.z += z;
	}
	
	public void increaseScale(float x, float y, float z) {
		scale.x += x;
		scale.y += y;
		scale.z += z;
	}
	
	public Matrix4f getTransformationMatrix() {
		return Utils.createTransformationMatrix(position, rotation, scale);
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public void setPosition(float x, float y, float z) {
		this.position = new Vector3f(x, y, z);
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
	
	public void setRotation(float x, float y, float z) {
		this.rotation = new Vector3f(x, y, z);
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
	
	public void setScale(float x, float y, float z) {
		this.scale = new Vector3f(x, y, z);
	}

	@Override
	public String toString() {
		return "WorldObject [position=" + position + ", rotation=" + rotation
				+ ", scale=" + scale + "]";
	}
	
}
