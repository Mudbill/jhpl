package engine.materials;

import java.io.File;

import engine.Engine;
import engine.core.LowLevelTexture;
import engine.system.DirectoryManager;
import engine.util.Log;
import engine.util.Utils;
import engine.util.XMLParser;
import engine.util.XMLParser.Element;

public class MaterialHandler {

	//==============================================================================
	// Private fields
	//==============================================================================
	
	private Engine engine;

	//==============================================================================
	// Public methods
	//==============================================================================
	
	public MaterialHandler(Engine engine) {
		Log.info("Creating material handler...");
		this.engine = engine;
	}
	
	/**
	 * Simple function for creating a new texture unit with default settings. Only sets the type and file.
	 * @param type
	 * @param file
	 * @return
	 */
	public TextureUnit createTextureUnit(ETextureUnitType type, LowLevelTexture llt) 
	{
		TextureUnit t = new TextureUnit(type, llt);
		t.setFile("");
		return t;
	}
	
	/**
	 * Simple function for creating a new texture unit with default settings. Only sets the type and file.
	 * @param type
	 * @param file
	 * @return
	 */
	public TextureUnit createTextureUnit(ETextureUnitType type, String file) 
	{
		LowLevelTexture llt = engine.getTextureLoader().loadTexture(file);
		TextureUnit t = new TextureUnit(type, llt);
		t.setFile(file);
		return t;
	}
	
	/**
	 * Loads a material config file, adds it to the map of loaded materials, then returns it. If the material is already loaded, it is not loaded again.
	 * @param path - the material config file to load.
	 * @return the material
	 */
	public Material loadMaterialFile(String path)
	{
		return loadMaterialFile(new File(path));
	}
	
	/**
	 * Loads a material config file, adds it to the map of loaded materials, then returns it. If the material is already loaded, it is not loaded again.
	 * @param file - the material config file to load.
	 * @return the material
	 */
	public Material loadMaterialFile(File file) 
	{
		if(file == null)
		{
			Log.error("MaterialHandler: Null file given! Returning plain, white material.");
			Material m = new Material();
			m.setTextureUnit(createTextureUnit(ETextureUnitType.DIFFUSE, engine.getTextureLoader().getWhiteTexture()));
			return m;
		}
		
		if(engine.getMemoryManager().isFileLoaded(file))
		{
			Log.info("Material is already loaded: "+file.getAbsolutePath());
			return (Material) engine.getMemoryManager().getLoadedFile(file);
		}
		
		Material material = new Material();

		try {
			
			XMLParser fileParser = new XMLParser();
			fileParser.read(file);
			
			Element eMaterial = fileParser.getElement("Material");
			
			Element eMain = eMaterial.getElement("Main");
			Element eTextureUnits = eMaterial.getElement("TextureUnits");
			Element eSpecificVariables = eMaterial.getElement("SpecificVariables");
			
			if(eMain == null)
			{
				Log.error("ERR: Material file not valid!");
				return null;
			}
			
			if(eMain.hasAttribute("BlendMode")) 		material.setBlendMode(eMain.getAttribute("BlendMode"));
			if(eMain.hasAttribute("DepthTest")) 		material.setDepthTest(Boolean.parseBoolean(eMain.getAttribute("DepthTest")));
			if(eMain.hasAttribute("PhysicsMaterial")) 	material.setPhysicsMaterial(eMain.getAttribute("PhysicsMaterial"));
			if(eMain.hasAttribute("Type")) 				material.setType(eMain.getAttribute("Type"));
			if(eMain.hasAttribute("UseAlpha")) 			material.setUseAlpha(Boolean.parseBoolean(eMain.getAttribute("UseAlpha")));
			if(eMain.hasAttribute("Value"))				material.setValue(Float.parseFloat(eMain.getAttribute("Value")));
			
			if(eTextureUnits != null) 
			{
				for(Element unit : eTextureUnits.getElements())
				{
					String unitType = unit.getName();
					//TODO: Make sure this actually is a good idea...
					File f = new File(unit.getAttribute("File"));
					File textureFile = DirectoryManager.locate(f.getName(), file.getParentFile());
					TextureUnit tu = new TextureUnit(ETextureUnitType.getTypeFromString(unitType), engine.getTextureLoader().loadTexture(textureFile));
					
					if(unit.hasAttribute("AnimFrameTime"))	tu.setAnimFrameTime(Utils.parseFloat(unit.getAttribute("AnimFrameTime")));
					if(unit.hasAttribute("AnimMode")) 		tu.setAnimMode(unit.getAttribute("AnimMode"));
					if(unit.hasAttribute("Compress")) 		tu.setCompress(Boolean.parseBoolean(unit.getAttribute("Compress")));
					if(unit.hasAttribute("File")) 			tu.setFile(unit.getAttribute("File"));
					if(unit.hasAttribute("MipMaps")) 		tu.setMipMaps(Boolean.parseBoolean(unit.getAttribute("MipMaps")));
					if(unit.hasAttribute("Type")) 			tu.setType(unit.getAttribute("Type"));
					if(unit.hasAttribute("Wrap")) 			tu.setWrap(unit.getAttribute("Wrap"));
					
					material.setTextureUnit(tu);
				}
			}
			
			if(eSpecificVariables != null)
			{
				for(Element var : eSpecificVariables.getElements())
				{
					String varName = var.getAttribute("Name");
					String varValue = var.getAttribute("Value");
					material.setSpecificVariable(varName, varValue);
				}
			}
			
			engine.getMemoryManager().addFile(file, material);
			
		} catch(Exception e) {
			Log.error("Failed to load material file!");
			e.printStackTrace();
			return null;
		}
		
		return material;
	}
	
	/**
	 * Write a <code>Material</code> to a file. The output file format will be XML formatted.
	 * @param material
	 * @param location
	 * @return
	 */
	public boolean writeFile(Material material, String location)
	{
		return writeFile(material, new File(location));
	}
	
	/**
	 * Write a <code>Material</code> to a file. The output file format will be XML formatted.
	 * @param material
	 * @param location
	 * @return
	 */
	public boolean writeFile(Material material, File location)
	{
		XMLParser xml = new XMLParser();
		Element eMaterial = xml.addElement("Material");
		Element eMain = eMaterial.addElement("Main");
		eMain.addAttribute("BlendMode", material.getBlendMode());
		eMain.addAttribute("DepthTest", ""+material.getDepthTest());
		eMain.addAttribute("PhysicsMaterial", material.getPhysicsMaterial());
		eMain.addAttribute("Type", material.getType());
		eMain.addAttribute("UseAlpha", ""+material.getUseAlpha());
		
		Element eTextureUnits = eMaterial.addElement("TextureUnits");
		for(ETextureUnitType unitName : material.getTextureUnits().keySet())
		{
			TextureUnit unit = material.getTextureUnit(unitName);
			
			Element eUnit = eTextureUnits.addElement(unitName.getString());
			eUnit.addAttribute("AnimFrameTime", ""+unit.getAnimFrameTime());
			eUnit.addAttribute("AnimMode", unit.getAnimMode());
			eUnit.addAttribute("Compress", "false");
			eUnit.addAttribute("File", unit.getFile());
			eUnit.addAttribute("MipMaps", "true");
			eUnit.addAttribute("Type", unit.getType());
			eUnit.addAttribute("Wrap", unit.getWrap());
		}
		
		Element eSpecificVariables = eMaterial.addElement("SpecificVariables");
		for(String varName : material.getSpecificVariables().keySet())
		{
			Element eVar = eSpecificVariables.addElement("Var");
			eVar.addAttribute("Name", varName);
			eVar.addAttribute("Value", material.getSpecificVariable(varName));
		}
		
//		xml.printDebug = true;
		xml.write(location);
		return true;
	}
	
}
