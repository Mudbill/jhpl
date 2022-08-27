package engine.renderer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.Engine;
import engine.StateMachine;
import engine.core.LowLevelMesh;
import engine.core.LowLevelTexture;
import engine.gui.GuiTexture;
import engine.loader.Loader;
import engine.shaders.Shader;
import engine.shaders.ShaderManager;
import engine.util.Utils;

public class GuiRenderer {

	private ShaderManager shaderManager;
	private LowLevelMesh quad;
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();
	private Shader shader;
	private Loader loader;
	
	public GuiRenderer(Engine instance)
	{
		this.loader = instance.getLoader();
		this.shaderManager = instance.getShaderManager();
		float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
		quad = loader.loadToVAO(positions);
		shader = shaderManager.createShader("GUI", "shaders/gui_vertex.glsl", "shaders/gui_frag.glsl");
		shaderManager.createShader("BILLBOARD", "shaders/billboard_vertex.glsl", "shaders/billboard_frag.glsl");
	}
	
	public void render()
	{
		Shader current = shaderManager.getActiveShader();
		shader.use();
		GL30.glBindVertexArray(quad.getVaoID());
//		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for(GuiTexture gui : guis)
		{
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture().getID());
			Matrix4f transformation = Utils.createTransformationMatrix(gui.getPosition(), gui.getScale());
			shader.setMat4("transform", transformation);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		if(StateMachine.enableDepthTest)
			GL11.glEnable(GL11.GL_DEPTH_TEST);
//		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		current.use();
	}
	
	public void renderBillboard(LowLevelTexture texture, Vector3f position)
	{
		Shader current = shaderManager.getActiveShader();
		Shader shader = shaderManager.getShader("BILLBOARD");
		shader.use();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
		Matrix4f transformation = Utils.createTransformationMatrix(position);
		shader.setMat4("transform", transformation);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		if(StateMachine.enableDepthTest)
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		current.use();
	}
	
	public void registerGui(GuiTexture gui)
	{
		guis.add(gui);
	}
	
}
