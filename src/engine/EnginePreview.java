package engine;

import java.io.File;

import net.buttology.lwjgl.swt.InputHandler;

import org.eclipse.swt.SWT;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import engine.cameras.TPCamera;
import engine.core.Cube;
import engine.core.EPrimitiveType;
import engine.core.Framebuffer;
import engine.core.FramebufferManager;
import engine.forms.editor.material.EWrapMode;
import engine.lights.PointLight;
import engine.lights.SpotLight;
import engine.loader.Loader;
import engine.loader.TexLoader;
import engine.main.Application;
import engine.materials.ETextureUnitType;
import engine.materials.MaterialHandler;
import engine.objects.Model;
import engine.objects.Skybox;
import engine.renderer.Renderer;
import engine.shaders.Shader;
import engine.shaders.ShaderManager;
import engine.util.Log;

public class EnginePreview extends Engine {

	private Loader loader;
	private Renderer renderer;
	private TexLoader texLoader;
	private MemoryManager memoryManager;
	private MaterialHandler materialHandler;
	private ShaderManager shaderManager;
	private FramebufferManager framebufferManager;
	private InputHandler input = super.getInputHandler();
	
	@Override
	public Loader getLoader() {return loader;}

	@Override
	public Renderer getRenderer() {return renderer;}

	@Override
	public TexLoader getTextureLoader() {return texLoader;}

	@Override
	public MemoryManager getMemoryManager() {return memoryManager;}

	@Override
	public MaterialHandler getMaterialHandler() {return materialHandler;}

	@Override
	public ShaderManager getShaderManager() {return shaderManager;}

	@Override
	public FramebufferManager getFramebufferManager() {return framebufferManager;}

	@Override
	public void init() {
		Log.info("Initializing preview context...");
		GL11.glClearColor(0.2f, 0.2f, 0.2f, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL13.GL_MULTISAMPLE);
		loader = new Loader(this);
		renderer = new Renderer(this);
		texLoader = new TexLoader(this);
		memoryManager = new MemoryManager();
		materialHandler = new MaterialHandler(this);
		shaderManager = new ShaderManager();
		framebufferManager = new FramebufferManager();
		
		initShaders();
		
		texLoader.createCoreTextures();
		lightShader.use();
		
		lightModel = loader.loadPrimitive(new Cube(texLoader.getWhiteTexture()));
		lightModel.setScale(0.2f, 0.2f, 0.2f);
		lightModel.setPosition(1, 2, 1.5f);
		pointLight = new PointLight(lightModel.getPosition(), new Vector3f(1, 1, 1));

		camera = new TPCamera(input);
		camera.setAngle(180);
		camera.setPitch(30);
		
		object = loader.loadPrimitive(EPrimitiveType.CUBE, texLoader.getWhiteTexture());
//		object.setTextureMap(ETextureUnitType.SPECULAR, "resources/brickwall_spec.jpg");
//		object.setTextureMap(ETextureUnitType.NORMAL, "resources/brickwall_normal.jpg");
		
		renderer.registerLight("light", pointLight);
		renderer.registerModel("object", object);
		renderer.setSkybox(new Skybox(texLoader.loadTexture("resources/editor_cubemap_default.dds")));

		Log.info("INIT COMPLETE (total time: %d ms)", (System.currentTimeMillis() - Application.START_TIME));
		Log.info("--------------------------------------------------------------------------------");
	}

	@Override
	public void update() {
		Framebuffer.clear();
		
		/* Input */
		this.updateMouse(input.getMousePosX(), input.getMousePosY());
		this.updateKeyboard();
				
		//--------------------------
		
		if(StateMachine.getBoolean("preview.rotate"))
			object.increaseRotation(0.0f, 0.1f, 0.0f);
		
		whiteShader.use();
		renderer.renderSingleModel(camera, lightModel);
		
		lightShader.use();
		lightShader.setInt("material.aDiffuseMap", 0);
		lightShader.setInt("material.aSpecularMap", 1);
		lightShader.setInt("material.aNormalMap", 2);
		lightShader.setFloat("material.shininess", 32.0f);
		this.draw();
		
		Vector3f camPos = camera.getPosition();
		Application.getForm().setPitch(camera.getPitch());
		Application.getForm().setYaw(camera.getYaw());
		Application.getForm().setPos(camPos.x, camPos.y, camPos.z);
	}

	@Override
	public void shutdown() {
		loader.cleanUp();
		texLoader.cleanUp();
		renderer.cleanUp();
		memoryManager.cleanUp();
		shaderManager.cleanUp();
		framebufferManager.cleanUp();
	}
	
	//==============================================================================
	// Custom functions
	//==============================================================================

	Shader shader, lightShader, whiteShader, outlineShader;
	
	Model model2, lightModel, window2;
	Model object;
	
	TPCamera camera;
	SpotLight spotLight;
	PointLight pointLight;
	
	float lastFrame = 0.0f;
	
	float lastX = 400;
	float lastY = 300;
	
	float yaw = 0.0f;
	float pitch = 0.0f;
	
	/** Temporary function for loading different dae models. */
	public void updateModel(String path) 
	{
		Model newObject = loader.loadModel(path);
		if(newObject != null)
		{
			renderer.unregisterModel("object");
			object = newObject;
			renderer.registerModel("object", object);		
		}
	}
	
	public void setPrimitiveModel(EPrimitiveType type)
	{
		Log.info("Setting primitive " + type);
		Model model = loader.loadPrimitive(type, texLoader.getWhiteTexture());
		renderer.unregisterModel("object");
		object = model;
		renderer.registerModel("object", object);
	}
	
	public Model getModel() {
		return object;
	}
	
	public void setTexture(ETextureUnitType type, File file) {
		object.getFirstMesh().getMaterial().setTextureUnit(type, texLoader.loadTexture(file));
	}
	
	public void setWrapMode(EWrapMode mode) {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, mode.getWrapMode());
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, mode.getWrapMode());
	}
	
	private void initShaders()
	{
		shaderManager.createShader("SKYBOX", "shaders/skybox_vertex.glsl", "shaders/skybox_frag.glsl");
		shaderManager.createShader("NMAP", "shaders/core/debug_nmap_vertex.glsl", "shaders/core/debug_nmap_frag.glsl");
		whiteShader = shaderManager.createShader("WHITE", "shaders/3d_light_vertex.glsl", "shaders/white_frag.glsl");
		lightShader = shaderManager.createShader("LIGHT", "shaders/3d_light_vertex.glsl", "shaders/test_light_frag.glsl");
		
		shaderManager.createShader("GEOMETRY", "shaders/test_geom_v.glsl", "shaders/test_geom_g.glsl", "shaders/test_geom_f.glsl");
	}
		
	private void updateMouse(int xpos, int ypos)
	{
		float xoffset = xpos - lastX;
		float yoffset = lastY - ypos;
		lastX = xpos;
		lastY = ypos;
		camera.tick(xoffset, yoffset);
	}
	
	private void updateKeyboard()
	{
		if(input.getKeyPressed('e'))
			object.increaseRotation(0, 0.4f, 0);
		if(input.getKeyPressed('q'))
			object.increaseRotation(0, -0.4f, 0);
		if(input.getKeyPressed('1'))
			object.increaseRotation(0.4f, 0, 0);
		if(input.getKeyPressed('2'))
			object.increaseRotation(-0.4f, 0, 0);
		if(input.getKeyPressed('3'))
			object.increaseRotation(0, 0, 0.4f);
		if(input.getKeyPressed('4'))
			object.increaseRotation(0, 0, -0.4f);
		
		if(input.getKeyPressed('w'))
			object.increasePosition(0, 0.02f, 0);
		if(input.getKeyPressed('s'))
			object.increasePosition(0, -0.02f, 0);
		if(input.getKeyPressed('a'))
			object.increasePosition(0.02f, 0, 0);
		if(input.getKeyPressed('d'))
			object.increasePosition(-0.02f, 0, 0);

		if(input.getKeyPressed(SWT.ARROW_UP))
			camera.increasePosition(0, 0.02f, 0);
		if(input.getKeyPressed(SWT.ARROW_DOWN))
			camera.increasePosition(0, -0.02f, 0);
		if(input.getKeyPressed(SWT.ARROW_LEFT))
			camera.increasePosition(0.02f, 0, 0);
		if(input.getKeyPressed(SWT.ARROW_RIGHT))
			camera.increasePosition(-0.02f, 0, 0);
	}
		
	private void draw()
	{
		renderer.updateCamera(camera);
		renderer.renderScene(camera);
	}
}
