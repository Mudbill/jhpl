package engine.shaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.util.Log;

public class Shader {
	
	private int id;
	private String name;
	private boolean linked = false;
	private ShaderManager manager;
	
	//==============================================================================
	// Public
	//==============================================================================

	/**
	 * Get the ID for this shader program.
	 * @return
	 */
	public int getID() {
		return this.id;
	}
	
	/**
	 * Gets the name of this shader. The name is either supplied at creation, or if not, named SHADER + the number of currently created shaders.
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Activates this shader program, making it the current shader used for rendering calls. This overrides any previous calls to this or other shaders.
	 */
	public void use() {
		if(!linked) {
			Log.warn("Shader: Not linked yet!");
			return;
		}
		GL20.glUseProgram(id);
		manager.setActiveShader(this);
	}
	
	/**
	 * Sends a boolean to the shader program as an integer (0 or 1).
	 * @param name - The int uniform in the shader
	 * @param value - The boolean value to set.
	 */
	public void setBoolean(String name, boolean value) {
		GL20.glUniform1i(GL20.glGetUniformLocation(id, name), value ? 1 : 0);
	}
	
	/**
	 * Sends an integer to the shader program.
	 * @param name - The int uniform in the shader
	 * @param value - The int value to set
	 */
	public void setInt(String name, int value) {
		GL20.glUniform1i(GL20.glGetUniformLocation(id, name), value);
	}
	
	/**
	 * Sends a float to the shader program.
	 * @param name - The float uniform in the shader
	 * @param value - the float value to set
	 */
	public void setFloat(String name, float value) {
		GL20.glUniform1f(GL20.glGetUniformLocation(id, name), value);
	}
	
	/**
	 * Sends a 4x4 matrix to the shader program.
	 * @param name - The mat4 uniform in the shader
	 * @param matrix - The matrix value to set
	 */
	public void setMat4(String name, Matrix4f matrix) {
		FloatBuffer matrices = BufferUtils.createFloatBuffer(4*4);
		matrix.store(matrices);
		matrices.flip();
		GL20.glUniformMatrix4(GL20.glGetUniformLocation(id, name), false, matrices);
	}
	
	/**
	 * Sends a 3 float vector to the shader program.
	 * @param name - The vec3 uniform in the shader
	 * @param vector - The vector to set
	 */
	public void setVec3(String name, Vector3f vector) {
		FloatBuffer vectors = BufferUtils.createFloatBuffer(3);
		vector.store(vectors);
		vectors.flip();
		GL20.glUniform3(GL20.glGetUniformLocation(id, name), vectors);
	}
	
	//==============================================================================
	// Package private
	//==============================================================================
	
	/**
	 * Creates a new, empty shader program with the given name.
	 * @param name - The name used to refer to this shader. Must be unique.
	 */
	Shader(ShaderManager manager, String name) {
		this.manager = manager;
		this.name = name;
		this.create();
		Log.info("Created Shader with ID %d: %s", id, name);
	}
	
	/**
	 * Creates a new OpenGL shader program and stores the ID.
	 */
	void create() {
		this.id = GL20.glCreateProgram();
	}
	
	/**
	 * Extracts the source from the given file path and compiles it as the given shader type. The shader is then added to this shader program.
	 * @param type
	 * @param path
	 */
	void addShader(EShaderType type, String path) {
		File file = new File(path);
		if(!file.isFile())
		{
			Log.error("Shader file does not exist '%s'", file.getAbsolutePath());
			return;
		}
		
		String source = null;
		
		try 
		{
			Scanner scanner = new Scanner(file);
			StringBuilder sb = new StringBuilder();
			while(scanner.hasNextLine()) sb.append(scanner.nextLine() + System.lineSeparator());
			scanner.close();
			source = sb.toString();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		int id = GL20.glCreateShader(type.getType());
		
		GL20.glShaderSource(id, source);
		GL20.glCompileShader(id);
		
		int compileStatus = GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS);
		if(compileStatus == GL11.GL_FALSE)
		{
			String log = GL20.glGetShaderInfoLog(id, 512);
			Log.error("FAILED %s COMPILE:\n"+log, type.toString());
		}
		
		GL20.glAttachShader(this.id, id);
		GL20.glDeleteShader(id);
	}
	
	/**
	 * Ready this shader program, by linking the currently added shaders. Required for the shader to work.
	 */
	void link() {
		GL20.glLinkProgram(this.id);
		
		int linkStatus = GL20.glGetProgrami(this.id, GL20.GL_LINK_STATUS);
		if(linkStatus == GL11.GL_FALSE)
		{
			String log = GL20.glGetProgramInfoLog(this.id, 512);
			Log.error("FAILED SHADER PROGRAM LINKING:\n"+log);
			return;
		}
		
		linked = true;
	}
	
	/**
	 * Unassociate this shader from OpenGL. This is the last valid call to this shader.
	 */
	void destroy() {
		GL20.glDeleteProgram(this.id);
	}
	
}
