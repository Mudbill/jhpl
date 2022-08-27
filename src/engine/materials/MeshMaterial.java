package engine.materials;

import engine.core.LowLevelTexture;

public class MeshMaterial {

	private Material material;
	private LowLevelTexture diffuse;
	private LowLevelTexture specular;
	private LowLevelTexture normal;
	private boolean useAlpha;

	public MeshMaterial(LowLevelTexture texture) {
		this.material = new Material();
		this.diffuse = texture;
	}
	
	public void setDiffuseMap(LowLevelTexture texture) {
		this.diffuse = texture;
		String filePath = "";
		if(texture != null) filePath = texture.getFilePath();
		this.material.getTextureUnit(ETextureUnitType.DIFFUSE).setFile(filePath);
	}
	
	public LowLevelTexture getDiffuseMap() {
		return diffuse;
	}

	public void setSpecularMap(LowLevelTexture texture) {
		this.specular = texture;
		String filePath = "";
		if(texture != null) filePath = texture.getFilePath();
		this.material.getTextureUnit(ETextureUnitType.SPECULAR).setFile(filePath);
	}
	
	public LowLevelTexture getSpecularMap() {
		return specular;
	}

	public void setNormalMap(LowLevelTexture texture) {
		this.normal = texture;
		String filePath = "";
		if(texture != null) filePath = texture.getFilePath();
		this.material.getTextureUnit(ETextureUnitType.NORMAL).setFile(filePath);
	}
	
	public LowLevelTexture getNormalMap() {
		return normal;
	}
	
	public boolean getUseAlpha() {
		return useAlpha;
	}
	
	public void setUseAlpha(boolean use) {
		useAlpha = use;
		this.material.setUseAlpha(use);
	}
	
	public boolean hasSpecularMap() {
		return specular != null;
	}
	
	public boolean hasNormalMap() {
		return normal != null;
	}
	
	public Material getMaterial() {
		return this.material;
	}
}
