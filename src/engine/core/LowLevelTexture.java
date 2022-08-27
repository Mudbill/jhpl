package engine.core;

import engine.util.Log;

public class LowLevelTexture {

	private int textureID;
	private String filePath;
	
	public LowLevelTexture(int textureID, String filePath) {
		this.textureID = textureID;
		this.filePath = filePath;
		Log.info("Created LowLevelTexture with ID %d", textureID);
	}

	public int getID() {
		return textureID;
	}

	public String getFilePath() {
		return filePath;
	}

	@Override
	public String toString() {
		return "LowLevelTexture [textureID=" + textureID + ", filePath="
				+ filePath + "]";
	}
	
}
