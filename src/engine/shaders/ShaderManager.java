package engine.shaders;

import java.util.HashMap;
import java.util.Map;

import engine.util.Log;

public class ShaderManager {

	//==============================================================================
	// Private fields
	//==============================================================================
	
	/** Contains a map of all shaders created with this shader manager, identified by their given names. */
	private Map<String, Shader> shaderPrograms = new HashMap<String, Shader>();
	
	/** Contains the currently active shader in this shader manager. */
	private Shader activeShaderProgram;
	
	//==============================================================================
	// Public methods
	//==============================================================================
	
	public ShaderManager() {
		Log.info("Creating shader manager...");
	}
	
	/**
	 * Returns the currently active shader in this shader manager.
	 * @return
	 */
	public Shader getActiveShader() {
		return activeShaderProgram;
	}
	
	/**
	 * Returns the shader that matches the given name. If no such shader is found, null is returned.
	 * @param name
	 * @return
	 */
	public Shader getShader(String name)
	{
		if(shaderPrograms.containsKey(name)) return shaderPrograms.get(name);
		Log.error("No shader found with name '%s'", name);
		return null;
	}
	
	/**
	 * Destroys all created shaders.
	 */
	public void cleanUp()
	{
		for(Shader shader : shaderPrograms.values()) shader.destroy();
		shaderPrograms.clear();
	}
	
	/**
	 * Create a new shader and store it in this shader manager.
	 * @param name - A unique name used to identify this shader.
	 * @param vertexShader - The path to the vertex shader source.
	 * @param fragmentShader - The path to the fragment shader source.
	 * @return The created shader.
	 */
	public Shader createShader(String name, String vertexShader, String fragmentShader) {
		return createShader(name, vertexShader, null, fragmentShader);
	}
	
	/**
	 * Create a new shader and store it in this shader manager.
	 * @param name - A unique name used to identify this shader.
	 * @param vertexShader - The path to the vertex shader source.
	 * @param geometryShader - The path to the geometry shader source.
	 * @param fragmentShader - The path to the fragment shader source.
	 * @return The created shader.
	 */
	public Shader createShader(String name, String vertexShader, String geometryShader, String fragmentShader)
	{
		while(shaderPrograms.containsKey(name)) name += shaderPrograms.size();
		Shader shader = new Shader(this, name);
		shader.addShader(EShaderType.VERTEX_SHADER, vertexShader);
		if(geometryShader != null && !geometryShader.isEmpty()) 
			shader.addShader(EShaderType.GEOMETRY_SHADER, geometryShader);
		shader.addShader(EShaderType.FRAGMENT_SHADER, fragmentShader);
		shader.link();
		shaderPrograms.put(name, shader);
		return shader;
	}
	
	/**
	 * Destroys the given shader and removes it from this shader manager.
	 * @param shader
	 */
	public void removeShader(Shader shader)
	{
		shader.destroy();
		shaderPrograms.remove(shader.getName());
	}
	
	/**
	 * Destroys the shader with the given name and removes it from this shader manager.
	 * @param name
	 */
	public void removeShader(String name) {
		removeShader(getShader(name));
	}
	
	//==============================================================================
	// Package private
	//==============================================================================
	
	/**
	 * Set the active shader program.
	 * @param shader
	 */
	void setActiveShader(Shader shader) {
		activeShaderProgram = shader;
	}
}
