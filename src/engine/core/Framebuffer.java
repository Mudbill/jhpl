package engine.core;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class Framebuffer {
	
	//==============================================================================
	// Private fields
	//==============================================================================

	private int id;
	private List<Integer> textures = new ArrayList<Integer>();
	private List<Renderbuffer> renderbuffers = new ArrayList<Renderbuffer>();
	private final int WIDTH, HEIGHT, SAMPLES;
	
	//==============================================================================
	// Public methods
	//==============================================================================
	
	protected Framebuffer(int width, int height, int samples) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.SAMPLES = samples;
		this.setupFramebuffer();
		if(getFramebufferComplete() == false) {
			System.err.println("Framebuffer incomplete!");
		}
		this.unbind();
	}
	
	public static void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
	}
	
	public void blitFramebuffers() {
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, id);
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		GL30.glBlitFramebuffer(0, 0, WIDTH, HEIGHT, 0, 0, WIDTH, HEIGHT, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
	}
	
	public void setClearColor(float r, float g, float b, float a) {
		this.bind();
		GL11.glClearColor(r, g, b, a);
	}
	
	public void attachRenderbufferToDepthAndStencil(Renderbuffer rbo) {
		renderbuffers.add(rbo);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, rbo.getID());
	}
	
	public void bind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
	}
	
	public void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public int getID() {
		return this.id;
	}
	
	public void destroy() {
		for(int texture : textures) GL11.glDeleteTextures(texture);
		for(Renderbuffer rbo : renderbuffers) rbo.destroy();
		GL30.glDeleteFramebuffers(id);
	}
	
	//==============================================================================
	// Private methods
	//==============================================================================

	private void setupFramebuffer() {
		int fboID = GL30.glGenFramebuffers();
		this.bind();
		this.id = fboID;
		this.attachMultisampleTextureToColorBuffer(SAMPLES);
		this.attachRenderbufferToDepthAndStencil(new Renderbuffer(WIDTH, HEIGHT, SAMPLES));
		GL11.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
	}
	
	private void attachMultisampleTextureToColorBuffer(int samples) {
		int id = GL11.glGenTextures();
		GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, id);
		GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, samples, GL11.GL_RGB, WIDTH, HEIGHT, true);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, 0);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL32.GL_TEXTURE_2D_MULTISAMPLE, id, 0);
		textures.add(id);
	}
	
	private boolean getFramebufferComplete() {
		return GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE;
	}	

//	private void attachTextureToColorBuffer() {
//		int id = GL11.glGenTextures();
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
//		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, WIDTH, HEIGHT, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
//		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, id, 0);
//		textures.add(id);
//	}
		
//	private void createFrameBuffer() {
//		id = GL30.glGenFramebuffers();
//		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
//		int tID = GL11.glGenTextures();
//		GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, tID);
//		GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, 4, GL11.GL_RGB, 800, 600, true);
//		GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, 0);
//		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL32.GL_TEXTURE_2D_MULTISAMPLE, tID, 0);
//		
//		GL30.glRenderbufferStorageMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, 4, GL30.GL_DEPTH24_STENCIL8, 800, 600);
//		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, id);
//		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
//		GL30.glBlitFramebuffer(0, 0, 800, 600, 0, 0, 800, 600, GL11.GL_COLOR_BUFFER_BIT, GL11.GL_NEAREST);
//
//		if(getFramebufferComplete()) {
//			System.out.println("Framebuffer complete!");
//		}
//	}
}
