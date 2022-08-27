package engine.util;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import engine.cameras.Camera;
import engine.main.Application;

public class Utils {

	/** Returns the byte size of these integers. */
	public static int sizeOfInt(int i) {
		return i*4;
	}

	/** Creates a new IntBuffer and stores the data within it before returning it. */
	public static IntBuffer newIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	/** Creates a new FloatBuffer and stores the data within it before returning it. */
	public static FloatBuffer newFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	/** Creates a new ByteBuffer and stores the data within it before returning it. */
	public static ByteBuffer newByteBuffer(byte[] data) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	/** 
	 * Creates a projection matrix based on the FOV, near plane and far plane.
	 * @param fov
	 * @param near_plane
	 * @param far_plane
	 * @return the projection matrix
	 */
	public static Matrix4f createProjectionMatrix(float fov, float aspect_ratio, float near_plane, float far_plane) {
		float aspect_limit = aspect_ratio;
//		if(aspect_limit > 1.8f) aspect_limit = 1.8f;
		
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(fov / 2f))) * aspect_limit);
		float x_scale = y_scale / aspect_ratio;
		float frustum_length = far_plane - near_plane;
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((far_plane + near_plane) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * near_plane * far_plane) / frustum_length);
		projectionMatrix.m33 = 0;
		return projectionMatrix;
	}
	
	/**
	 * Creates a transformation matrix based on the given 3D translation, rotation and scale.
	 * @param translation
	 * @param rx
	 * @param ry
	 * @param rz
	 * @param scale
	 * @return
	 */
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, Vector3f scale) {
		Matrix4f matrix = new Matrix4f();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate(toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate(toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate(toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.scale(scale, matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
		return createTransformationMatrix(translation, rotation.x, rotation.y, rotation.z, scale);
	}
	
	/**
	 * Create a transformation matrix based on the given 2D transformation and scale.
	 * @param translation
	 * @param scale
	 * @return
	 */
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1.0f), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation) {
		Matrix4f matrix = new Matrix4f();
		Matrix4f.translate(translation, matrix, matrix);
		return matrix;
	}

	/**
	 * Returns a normalized version of the given vector.
	 * @param vector
	 * @return
	 */
	public static Vector3f normalize(Vector3f vector) {
		float length_of_vector = length(vector);
		return new Vector3f(vector.x / length_of_vector, vector.y / length_of_vector, vector.z / length_of_vector);
	}
	
	public static float length(Vector3f vector) {
		return (float) Math.sqrt((vector.x * vector.x) + (vector.y * vector.y) + (vector.z * vector.z));
	}
	
	public static float length2(Vector3f vector) {
		return (float) (vector.x * vector.x) + (vector.y * vector.y) + (vector.z * vector.z);
	}

	/**
	 * Create a view matrix based on the 3D position, the 3D target to look at, and the up axis.
	 * @param position
	 * @param target
	 * @param worldUp
	 * @return
	 */
	public static Matrix4f createLookAtMatrix(Vector3f position, Vector3f target, Vector3f worldUp) {

		// Compute direction from position to lookAt
		float dirX, dirY, dirZ;
		dirX = position.x - target.x;
		dirY = position.y - target.y;
		dirZ = position.z - target.z;
		// Normalize direction
		float invDirLength = 1.0f / (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
		dirX *= invDirLength;
		dirY *= invDirLength;
		dirZ *= invDirLength;
		// left = up x direction
		float leftX, leftY, leftZ;
		leftX = worldUp.y * dirZ - worldUp.z * dirY;
		leftY = worldUp.z * dirX - worldUp.x * dirZ;
		leftZ = worldUp.x * dirY - worldUp.y * dirX;
		// normalize left
		float invLeftLength = 1.0f / (float) Math.sqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
		leftX *= invLeftLength;
		leftY *= invLeftLength;
		leftZ *= invLeftLength;
		// up = direction x left
		float upnX = dirY * leftZ - dirZ * leftY;
		float upnY = dirZ * leftX - dirX * leftZ;
		float upnZ = dirX * leftY - dirY * leftX;

		Matrix4f lookAt = new Matrix4f();

		lookAt.m00 = leftX;
		lookAt.m01 = upnX;
		lookAt.m02 = dirX;
		lookAt.m03 = 0.0f;
		lookAt.m10 = leftY;
		lookAt.m11 = upnY;
		lookAt.m12 = dirY;
		lookAt.m13 = 0.0f;
		lookAt.m20 = leftZ;
		lookAt.m21 = upnZ;
		lookAt.m22 = dirZ;
		lookAt.m23 = 0.0f;
		lookAt.m30 = -(leftX * position.x + leftY * position.y + leftZ * position.z);
		lookAt.m31 = -(upnX * position.x + upnY * position.y + upnZ * position.z);
		lookAt.m32 = -(dirX * position.x + dirY * position.y + dirZ * position.z);
		lookAt.m33 = 1.0f;

		return lookAt;

		//		Vector3f z_axis = normalize(subtract(position, target));
		//		Vector3f x_axis = normalize(cross(normalize(worldUp), z_axis));
		//		Vector3f y_axis = cross(z_axis, x_axis);
		//		
		//		Matrix4f translation = new Matrix4f();
		//		translation.m30 = -position.x;
		//		translation.m31 = -position.y;
		//		translation.m32 = -position.z;
		//		Matrix4f rotation = new Matrix4f();
		//		rotation.m00 = x_axis.x;
		//		rotation.m10 = x_axis.y;
		//		rotation.m20 = x_axis.z;
		//		rotation.m01 = y_axis.x;
		//		rotation.m11 = y_axis.y;
		//		rotation.m21 = y_axis.z;
		//		rotation.m02 = z_axis.x;
		//		rotation.m12 = z_axis.y;
		//		rotation.m22 = z_axis.z;
		//		
		//		Matrix4f lookAtMatrix = new Matrix4f();
		//		Matrix4f.mul(translation, rotation, lookAtMatrix);
		//		return lookAtMatrix;
	}
	
	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f view = new Matrix4f();
		view.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), view, view);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), view, view);
		Matrix4f.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), view, view);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, view, view);
		return view;
	}

	/**
	 * Return a vector that is the cross product of the two vectors.
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static Vector3f cross(Vector3f vector1, Vector3f vector2) {
		Vector3f cross = new Vector3f();
		Vector3f.cross(vector1, vector2, cross);
		return cross;
	}

	/**
	 * Return a vector that is the result of vector1 subtracted by vector2.
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static Vector3f subtract(Vector3f vector1, Vector3f vector2) {
		Vector3f result = new Vector3f();
		Vector3f.sub(vector1, vector2, result);
		return result;
	}
	
	/**
	 * Adds two vectors together.
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static Vector3f add(Vector3f vector1, Vector3f vector2) {
		Vector3f result = new Vector3f();
		Vector3f.add(vector1, vector2, result);
		return result;
	}
	
	/**
	 * Multiply the two vectors together.
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static Vector3f mul(Vector3f vector1, Vector3f vector2) {
		Vector3f result = new Vector3f();
		result.x = vector1.x * vector2.x;
		result.y = vector1.y * vector2.y;
		result.z = vector1.z * vector2.z;
		return result;
	}
	
	public static Vector3f mul(Vector3f vector, float value) {
		Vector3f result = new Vector3f();
		result.x = vector.x * value;
		result.y = vector.y * value;
		result.z = vector.z * value;
		return result;
	}
	
	/**
	 * Attempt to parse a float from a String carefully and if it fails, return a float of value 0.0f.
	 * @param string
	 * @return
	 */
	public static float parseFloat(String string) {
		if(string.isEmpty()) return 0.0f;
		try {
			return Float.parseFloat(string);
		} catch (NumberFormatException e) {
			return 0.0f;
		}
	}
	
	/**
	 * Convert a 4x4 matrix to a 3x3 matrix, disregarding the out-of-bounds information.
	 * @param mat4
	 * @return
	 */
	public static Matrix3f toMat3(Matrix4f mat4) {
		Matrix3f m = new Matrix3f();
		m.m00 = mat4.m00;
		m.m01 = mat4.m01;
		m.m02 = mat4.m02;
		m.m10 = mat4.m10;
		m.m11 = mat4.m11;
		m.m12 = mat4.m12;
		m.m20 = mat4.m20;
		m.m21 = mat4.m21;
		m.m22 = mat4.m22;
		return m;
	}
	
	/**
	 * Wraps a 3x3 matrix in a 4x4 identity matrix.
	 * @param mat3
	 * @return
	 */
	public static Matrix4f toMat4(Matrix3f mat3) {
		Matrix4f m = new Matrix4f();
		m.m00 = mat3.m00;
		m.m01 = mat3.m01;
		m.m02 = mat3.m02;
		m.m10 = mat3.m10;
		m.m11 = mat3.m11;
		m.m12 = mat3.m12;
		m.m20 = mat3.m20;
		m.m21 = mat3.m21;
		m.m22 = mat3.m22;
		return m;
	}
	
	public static float toRadians(float f) {
		return (float) Math.toRadians(f);
	}
	
	/**
	 * Positions a Shell at the center of the screen.
	 * @param shell = the Shell to center.
	 */
	public static void center(Shell shell) 
	{
        Rectangle bds = shell.getDisplay().getPrimaryMonitor().getBounds();
        Point p = shell.getSize();

        int nLeft = (bds.width - p.x) / 2;
        int nTop = (bds.height - p.y) / 2;

        shell.setBounds(nLeft, nTop, p.x, p.y);
    }
	
	/**
	 * Gets the refresh rate set to the current monitor device.
	 * @return
	 */
	public static int getMonitorRefreshRate() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for(GraphicsDevice gd : gs)
		{
			DisplayMode dm = gd.getDisplayMode();

			int rate = dm.getRefreshRate();
			if(rate == DisplayMode.REFRESH_RATE_UNKNOWN) 
			{
				System.err.println("Unknown refresh rate!"); 
			}
		}
		System.err.println("ERR: Could not get refresh rate! Defaulting to 60.");
		return 60;
	}
	
	public static String trimFileExtension(String file) {
		if(file.isEmpty()) return "";
		int i = getFileExtension(file).length();
		return file.substring(0, file.length()-i-1);
	}
	
	/** Returns a string of the file extension for this file */
	public static String getFileExtension(File file) {
//		if(file == null) return "";
		return getFileExtension(file.getName());
	}
	
	/** Returns a string of the file extension for this file */
	public static String getFileExtension(String file) {
		String ext = file;
		ext = ext.substring(ext.lastIndexOf('.') + 1);
		return ext;
	}
	
	/**
	 * Prompts a dialog message of type Error with the provided message, to let the user know something went wrong when they can't see the console.
	 * @param message
	 */
	public static void promptError(String message)
	{
		MessageBox m = new MessageBox(Application.getForm().getShell(), SWT.ICON_ERROR);
		m.setText("Error");
		m.setMessage(message);
		m.open();
	}
	
	/**
	 * Serialize an Object that extends Serializable. The memory print of this Object will be written to disk.
	 * @param obj - The object to serialize.
	 * @param file - The file to write to.
	 */
	public static void serialize(Object obj, String file)
	{
		try 
		{
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deserialize an Object that extends Serializable. Remember to cast this Object to the type you expect.
	 * @param file - The file to read from.
	 * @return An Object castable to the original serialized type.
	 */
	public static Object deserialize(File file)
	{
		try 
		{
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object o = ois.readObject();
			ois.close();
			fis.close();
			return o;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
