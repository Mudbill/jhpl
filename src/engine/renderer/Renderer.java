package engine.renderer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import engine.Engine;
import engine.StateMachine;
import engine.cameras.Camera;
import engine.lights.Light;
import engine.lights.PointLight;
import engine.main.Application;
import engine.materials.ETextureUnitType;
import engine.objects.Mesh;
import engine.objects.Model;
import engine.objects.Skybox;
import engine.shaders.Shader;
import engine.util.Log;
import engine.util.Utils;

public class Renderer {

	//==============================================================================
	// Private fields
	//==============================================================================

	private Engine engine;
	
	private Map<String, Model> renderMap;
	private Map<String, Light> lightMap;

	private Map<Mesh, Float> translucentMeshMap;
	private Skybox skybox;
	
	//==============================================================================
	// Public methods
	//==============================================================================
	
	public Renderer(Engine engine)
	{
		Log.info("Creating renderer...");
		this.engine = engine;
		renderMap = new HashMap<String, Model>();
		lightMap = new HashMap<String, Light>();
		translucentMeshMap = new HashMap<Mesh, Float>();
	}
	
	public void cleanUp()
	{
		for(String name : renderMap.keySet())
			unregisterModel(name);
		
		for(String name : lightMap.keySet())
			unregisterLight(name);
		
		translucentMeshMap.clear();
	}
	
	/**
	 * Push this camera's projection and view matrix to the active shader.
	 * @param camera
	 */
	public void updateCamera(Camera camera)
	{
		Shader shader = engine.getShaderManager().getActiveShader();
		float aspect = (float) Application.getForm().getGLComposite().getSize().x / (float) Application.getForm().getGLComposite().getSize().y;
		shader.setMat4("projection", Utils.createProjectionMatrix(camera.getFOV(), aspect, 0.1f, 100.0f));
		shader.setMat4("view", camera.getViewMatrix());
	}
	
	/**
	 * Set the active skybox.
	 * @param skybox
	 */
	public void setSkybox(Skybox skybox) 
	{
		this.skybox = skybox;
	}
	
	/**
	 * Renders all registered objects to the scene. 
	 * @param camera
	 */
	public void renderScene(Camera camera)
	{
		prepareOpenGL();
		renderLights(camera);
		renderSkybox(camera); // for optimization, render this after opaque.
		renderOpaqueModels(camera);
		renderTranslucentModels();
		renderNormals(camera);
	}
	
	/**
	 * Registers a model to be rendered when called.
	 * @param model
	 */
	public void registerModel(String id, Model model)
	{
		renderMap.put(id, model);
	}
	
	/**
	 * Removes a registered model from the render list.
	 * @param model
	 */
	public void unregisterModel(String id)
	{
		if(renderMap.containsKey(id)) renderMap.remove(id);
		else Log.error("UNREGISTER: Model '%s' not found in render list.", id);
	}
	
	/**
	 * Register this light to be rendered.
	 * @param light
	 */
	public void registerLight(String id, Light light)
	{
		lightMap.put(id, light);
	}
	
	/**
	 * Removes a registered light from the render list.
	 * @param light
	 */
	public void unregisterLight(String name)
	{
		if(lightMap.containsKey(name)) lightMap.remove(name);
		else Log.error("UNREGISTER: Light '%s' not found in render list.", name);
	}
	
	/**
	 * Renders a single model without registering it and going through the usual pipeline.
	 * @param camera
	 * @param model
	 */
	public void renderSingleModel(Camera camera, Model model)
	{
		updateCamera(camera);
		for(Mesh mesh : model.getMeshes())
		{
			drawMesh(mesh);
		}
	}
	
	//==============================================================================
	// Private methods
	//==============================================================================
	
	private void renderNormals(Camera camera)
	{
		if(StateMachine.renderNormalsDebug)
		{
			Shader active = engine.getShaderManager().getActiveShader();
			engine.getShaderManager().getShader("GEOMETRY").use();
			
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			
			updateCamera(camera);
			renderOpaqueModels(camera);
			renderTranslucentModels();
			
			if(StateMachine.enableDepthTest) GL11.glEnable(GL11.GL_DEPTH_TEST);
			
			active.use();
		}
	}
	
//	private static void renderWireframes(Camera camera)
//	{
//		Shader current = ShaderManager.getActiveShader();
//
//		if(StateMachine.drawModelWireframe)
//		{
//			Shader white = ShaderManager.getShader("WHITE");
//			white.use();
//			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
//		} else {
//			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
//		}
//		
//		if(StateMachine.drawModelFaces)
//		{
//			GL11.glDisable(GL11.GL_DEPTH_TEST);
//			Shader white = ShaderManager.getShader("LAMP");
//			white.use();
//			
////			boolean diffuse = Application.useDiffuseMap;
//			boolean wireframe = Application.getDrawWireframe();
////			Application.useDiffuseMap = false;
//			Application.setDrawWireFrame(true);
//						
////			renderSkybox(camera);
//			renderOpaqueModels(camera);
//			renderTranslucentModels();
//			
//			GL11.glEnable(GL11.GL_DEPTH_TEST);
//			Application.setDrawWireFrame(wireframe);
////			Application.useDiffuseMap = diffuse;
//		}
//		current.use();
//	}
	
	/**
	 * Perform the steps necessary for drawing the skybox.
	 * @param camera
	 */
	private void renderSkybox(Camera camera)
	{
		if(skybox == null || !StateMachine.getBoolean("preview.skybox")) return;
		
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glFrontFace(GL11.GL_CW);
		
		Shader activeShader = engine.getShaderManager().getActiveShader();
		Shader skyboxShader = engine.getShaderManager().getShader("SKYBOX");
		
		/* Enable the shader for rendering the skybox */
		skyboxShader.use();
		updateCamera(camera);
		skyboxShader.setMat4("view", Utils.toMat4(Utils.toMat3(camera.getViewMatrix())));
		Mesh mesh = skybox.getShape();
		
		GL30.glBindVertexArray(mesh.getMesh().getVaoID());
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, mesh.getMaterial().getTextureUnit(ETextureUnitType.DIFFUSE).getTexture().getID());
		GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getMesh().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		GL30.glBindVertexArray(0);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glFrontFace(GL11.GL_CCW);
		
		/* Re-enable the normal shader */
		activeShader.use();
	}

//	/**
//	 * Not quite working yet.
//	 * @param model
//	 */
//	public void drawModelWithOutline(GfxObject model)
//	{
////		GL11.glEnable(GL11.GL_DEPTH_TEST);
////		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
//		
////		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
//		
////		GL11.glStencilMask(0x00);
//		
//		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
//		GL11.glStencilMask(0xFF);
//		Shader currentShader = Shader.getCurrentShader();
//		drawModel(model);
//		
//		Shader outlineShader = Shader.getOutlineShader();
//		GfxObject outline = model;
//		outline.setScale(new Vector3f(1.1f, 1.1f, 1.1f));
//		outlineShader.use();
//		outlineShader.setMat4("model", outline.getTransformationMatrix());
//		
//		GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF);
//		GL11.glStencilMask(0x00);
//		GL11.glDisable(GL11.GL_DEPTH_TEST);
//		
//		drawModel(outline);
//		
//		GL11.glStencilMask(0xFF);
//		GL11.glEnable(GL11.GL_DEPTH_TEST);
//		currentShader.use();
//	}
	
	private void prepareOpenGL()
	{
		if(StateMachine.enableCulling)		GL11.glEnable(GL11.GL_CULL_FACE);
		else 								GL11.glDisable(GL11.GL_CULL_FACE);
		
		if(StateMachine.enableDepthTest)	GL11.glEnable(GL11.GL_DEPTH_TEST);
		else 								GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private void renderLights(Camera camera)
	{
		Shader shader = engine.getShaderManager().getActiveShader();
		for(Light light : lightMap.values())
		{
			shader.setVec3("viewPos",  camera.getPosition());
			shader.setVec3("pLight.position",  light.getPosition());
			shader.setVec3("pLight.diffuse",  light.getColor());
			shader.setVec3("pLight.specular", new Vector3f(1f, 1f, 1f));
			
			if(light instanceof PointLight)
			{
				shader.setFloat("pLight.constant", 1.0f);
				shader.setFloat("pLight.linear", 0.09f);
				shader.setFloat("pLight.quadratic", 0.032f);
				
//				if(StateMachine.drawEntityIcons)
//					guiRenderer.renderBillboard(PointLight.icon, light.getPosition());
			}
		}
	}
		
	/**
	 * Iterates through all currently registered models and draws them to the viewport. Transparent models are sorted based on the position of the given camera.
	 * @param camera
	 */
	private void renderOpaqueModels(Camera camera)
	{
		Vector3f camPos = camera.getPosition();
		translucentMeshMap.clear();
		
		for(Model model : renderMap.values())
		{
			for(Mesh mesh : model.getMeshes())
			{
				if(mesh.getMaterial().getUseAlpha() == false)
				{
					drawMesh(mesh);
					continue;
				}
				else
				{
					float distance = -Utils.length2(Utils.subtract(camPos, model.getPosition()));
					translucentMeshMap.put(mesh, distance);
				}
			}
		}
	}
	
	/**
	 * Renders the remaining models that have translucency enabled. First creates a sorted map so that they are rendered in order from furthest to nearest, 
	 * for the transparency effect to work.
	 */
	private void renderTranslucentModels()
	{
		Map<Mesh, Float> sortedMap = translucentMeshMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		
		for(Mesh mesh : sortedMap.keySet())
		{
			drawMesh(mesh);
		}
	}
	
	private void drawMesh(Mesh mesh)
	{
		if(StateMachine.drawModelFaces)
		{
			prepareShader(mesh);
			prepareMesh(mesh);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getMesh().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			GL30.glBindVertexArray(0);
		}
		
		if(StateMachine.drawModelWireframe)
		{
			Shader active = engine.getShaderManager().getActiveShader();
			engine.getShaderManager().getShader("WHITE").use();
			prepareShader(mesh);
			prepareMesh(mesh);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getMesh().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			GL30.glBindVertexArray(0);
			active.use();
		}
	}
	
	private void prepareShader(Mesh mesh) 
	{
		Shader shader = engine.getShaderManager().getActiveShader();
		shader.setInt("renderMode", StateMachine.renderMode);
		shader.setMat4("model", mesh.getParent().getTransformationMatrix());
		shader.setBoolean("useNMap", mesh.getMaterial().hasTextureUnit(ETextureUnitType.NORMAL) && StateMachine.getBoolean("Enable_NMap"));
	}
	
	private void prepareMesh(Mesh mesh)
	{
		GL30.glBindVertexArray(mesh.getMesh().getVaoID());
		
		int diffID = StateMachine.getBoolean("Enable_Diffuse") 
				? mesh.getMaterial().getTextureUnit(ETextureUnitType.DIFFUSE).getTexture().getID() 
				: engine.getTextureLoader().getWhiteTexture().getID();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, diffID);
		
		int specID = mesh.getMaterial().hasTextureUnit(ETextureUnitType.SPECULAR) && StateMachine.getBoolean("Enable_Specular") 
				? mesh.getMaterial().getTextureUnit(ETextureUnitType.SPECULAR).getTexture().getID() 
				: engine.getTextureLoader().getBlackTexture().getID();
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, specID);
		
		int normID = mesh.getMaterial().hasTextureUnit(ETextureUnitType.NORMAL) && StateMachine.getBoolean("Enable_NMap")
				? mesh.getMaterial().getTextureUnit(ETextureUnitType.NORMAL).getTexture().getID()
				: engine.getTextureLoader().getBlackTexture().getID();
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normID);
	}
}
