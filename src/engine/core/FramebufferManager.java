package engine.core;

import java.util.ArrayList;
import java.util.List;

import engine.util.Log;

public class FramebufferManager {
	
	//==============================================================================
	// Private fields
	//==============================================================================
	
	private List<Framebuffer> framebuffers = new ArrayList<Framebuffer>();
	
	//==============================================================================
	// Public methods
	//==============================================================================
	
	public FramebufferManager() {
		Log.info("Creating framebuffer manager...");
	}
	
	/**
	 * Destroys all created framebuffers.
	 */
	public void cleanUp() {
		for(Framebuffer fb : framebuffers) fb.destroy();
		framebuffers.clear();
	}
	
	public Framebuffer createFramebuffer(int height, int width, int samples) {
		Framebuffer fb = new Framebuffer(height, width, samples);
		framebuffers.add(fb);
		return fb;
	}

}
