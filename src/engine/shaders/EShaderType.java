package engine.shaders;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

public enum EShaderType {

	VERTEX_SHADER(GL20.GL_VERTEX_SHADER),
	GEOMETRY_SHADER(GL32.GL_GEOMETRY_SHADER),
	FRAGMENT_SHADER(GL20.GL_FRAGMENT_SHADER);
	
	private int shaderValue;
	
	private EShaderType(int type)
	{
		this.shaderValue = type;
	}
	
	public int getType() { return this.shaderValue; }
}
