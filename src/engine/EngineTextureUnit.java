package engine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.buttology.lwjgl.swt.GLComposite;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import engine.core.FramebufferManager;
import engine.core.LowLevelMesh;
import engine.core.LowLevelTexture;
import engine.forms.editor.material.EWrapMode;
import engine.loader.Loader;
import engine.loader.TexLoader;
import engine.materials.MaterialHandler;
import engine.renderer.Renderer;
import engine.shaders.Shader;
import engine.shaders.ShaderManager;
import engine.util.Log;

public class EngineTextureUnit extends Engine {
	
	private static Map<GLComposite, TextureUnitData> contexts = new HashMap<GLComposite, TextureUnitData>();
	
	public static void registerContext(GLComposite c) {
		contexts.put(c, new TextureUnitData());
	}
	
	//===========================
	
	private Loader loader;
	private Renderer renderer;
	private TexLoader texLoader;
	private MemoryManager memoryManager;
	private ShaderManager shaderManager;
	
	private Shader shader;
//	private LowLevelMesh quad;
//	private LowLevelTexture texture;
	
	@Override
	public Loader getLoader() {
		return loader;
	}

	@Override
	public Renderer getRenderer() {
		return renderer;
	}

	@Override
	public TexLoader getTextureLoader() {
		return texLoader;
	}

	@Override
	public MemoryManager getMemoryManager() {
		return memoryManager;
	}

	@Override
	public MaterialHandler getMaterialHandler() {
		return null;
	}

	@Override
	public ShaderManager getShaderManager() {
		return shaderManager;
	}

	@Override
	public FramebufferManager getFramebufferManager() {
		return null;
	}
	
	@Override
	public void init()
	{
		Log.info("Initializing texture unit context...");
		loader = new Loader(this);
		shaderManager = new ShaderManager();
		renderer = new Renderer(this);
		memoryManager = new MemoryManager();
		texLoader = new TexLoader(this);
		
		shader = shaderManager.createShader("PLAIN_TEXTURE", "shaders/plain_texture_vertex.glsl", "shaders/plain_texture_frag.glsl");
		shader.use();
	}

	@Override
	public void update()
	{
		for(GLComposite c : contexts.keySet())
		{
			c.setCurrent();
			GL11.glClearColor(1.0f, 0.3f, 1.0f, 1.0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			render(c);
		}
	}

	@Override
	public void shutdown()
	{
		loader.cleanUp();
		renderer.cleanUp();
		texLoader.cleanUp();
		memoryManager.cleanUp();
		shaderManager.cleanUp();
	}
	
	public void init(GLComposite c)
	{
		c.setCurrent();
		LowLevelMesh quad = loader.loadToVAO(new float[] {-1, 1, -1, -1, 1, 1/*, 1, -1*/});
		contexts.get(c).setMesh(quad);
	}

	public void setTexture(GLComposite c, File file)
	{
		c.setCurrent();
		LowLevelTexture texture = texLoader.loadTexture(file);
		contexts.get(c).setTexture(texture);
		Log.info("Setting active texture with ID %d", texture.getID());
	}
	
	public void setWrapMode(EWrapMode mode)
	{
//		currentWrapMode = mode;
		Log.info("Setting WrapMode to " + mode);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, mode.getWrapMode());
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, mode.getWrapMode());
	}
	
	public void setUseMipMaps(boolean use)
	{
//		useMipMaps = use;
		Log.info("Setting UseMipMaps to " + use);
		int mode = use ? GL11.GL_NEAREST_MIPMAP_LINEAR : GL11.GL_LINEAR;
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mode);
	}
	
	//==============================================================================
	// Private methods
	//==============================================================================
	
	private void render(GLComposite c)
	{
		TextureUnitData tua = contexts.get(c);
		LowLevelMesh quad = tua.getMesh();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		LowLevelTexture texture = tua.getTexture();
		if(texture != null)
		{
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
		}
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

}
