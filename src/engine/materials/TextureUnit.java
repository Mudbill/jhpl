package engine.materials;

import engine.core.LowLevelTexture;

/**
 * A unit of the material file, which specifies in more detail the attributes associated with each graphical input used.
 * This can take many forms but generally the important parts include the unit type and the texture, as they are combined as a filter for the final material of the mesh.
 * @author Mudbill
 *
 */
public class TextureUnit {
	
	private ETextureUnitType textureUnit;
	
	private LowLevelTexture texture;
	
	private float animFrameTime;
	private String animMode;
	private boolean compress;
	private String file;
	private boolean mipMaps;
	private String type;
	private String wrap;
	
	public TextureUnit(ETextureUnitType unitType, LowLevelTexture texture) {
		this.textureUnit = unitType;
		this.texture = texture;
	}
	
	public LowLevelTexture getTexture() {
		return texture;
	}
	
	public ETextureUnitType getTextureUnitType() {
		return textureUnit;
	}

	public float getAnimFrameTime() {
		return animFrameTime;
	}

	public void setAnimFrameTime(float animFrameTime) {
		this.animFrameTime = animFrameTime;
	}

	public String getAnimMode() {
		return animMode;
	}

	public void setAnimMode(String animMode) {
		this.animMode = animMode;
	}

	public boolean isCompressed() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public String getFile() {
		return file;
	}
	
	public String getFileName() {
		String[] s = file.split("/");
		String fileName = s[s.length-1];
		return fileName;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public boolean hasMipMaps() {
		return mipMaps;
	}

	public void setMipMaps(boolean mipMaps) {
		this.mipMaps = mipMaps;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWrap() {
		return wrap;
	}

	public void setWrap(String wrap) {
		this.wrap = wrap;
	}
	
	public String toString() {
		String output = "\tTextureUnit ("+textureUnit+"): AnimFrameTime="+animFrameTime+", AnimMode="+animMode+", Compress="+compress+", File="+file+", MipMaps="+mipMaps+", Type="+type+", Wrap="+wrap;
		return output;
	}
}