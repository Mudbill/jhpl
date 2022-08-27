package engine.core;

import org.lwjgl.opengl.GL30;

public class Renderbuffer {

	private int id;
	
	public Renderbuffer(int width, int height, int samples) {
		this.setup(width, height, samples);
	}
	
	private void setup(int width, int height, int samples) {
		int id = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, id);
		GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, samples, GL30.GL_DEPTH24_STENCIL8, width, height);
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
		this.id = id;
	}
	
	public int getID() {
		return id;
	}
	
	public void destroy() {
		GL30.glDeleteRenderbuffers(id);
	}
	
}
