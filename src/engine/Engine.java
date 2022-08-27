package engine;

import engine.core.FramebufferManager;
import engine.loader.Loader;
import engine.loader.TexLoader;
import engine.materials.MaterialHandler;
import engine.renderer.Renderer;
import engine.shaders.ShaderManager;
import net.buttology.lwjgl.swt.GLDrawLoop;

public abstract class Engine extends GLDrawLoop {

	public abstract Loader getLoader();
	public abstract Renderer getRenderer();
	public abstract TexLoader getTextureLoader();
	public abstract MemoryManager getMemoryManager();
	public abstract MaterialHandler getMaterialHandler();
	public abstract ShaderManager getShaderManager();
	public abstract FramebufferManager getFramebufferManager();

	
}
