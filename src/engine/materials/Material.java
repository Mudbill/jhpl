package engine.materials;

import java.util.HashMap;
import java.util.Map;

import engine.core.LowLevelTexture;
import engine.util.Log;


/**
 * This class represents a complete material, containing all the information normally stored in HPL2's *.mat files.
 * @author Mudbill
 *
 */
public class Material {
	
	private String 	blendMode;
	private boolean depthTest;
	private float 	value;
	private String 	physicsMaterial;
	private String 	type;
	private boolean useAlpha;
	
	private Map<ETextureUnitType, TextureUnit> textureUnits;
	private Map<String, String> specificVars;
	
	/** Creates a new, default material instance */
	public Material()
	{
		this.blendMode = "Add";
		this.depthTest = true;
		this.value = 0.0f;
		this.physicsMaterial = "Default";
		this.type = "decal";
		this.useAlpha = false;
		
		this.textureUnits = new HashMap<ETextureUnitType, TextureUnit>();
		this.specificVars = new HashMap<String, String>();
	}
	
	public String getBlendMode() {
		return blendMode;
	}

	public void setBlendMode(String blendMode) {
		this.blendMode = blendMode;
	}

	public boolean getDepthTest() {
		return depthTest;
	}

	public void setDepthTest(boolean depthTest) {
		this.depthTest = depthTest;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public String getPhysicsMaterial() {
		return physicsMaterial;
	}

	public void setPhysicsMaterial(String physicsMaterial) {
		this.physicsMaterial = physicsMaterial;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getUseAlpha() {
		return useAlpha;
	}

	public void setUseAlpha(boolean useAlpha) {
		this.useAlpha = useAlpha;
	}
	
	public Map<ETextureUnitType, TextureUnit> getTextureUnits() {
		return textureUnits;
	}
	
	/** Get the texture unit of the given type */
	public TextureUnit getTextureUnit(ETextureUnitType type) {
		if(textureUnits.containsKey(type) == false) {
			Log.warn("'%s' is not an existing TextureUnit!", type.getString());
			return null;
//			TextureUnit unit = new TextureUnit(typeName);
//			addTextureUnit(unit);
		}
		return textureUnits.get(type);
	}
	
	/**
	 * Checks if this material currently contains the unit of the given type.
	 * @param unitName
	 * @return
	 */
	public boolean hasTextureUnit(ETextureUnitType unit) {
		return textureUnits.containsKey(unit);
	}
	
	public void setTextureUnit(ETextureUnitType unitName, LowLevelTexture texture) {
		setTextureUnit(new TextureUnit(unitName, texture));
	}
	
	/** Add a TextureUnit unit to this material */
	public void setTextureUnit(TextureUnit unit) {
		ETextureUnitType type = unit.getTextureUnitType();
//		if(textureUnits.containsKey(typeName)) {
//			Log.warn("ERR: Material already contains a texture unit of type '"+typeName+"'");
//			return;
//		}
		textureUnits.put(type, unit);
	}
	
	/** Add a SpecificVariable unit to this material. If it already exists, it updates the value. */
	public void setSpecificVariable(String name, String value) {
		specificVars.put(name, value);
	}
	
	/** Get a SpecificVariable with the given name */
	public String getSpecificVariable(String name) {
		if(specificVars.containsKey(name)) return specificVars.get(name);
		Log.warn("ERR: Could not find a SpecificVariable with name '"+name+"'");
		return null;
	}
	
	public Map<String, String> getSpecificVariables() {
		return specificVars;
	}
	
	/**
	 * Returns a human-readable string containing the information this material contains.
	 */
	public String toString() {
		String output = "Material: BlendMode="+blendMode+", DepthTest="+depthTest+", Value="+value+", PhysicsMaterial="+physicsMaterial+", Type="+type+", UseAlpha="+useAlpha+"\n";
		for(TextureUnit t : textureUnits.values()) output += t + "\n";
		output += specificVars + "\n";
		return output; 
	}
	
}