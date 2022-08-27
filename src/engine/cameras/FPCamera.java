package engine.cameras;

import net.buttology.lwjgl.swt.InputHandler;

import org.lwjgl.util.vector.Vector3f;

import engine.main.Application;
import engine.util.Utils;

public class FPCamera extends Camera
{	
	private InputHandler hid;
	
	public FPCamera(InputHandler hid, Vector3f position, float yaw, float pitch)
	{
		super(position, yaw, pitch);
		this.hid = hid;
	}
	
	public void update(float xoffset, float yoffset)
	{
		yaw += xoffset;
		pitch += yoffset;
		
		if(pitch >  89.0f) pitch =  89.0f;
		if(pitch < -89.0f) pitch = -89.0f;
		
		while(yaw > 360.0f) yaw -= 360.0f;
		while(yaw < 0.0f) yaw += 360.0f;
	}
	
	public void updateKeyboard()
	{
		float velocity = moveSpeed * Application.getForm().getGLComposite().getFrametimeDelta();
		if(hid.getKeyPressed('w')) position = Utils.add(position, Utils.mul(front, velocity));
		if(hid.getKeyPressed('s')) position = Utils.subtract(position, Utils.mul(front, velocity));
		if(hid.getKeyPressed('a')) position = Utils.subtract(position, Utils.mul(Utils.normalize(Utils.cross(front, up)), velocity));
		if(hid.getKeyPressed('d')) position = Utils.add(position, Utils.mul(Utils.normalize(Utils.cross(front, up)), velocity));
	}
}
