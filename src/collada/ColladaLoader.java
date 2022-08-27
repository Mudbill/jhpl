package collada;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import collada.internal.CAsset;
import collada.internal.CBlinn;
import collada.internal.CEffect;
import collada.internal.CGeometry;
import collada.internal.CImage;
import collada.internal.CInput;
import collada.internal.CInstanceController;
import collada.internal.CInstanceEffect;
import collada.internal.CInstanceGeometry;
import collada.internal.CInstanceMaterial;
import collada.internal.CMaterial;
import collada.internal.CNewParam;
import collada.internal.CNode;
import collada.internal.CSampler2D;
import collada.internal.CScene;
import collada.internal.CSource;
import collada.internal.CTechnique;
import collada.internal.CTechniqueCommon;
import collada.internal.CTexture;
import collada.internal.CTriangles;
import collada.internal.CVertices;
import collada.internal.CVisualScene;
import engine.util.Log;
import engine.util.XMLParser;
import engine.util.XMLParser.Element;

public class ColladaLoader {

	/** Enable to print debug messages to console */
	private static boolean printDebug = false;
	
	/** No constructor for you! */
	private ColladaLoader() {}
	
	private static void log(String text, Object... args) {
		if(printDebug) Log.info(text, args);
	}
	
	private static void err(String text, Object... args) {
		if(printDebug) Log.error(text, args);
	}
	
	/**
	 * Loads a collada file and extracts the model data from it.
	 * @param file
	 * @return 
	 */
	public static ColladaDocument loadFile(File file)
	{
		printDebug = ColladaParser.printDebug;
		
		if(!file.isFile()) 
		{
			Log.error("File not found '"+file+"'");
			return null;
		}
		
		XMLParser parser = new XMLParser();
		parser.printDebug = ColladaParser.printXmlDebug;
		try
		{
			long xmlReadStartTime = System.currentTimeMillis();
			parser.read(file);
			if(printDebug) Log.info("Time to load XML document: %d ms.", System.currentTimeMillis() - xmlReadStartTime);
		} 
		catch (Exception e)
		{
			log("Failed to load XML document!");
			e.printStackTrace();
			return null;
		}
		
		Element eCollada = parser.getElement("COLLADA");
		
		if(eCollada != null)
		{			
			log("Loading Collada file '"+file.getAbsolutePath()+"' version: "+eCollada.getAttribute("version"));	
			return readDocumentFromXml(eCollada);
		}
		
		log("Collada file invalid!");
		return null;
	}
	
	private static ColladaDocument readDocumentFromXml(Element eCollada)
	{
		ColladaDocument cModel = new ColladaDocument();
		Element eAsset;
		Element eLibraryImages;
		Element eLibraryMaterials;
		Element eLibraryEffects;
		Element eLibraryGeometries;
		Element eLibraryVisualScenes;
		Element eScene;
		
		log("-- Loading asset section.");
		if((eAsset = eCollada.getElement("asset")) != null)
			cModel.setColladaAsset(loadColladaAsset(eAsset));
		else err("No 'asset' element found!");
		
		log("-- Loading images section.");
		if((eLibraryImages = eCollada.getElement("library_images")) != null)
			for(Element eImage : eLibraryImages.getElementsOfName("image"))
				cModel.addColladaImage(loadColladaImage(eImage));
		else err("No 'library_images' element found!");
		
		log("-- Loading materials section.");
		if((eLibraryMaterials = eCollada.getElement("library_materials")) != null)
		for(Element eMaterial : eLibraryMaterials.getElementsOfName("material"))
			cModel.addColladaMaterial(loadColladaMaterial(eMaterial));
		else err("No 'library_materials' element found!");

		log("-- Loading effects section.");
		if((eLibraryEffects = eCollada.getElement("library_effects")) != null)
		for(Element eEffect : eLibraryEffects.getElementsOfName("effect"))
			cModel.addColladaEffect(loadColladaEffect(eEffect));
		else err("No 'library_effects' element found!");

		log("-- Loading geometries section.");
		if((eLibraryGeometries = eCollada.getElement("library_geometries")) != null)
		for(Element eGeometry : eLibraryGeometries.getElementsOfName("geometry"))
			cModel.addColladaGeometry(loadColladaGeometry(eGeometry));
		else err("No 'library_geometries' element found!");

		log("-- Loading visual scenes section.");
		if((eLibraryVisualScenes = eCollada.getElement("library_visual_scenes")) != null)
		for(Element eVisualScene : eLibraryVisualScenes.getElementsOfName("visual_scene"))
			cModel.addColladaVisualScene(loadColladaVisualScene(eVisualScene));
		else err("No 'library_visual_scenes' element found!");

		log("-- Loading scenes section.");
		if((eScene = eCollada.getElement("scene")) != null)
			cModel.setColladaScene(loadColladaScene(eScene));
		else err("No 'scene' element found!");

		log("---- EXTRACTION COMPLETE ----");
		return cModel;
	}
	
	private static CAsset loadColladaAsset(Element asset)
	{
		Element eUpAxis = asset.getElement("up_axis");
		Element eUnitMeter = asset.getElement("unit");
		String up_axis = "";
		float unitMeter = 1.0f;
		
		if(eUpAxis != null)	up_axis = eUpAxis.getValue();
		if(eUnitMeter != null && !eUnitMeter.getValue().isEmpty())	unitMeter = Float.parseFloat(eUnitMeter.getAttribute("unit"));
		
		return new CAsset(up_axis, unitMeter);
	}
	
	private static CScene loadColladaScene(Element scene)
	{
		Element eInstanceVisualScene = scene.getElement("instance_visual_scene");
		if(eInstanceVisualScene == null)
		{
			err("Failed to load 'instance_visual_scene' element from scene!");
			return null;
		}
		
		String sid = eInstanceVisualScene.getAttribute("sid");
		String name = eInstanceVisualScene.getAttribute("name");
		String url = eInstanceVisualScene.getAttribute("url");
		log("Adding collada scene: sid='%s', name='%s', url='%s'", sid, name, url);
		return new CScene(sid, name, url);
	}
	
	private static CVisualScene loadColladaVisualScene(Element element)
	{
		String id = element.getAttribute("id");
		String name = element.getAttribute("name");
		List<CNode> lNode = new ArrayList<CNode>();
		
		for(Element eNode : element.getElementsOfName("node"))
			loadNode(lNode, eNode);
		
		log("Adding collada visual scene: id='%s', name='%s', nodes=%d", id, name, lNode.size());
		return new CVisualScene(id, name, lNode);
	}
	
	private static void loadNode(List<CNode> nodeList, Element eNode)
	{
		String nID = eNode.getAttribute("id");
		String nName = eNode.getAttribute("name");
		String nSID = eNode.getAttribute("sid");
		String nType = eNode.getAttribute("type");
		if(nType.isEmpty()) nType = "NODE";

		List<Vector4f> vRotate = new ArrayList<Vector4f>();
		Vector3f vTranslate = new Vector3f();
		Vector3f vScale = new Vector3f();
		
		List<Element> eRotateList = eNode.getElementsOfName("rotate");
		for(Element eRotate : eRotateList)
		{
			String[] sValues = eRotate.getValue().split(" ");
			float[] fValues = new float[sValues.length];
			for(int i = 0; i < sValues.length; i++) 
				fValues[i] = Float.parseFloat(sValues[i]);
			vRotate.add(new Vector4f(fValues[0], fValues[1], fValues[2], fValues[3]));
		}
		
		Element eTranslate = eNode.getElement("translate");
		if(eTranslate != null)
		{
			String[] sValues = eTranslate.getValue().split(" ");
			float[] fValues = new float[sValues.length];
			for(int i = 0; i < sValues.length; i++)
				fValues[i] = Float.parseFloat(sValues[i]);
			vTranslate.x = fValues[0];
			vTranslate.y = fValues[1];
			vTranslate.z = fValues[2];
		}
		
		Element eScale = eNode.getElement("scale");
		if(eScale != null)
		{
			String[] sValues = eScale.getValue().split(" ");
			float[] fValues = new float[sValues.length];
			for(int i = 0; i < sValues.length; i++)
				fValues[i] = Float.parseFloat(sValues[i]);
			vScale.x = fValues[0];
			vScale.y = fValues[1];
			vScale.z = fValues[2];
		}
		
		Element eInstanceController = eNode.getElement("instance_controller");
		CInstanceController cInstanceController = null;
		if(eInstanceController != null) cInstanceController = loadInstanceController(eInstanceController);
		
		Element eInstanceGeometry = eNode.getElement("instance_geometry");
		CInstanceGeometry cInstanceGeometry = null;
		if(eInstanceGeometry != null) cInstanceGeometry = loadInstanceGeometry(eInstanceGeometry);
		
		List<CNode> children = new ArrayList<CNode>();
		for(Element child : eNode.getElementsOfName("node"))
			loadNode(children, child);
		
		log("Adding node: id='%s', name='%s', sid='%s', type='%s', nodes=%d, instance_controller="+(cInstanceController != null), nID, nName, nSID, nType, children.size(), cInstanceController);
		nodeList.add(new CNode(nID, nName, nSID, nType, vTranslate, vRotate, vScale, children, cInstanceController, cInstanceGeometry));
	}
	
	private static CInstanceController loadInstanceController(Element eInstanceController)
	{
		String url = eInstanceController.getAttribute("url");
		String skeleton = eInstanceController.getElement("skeleton").getValue();

		Element eBindMaterial = eInstanceController.getElement("bind_material");
		if(eBindMaterial == null)
		{
			err("Failed to load 'bind_material' element from instance_controller!");
			return null;
		}
		
		CInstanceMaterial cInstanceMaterial = loadInstanceMaterial(eBindMaterial);
		return new CInstanceController(url, skeleton, cInstanceMaterial);
	}
	
	private static CInstanceGeometry loadInstanceGeometry(Element eInstanceGeometry)
	{
		String url = eInstanceGeometry.getAttribute("url");
		
		Element eBindMaterial = eInstanceGeometry.getElement("bind_material");
		
		CInstanceMaterial cInstanceMaterial = null;
		if(eBindMaterial != null) cInstanceMaterial = loadInstanceMaterial(eBindMaterial);
		return new CInstanceGeometry(url, cInstanceMaterial);
	}
	
	private static CInstanceMaterial loadInstanceMaterial(Element eBindMaterial)
	{
		Element eTechniqueCommon = eBindMaterial.getElement("technique_common");
		if(eTechniqueCommon == null)
		{
			err("Failed to load 'technique_common' element from bind_material!");
			return null;
		}
		
		Element eInstanceMaterial = eTechniqueCommon.getElement("instance_material");
		if(eInstanceMaterial == null)
		{
			err("Failed to load 'instance_material' element from bind_material!");
			return null;
		}
		
		String symbol = eInstanceMaterial.getAttribute("symbol");
		String target = eInstanceMaterial.getAttribute("target");
		
		Element eBindVertexInput = eInstanceMaterial.getElement("bind_vertex_input");
		if(eBindVertexInput == null)
		{
			err("Failed to load 'bind_vertex_input' element from bind_material!");
			return null;
		}
	
		String semantic = eBindVertexInput.getAttribute("semantic");
		String inputSemantic = eBindVertexInput.getAttribute("input_semantic");
		int inputSet = toInt(eBindVertexInput.getAttribute("input_set"));
		
		log("Adding instance_material: symbol='%s', target='%s', semantic='%s', input_semantic='%s', input_set='%s'", symbol, target, semantic, inputSemantic, inputSet);
		CInstanceMaterial cInstanceMaterial = new CInstanceMaterial(symbol, target, semantic, inputSemantic, inputSet);
		return cInstanceMaterial;
	}
	
	/**
	 * Loads a single <code>geometry</code> element from the collada document.
	 * @param element
	 * @return
	 */
	private static CGeometry loadColladaGeometry(Element element)
	{
		String id = element.getAttribute("id");
		
		Element eMesh = element.getElement("mesh");
		if(eMesh == null)
		{
			err("Failed to load 'mesh' element from geometry!");
			return null;
		}
		
		List<CSource> lSource = new ArrayList<CSource>();
		
		for(Element eSource : eMesh.getElementsOfName("source"))
		{
			String sID = eSource.getAttribute("id");
			
			Element eFloatArray = eSource.getElement("float_array");
			if(eFloatArray == null)
			{
				err("Failed to load 'float_array' element from geometry!");
				continue;
			}
			
			int count = toInt(eFloatArray.getAttribute("count"));
			
			// Convert float array String data to an actual float array.
			float[] fFloatArray = new float[count];
			String sFloatArrayData = removeWhiteSpace(eFloatArray.getValue());
			String[] sFloatArray = sFloatArrayData.split(" ");
			for(int i = 0; i < count; i++)
				fFloatArray[i] = Float.parseFloat(sFloatArray[i]);
			
			Element eTechniqueCommon = eSource.getElement("technique_common");
			if(eTechniqueCommon == null)
			{
				err("Failed to load 'technique_common' element from geometry!");
				continue;
			}
			
			Element eAccessor = eTechniqueCommon.getElement("accessor");
			if(eAccessor == null)
			{
				err("Failed to load 'accessor' element from geometry!");
				continue;
			}
			
			String sSource = eAccessor.getAttribute("source");
			int iCount = toInt(eAccessor.getAttribute("count"));
			int iStride = toInt(eAccessor.getAttribute("stride"));
			
			CTechniqueCommon cTechniqueCommon = new CTechniqueCommon(sSource, iCount, iStride);

			log("Adding source: id='%s', count=%d, stride=%d", sID, count, cTechniqueCommon.getStride());
			lSource.add(new CSource(sID, count, fFloatArray, cTechniqueCommon));
//			Log.info("Floats: "+Arrays.toString(fFloatArray));
		}
		
		/* Loading the triangles element */
		Element eTriangles = eMesh.getElement("triangles");
		if(eTriangles == null)
		{
			err("Failed to load 'triangles' element from geometry!");
			return null;
		}
		
		String tMaterial = eTriangles.getAttribute("material");
		int tCount = toInt(eTriangles.getAttribute("count"));
		List<CInput> tInputs = new ArrayList<CInput>();
		for(Element eInput : eTriangles.getElementsOfName("input"))
		{
			String semantic = eInput.getAttribute("semantic");
			String source = eInput.getAttribute("source");
			
			if(source.startsWith("#"))
			{
				//log("Removing # from input source '%s'", source);
				source = source.substring(1);
			}
			
			int offset = toInt(eInput.getAttribute("offset"));
			int set = toInt(eInput.getAttribute("set"));
			log("Adding input: semantic='%s', source='%s', offset=%d, set=%d", semantic, source, offset, set);
			tInputs.add(new CInput(semantic, source, offset, set));
		}
		
		Element eP = eTriangles.getElement("p");
		if(eP == null)
		{
			err("Failed to load 'p' element from geometry!");
			return null;
		}
		
		String[] sPdata = removeWhiteSpace(eP.getValue()).split(" ");
		int[] iP = new int[sPdata.length];
		for(int i = 0; i < iP.length; i++)
			iP[i] = Integer.parseInt(sPdata[i]);
		
		log("Adding triangles: material='%s', count=%d, inputs=%d, p=%d", tMaterial, tCount, tInputs.size(), iP.length);
//		Log.info("Ints: "+Arrays.toString(iP));
		CTriangles cTriangles = new CTriangles(tMaterial, tCount, tInputs, iP);

		/* Loading the vertices element */
		Element eVertices = eMesh.getElement("vertices");
		if(eVertices == null)
		{
			err("Failed to load 'vertices' element from geometry!");
			return null;
		}
		
		String vID = eVertices.getAttribute("id");
		List<CInput> vInputs = new ArrayList<CInput>();
		for(Element eInput : eVertices.getElementsOfName("input"))
		{
			String semantic = eInput.getAttribute("semantic");
			String source = eInput.getAttribute("source");
			
			if(source.startsWith("#"))
			{
				//log("Removing # from input source '%s'", source);
				source = source.substring(1);
			}
			
			log("Adding input: semantic='%s', source='%s'", semantic, source);
			vInputs.add(new CInput(semantic, source, -1, -1));
		}
		
		log("Adding vertices: id='%s', inputs=%d", vID, vInputs.size());
		CVertices cVertices = new CVertices(vID, vInputs);
		
		log("Adding collada geometry: id='%s'", id);
		return new CGeometry(id, lSource, cVertices, cTriangles);
	}
	
	/**
	 * Loads a single <code>effect</code> element from the collada document.
	 * @param element
	 * @return
	 */
	private static CEffect loadColladaEffect(Element element)
	{
		String id = element.getAttribute("id");
		
		Element eProfileCommon 		= element.getElement("profile_COMMON");
		if(eProfileCommon == null)
		{
			err("Failed to load 'profile_COMMON' in effect!");
			return null;
		}
		
		Element eTechnique			= eProfileCommon.getElement("technique");
		if(eTechnique == null)
		{
			err("Failed to load 'technique' in effect!");
			return null;
		}
		
		Element eBlinn				= eTechnique.getElement("blinn");
		if(eBlinn == null) eBlinn 	= eTechnique.getElement("phong");
		if(eBlinn == null) eBlinn 	= eTechnique.getElement("lambert");
		if(eBlinn == null)
		{
			err("Failed to load 'lambert' in effect!");
			return null;
		}
		
		Element eDiffuse 			= eBlinn.getElement("diffuse");
		if(eDiffuse == null)
		{
			err("Failed to load 'diffuse' in effect!");
			return null;
		}
		
		Element eTexture 			= eDiffuse.getElement("texture");
		if(eTexture == null)
		{
			err("Failed to load 'texture' in effect!");
			return null;
		}
		
		Map<String, CNewParam> lNewParam = new HashMap<String, CNewParam>();
		
		for(Element eNewParam : eProfileCommon.getElementsOfName("newparam"))
		{
			String sid = eNewParam.getAttribute("sid");
			String source;
			
			Element eSampler2D = eNewParam.getElement("sampler2D");
			if(eSampler2D == null) 
			{
				eSampler2D = eNewParam.getElement("surface");
				if(eSampler2D == null)
				{
					err("Failed to load 'surface' in effect!");
					continue;
				}
				
				Element eInit_from = eSampler2D.getElement("init_from");
				if(eInit_from == null)
				{
					err("Failed to load 'init_from' in effect!");
					continue;
				}
				
				source = eInit_from.getValue();
			}
			else
			{
				Element eSource = eSampler2D.getElement("source");
				if(eSource == null)
				{
					err("Failed to load 'source' in effect!");
					continue;
				}
				
				source = eSource.getValue();
			}
			
			log("Adding sampler2D: source='%s'", source);
			CSampler2D sampler2D = new CSampler2D(source);
			
			log("Adding newparam: sid='%s'", sid);
			lNewParam.put(sid, new CNewParam(sid, sampler2D));
		}
		
		String sTexture = eTexture.getAttribute("texture");
		String sTexcoord = eTexture.getAttribute("texcoord");
		log("Adding texture: texture='%s', texcoord='%s'", sTexture, sTexcoord);
		CTexture texture = new CTexture(sTexture, sTexcoord);
		
		CBlinn blinn = new CBlinn(texture);
		
		String tID = eTechnique.getAttribute("id");
		String tSID = eTechnique.getAttribute("sid");
		
		CTechnique technique = new CTechnique(tID, tSID, blinn);
		
		log("Adding collada effect: id='%s', newparams=%d", id, lNewParam.size());
		return new CEffect(id, lNewParam, technique);
	}
	
	/**
	 * Loads a single <code>material</code> element from the collada document.
	 * @param element
	 * @return
	 */
	private static CMaterial loadColladaMaterial(Element element)
	{
		String id = element.getAttribute("id");
		String name	= element.getAttribute("name");
		
		Element eInstanceEffect = element.getElement("instance_effect");
		if(eInstanceEffect == null)
		{
			err("Failed to load 'instance_effect' in material!");
			return null;
		}
		
		String ie_sid	= eInstanceEffect.getAttribute("sid");
		String ie_name	= eInstanceEffect.getAttribute("name");
		String ie_url	= eInstanceEffect.getAttribute("url");
			
		log("Adding instance_effect: sid='%s', name='%s', url='%s'", ie_sid, ie_name, ie_url);
		CInstanceEffect instance_effect = new CInstanceEffect(ie_sid, ie_name, ie_url);
		
		log("Adding collada material: id='%s', name='%s'", id, name);
		return new CMaterial(id, name, instance_effect);
	}
	
	/**
	 * Loads a single <code>image</code> tag from the XML collada document.
	 * @param element
	 * @return
	 */
	private static CImage loadColladaImage(Element element)
	{
		String id 		= element.getAttribute("id");
		String name 	= element.getAttribute("name");
		String format 	= element.getAttribute("format");
		int height 		= toInt(element.getAttribute("height"));
		int width 		= toInt(element.getAttribute("width"));
		int depth 		= toInt(element.getAttribute("depth"));
		
		Element eInit_from = element.getElement("init_from");
		if(eInit_from == null)
		{
			err("Failed to get 'init_from' element in image!");
			return null;
		}
		
		CImage cImage = new CImage(id, name, format, height, width, depth);
		String sInit_from = eInit_from.getValue();
		cImage.setInit_from(sInit_from);
		
		log("Adding collada image: id='%s', name='%s', format='%s', height='%d', width='%d', depth='%d', init_from='%s'", id, name, format, height, width, depth, sInit_from);
		return cImage;
	}
	
	/**
	 * Parses the String to an integer, or prints an error message if it fails.
	 * @param s
	 * @return
	 */
	private static int toInt(String s) {
		if(s.isEmpty()) return 0;
		int i = 0;
		try { i = Integer.parseInt(s); } 
		catch(NumberFormatException e) {Log.error("Failed to parse int from String: %s", s);}
		return i;
	}
	
	/** Removes all line breaks, tabs and spaces and replaces them with only single spaces. */
	private static String removeWhiteSpace(String source) {
		return source.replace(System.lineSeparator(), " ").replace("\t", " ").replaceAll(" +", " ").trim();
	}
	
	//-------------
	
//	public static void main(String[] a) {
//		ColladaLoader.loadModel("resources/servant_grunt.dae");
//	}
}
