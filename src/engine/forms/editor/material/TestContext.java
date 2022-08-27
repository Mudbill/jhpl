package engine.forms.editor.material;

import java.io.File;

import net.buttology.lwjgl.swt.GLComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import engine.Engine;
import engine.MemoryManager;
import engine.core.FramebufferManager;
import engine.core.LowLevelMesh;
import engine.core.LowLevelTexture;
import engine.loader.Loader;
import engine.loader.TexLoader;
import engine.materials.MaterialHandler;
import engine.renderer.Renderer;
import engine.shaders.Shader;
import engine.shaders.ShaderManager;
import engine.util.Log;

public class TestContext {

	protected Shell shell;
	private Engine engine;
	private GLComposite gl1, gl2;
	private GLComposite[] list;
	private LowLevelMesh quad;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TestContext window = new TestContext();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		engine = new Engine() {
			private Loader loader;
			private Renderer renderer;
			private TexLoader tex;
			private MemoryManager mem;
			private MaterialHandler mat;
			private ShaderManager shad;
			private FramebufferManager fram;
			private boolean init;
			private Shader shader;
			private LowLevelTexture texture;
			
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
				return tex;
			}
			@Override
			public MemoryManager getMemoryManager() {
				return mem;
			}
			@Override
			public MaterialHandler getMaterialHandler() {
				return mat;
			}
			@Override
			public ShaderManager getShaderManager() {
				return shad;
			}
			@Override
			public FramebufferManager getFramebufferManager() {
				return fram;
			}
			@Override
			protected void init() {
				if(init) return;
				init = true;
				
				Log.info("Init");
				
				loader = new Loader(this);
				renderer = new Renderer(this);
				tex = new TexLoader(this);
				mem = new MemoryManager();
				mat = new MaterialHandler(this);
				shad = new ShaderManager();
				fram = new FramebufferManager();
				texture = tex.loadTexture(new File("resources/core/error.png"));
				shader = shad.createShader("PLAIN_TEXTURE", "shaders/plain_texture_vertex.glsl", "shaders/plain_texture_frag.glsl");
				shader.use();
			}
			@Override
			protected void update() {
//				for(GLComposite g : list) {
//					g.setCurrent();
				gl1.setCurrent();
				GL11.glClearColor(1, 0, 0, 1);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				GL30.glBindVertexArray(quad.getVaoID());
				GL20.glEnableVertexAttribArray(0);
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				if(texture != null) GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
				GL20.glDisableVertexAttribArray(0);
				GL30.glBindVertexArray(0);
				
				gl2.setCurrent();
				GL11.glClearColor(0, 1, 0, 1);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				GL30.glBindVertexArray(quad.getVaoID());
				GL20.glEnableVertexAttribArray(0);
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
//				if(texture != null) GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
//				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
//				GL20.glDisableVertexAttribArray(0);
//				GL30.glBindVertexArray(0);
//				}
			}
			@Override
			protected void shutdown() {
				loader.cleanUp();
				renderer.cleanUp();
				tex.cleanUp();
				mem.cleanUp();
				shad.cleanUp();
				fram.cleanUp();
			}
		};
		
		gl1 = new GLComposite(shell, SWT.BORDER, engine);
		gl2 = new GLComposite(shell, SWT.BORDER, engine);
//		gl1.shareWith(gl2);
		gl1.init();
		quad = engine.getLoader().loadToVAO(new float[]{-1, 1, -1, -1, 1, 1});
//		gl2.shareWith(gl1);
		gl2.init();
//		quad = engine.getLoader().loadToVAO(new float[]{-1, 1, -1, -1, 1, 1});
//		list = new GLComposite[] {gl1, gl2};
	}
}
