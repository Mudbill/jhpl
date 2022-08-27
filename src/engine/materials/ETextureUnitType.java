package engine.materials;

import engine.util.Log;

public enum ETextureUnitType
{
	DIFFUSE("Diffuse"), 
	SPECULAR("Specular"), 
	NORMAL("NMap"), 
	ALPHA("Alpha"), 
	HEIGHT("Height"), 
	ILLUMINATION("Illumination"), 
	DISSOLVE_ALPHA("DissolveAlpha"), 
	CUBEMAP("CubeMap"), 
	CUBEMAP_ALPHA("CubeMapAlpha");

	private String rep;
	
	private ETextureUnitType(String rep)
	{
		this.rep = rep;
	}

	/**
	 * Gets this enum value's String representation.
	 * @return
	 */
	public String getString()
	{ 
		return rep;
	}
	
	/**
	 * Gets the matching enum value based on the given String representation.
	 * @param string
	 * @return
	 */
	public static ETextureUnitType getTypeFromString(String string)
	{
		for(ETextureUnitType e : ETextureUnitType.values())
			if(e.getString().equals(string))
				return e;
		
		Log.error("ETextureUnitType: Failed to get unit type '%s'", string);
		return null;
	}
}
