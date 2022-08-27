package engine.loader;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import collada.ColladaMesh;
import collada.ColladaModel;
import collada.ColladaParser;
import engine.Engine;
import engine.core.Cube;
import engine.core.Cylinder;
import engine.core.EPrimitiveType;
import engine.core.LowLevelMesh;
import engine.core.LowLevelTexture;
import engine.core.Plane;
import engine.core.Primitive;
import engine.main.Application;
import engine.materials.ETextureUnitType;
import engine.materials.Material;
import engine.objects.Mesh;
import engine.objects.Model;
import engine.system.DirectoryManager;
import engine.util.Log;
import engine.util.Utils;

public class Loader {

	//==============================================================================
	// Private fields
	//==============================================================================
	
	private Engine engine;
	
	private List<Integer> vaos;
	private List<Integer> vbos;
	
	//==============================================================================
	// Public methods
	//==============================================================================
	
	public Loader(Engine engine)
	{
		Log.info("Creating loader...");
		this.engine = engine;
		vaos = new ArrayList<Integer>();
		vbos = new ArrayList<Integer>();
	}
	
	/**
	 * Loads the COLLADA model file located at the given file path.
	 * @param fileName
	 * @return A <code>Model</code> with the loaded info, or null if file not found or load failed.
	 */
	public Model loadModel(String fileName)
	{
		long start = System.currentTimeMillis();
		
		File file = new File(fileName);
		
		if(file.isFile() == false)
		{
			System.err.println("File not found '"+fileName+"'");
			return null;
		}
		
		if(engine.getMemoryManager().isFileLoaded(file))
		{
			System.out.println("INFO: Model '"+file+"' is already loaded.");
			Application.getForm().setLoadingLabelText("'"+file.getName()+"' already loaded.", true);
			return (Model) engine.getMemoryManager().getLoadedFile(file);
		}
		
		ColladaModel cModel = new ColladaModel();
		ColladaParser.printDebug = false;
		ColladaParser.printXmlDebug = false;
		ColladaParser.forceCalculateTangents = true;

//		File serFile = new File(file.getParent()+File.separator+Utils.trimFileExtension(file.getName())+".ser");
//		if(serFile.isFile()) {
//			Log.info("Found serialized file '%s'", serFile.getAbsolutePath());
//			cModel = (ColladaModel) deserialize(serFile);
//		} else 
		
		try
		{
			cModel = ColladaParser.loadModel(file);
		}
		catch (Exception e)
		{
			Utils.promptError("Failed to load model!\n\n"+e.toString());
			e.printStackTrace();
			return null;
		}
		
		List<Mesh> meshList = new ArrayList<Mesh>();

		for(ColladaMesh cMesh : cModel.getMeshes())
		{
			LowLevelMesh llm = loadToVAO(cMesh.getPositions(), cMesh.getIndices(), cMesh.getTexcoords(), cMesh.getNormals(), cMesh.getTangents());
			
			String filePath = cMesh.getFilePath();
			File matFile = null;
			
			/* If model file has a texture material included. */
			if(!filePath.isEmpty())
			{
				filePath = Utils.trimFileExtension(filePath) + ".mat";
				matFile = DirectoryManager.locate(filePath, file.getParentFile());
			}
			else Log.error("Failed to locate material file!");
			
			Material mat = engine.getMaterialHandler().loadMaterialFile(matFile);
			Mesh mesh = new Mesh(llm, mat);
			meshList.add(mesh);
		}
		
//		if(!serFile.isFile())
//			serialize(cModel, Utils.trimFileExtension(file.getName())+".ser");
		
		Mesh[] meshes = new Mesh[0];
		meshes = meshList.toArray(meshes);
		
		Model model = new Model(meshes);
		engine.getMemoryManager().addFile(file, model);
		Application.getForm().setAdditionalTitle(file.getPath().substring(Application.ROOT_DIR.length()), true);
		
		long ms = System.currentTimeMillis() - start;
		Log.info("Total time to load model: "+ms+"ms");
		Application.getForm().setLoadingLabelText("Loaded '"+file.getName()+"' in "+ms+" ms.", true);
		return model;
	}
	
	/**
	 * Loads a primitive model.
	 * @param shape - The <code>Primitive</code> of the shape.
	 * @return A <code>Model</code> of the shape.
	 */
	public Model loadPrimitive(Primitive shape)
	{
		LowLevelTexture lowTex = shape.getTexture();
		Material tex = new Material();
		tex.setTextureUnit(ETextureUnitType.DIFFUSE, lowTex);
		LowLevelMesh lowMesh = loadToVAO(shape.getPositions(), shape.getIndices(), shape.getTexcoords(), shape.getNormals(), shape.getTangents());
		Mesh[] meshes = new Mesh[1];
		meshes[0] = new Mesh(lowMesh, tex);
		Model model = new Model(meshes);
		return model;
	}
	
	/**
	 * Loads a primitive model.
	 * @param shape - The shape of the primitive, from EPrimitiveType.
	 * @param texture - The texture path.
	 * @return
	 */
	public Model loadPrimitive(EPrimitiveType shape, String texture)
	{
		return loadPrimitive(shape, engine.getTextureLoader().loadTexture(texture));
	}
	
	/**
	 * Loads a primitive model.
	 * @param shape - The shape of the primitive, from EPrimitiveType.
	 * @param texture - The texture object to use.
	 * @return
	 */
	public Model loadPrimitive(EPrimitiveType shape, LowLevelTexture texture)
	{
		Primitive b = null;
		
		switch(shape)
		{
		case CUBE:
			b = new Cube(texture);
			break;
		case PLANE:
			b = new Plane(texture);
			break;
		case CYLINDER:
			b = new Cylinder(texture);
			break;
		default:
			b = new Plane(texture);
		}
		
		return loadPrimitive(b);
	}
	
	/**
	 * Deletes the loaded data using the generated identifiers. This should be the final call for this class, and used during shutdown.
	 */
	public void cleanUp()
	{
		for(int vao : vaos)	GL30.glDeleteVertexArrays(vao);
		vaos.clear();
		for(int vbo : vbos)	GL15.glDeleteBuffers(vbo);
		vbos.clear();
	}

	//==============================================================================
	// Private methods
	//==============================================================================
	
	/**
	 * Loads the given model information into a new Vertex Array Object, with a Vertex Buffer Object for each.
	 * @param positions
	 * @param indices
	 * @param texCoords
	 * @param normals
	 * @param tangents
	 * @return
	 */
	private LowLevelMesh loadToVAO(float[] positions, int[] indices, float[] texCoords, float[] normals, float[] tangents)
	{
		int vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		storeAttribute(0, positions, 3);
		storeAttribute(1, indices, 3);
		storeAttribute(2, texCoords, 2);
		if(normals != null) storeAttribute(3, normals, 3);
		if(tangents != null) storeAttribute(4, tangents, 3);
		//if(binormals != null) storeAttribute(5, binormals, 3);
		GL30.glBindVertexArray(0);
		vaos.add(vaoID);
		return new LowLevelMesh(vaoID, indices.length);
	}
	
	public LowLevelMesh loadToVAO(float[] positions)
	{
		int vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		storeAttribute(0, positions, 2);
		GL30.glBindVertexArray(0);
		vaos.add(vaoID);
		return new LowLevelMesh(vaoID, positions.length / 2);
	}
	
	/**
	 * Stores a float array as an array buffer.
	 * @param index
	 * @param data
	 * @param unitSize
	 */
	private void storeAttribute(int index, float[] data, int unitSize)
	{
		FloatBuffer buffer = Utils.newFloatBuffer(data);
		int vboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, unitSize, GL11.GL_FLOAT, false, 0, 0);
		GL20.glEnableVertexAttribArray(index);
		vbos.add(vboID);
	}
	
	/**
	 * Stores an int array as an element array buffer.
	 * @param index
	 * @param data
	 * @param unitSize
	 */
	private void storeAttribute(int index, int[] data, int unitSize)
	{
		IntBuffer buffer = Utils.newIntBuffer(data);
		int eboID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, eboID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, unitSize, GL11.GL_UNSIGNED_INT, false, 0, 0);
		GL20.glEnableVertexAttribArray(index);
		vbos.add(eboID);
	}
}
