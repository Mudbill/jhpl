package engine.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTBgra;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import engine.Engine;
import engine.core.LowLevelTexture;
import engine.textures.DDSFile;
import engine.util.Log;
import engine.util.Utils;

public class TexLoader {

	//==============================================================================
	// Private fields
	//==============================================================================

	private Engine engine;
	
	private List<Integer> textureIDs;
	
	private static enum ESupportedExtensions
	{
		NOT_SUPPORTED,
		DDS, TGA, PNG, BMP, GIF, JPG;
		
		public static ESupportedExtensions getFromString(String string)
		{
			for(ESupportedExtensions ext : values())
				if(string.equalsIgnoreCase(ext.toString())) return ext;
			return NOT_SUPPORTED;
		}
	}

	private LowLevelTexture whiteTexture;
	private LowLevelTexture grayTexture;
	private LowLevelTexture blackTexture;
	
	//==============================================================================
	// Public methods
	//==============================================================================
	
	public TexLoader(Engine engine) {
		Log.info("Creating texture loader...");
		this.engine = engine;
		textureIDs = new ArrayList<Integer>();
	}
	
	public void createCoreTextures() {
		whiteTexture = createTextureFromInt(0xffffff);
		grayTexture  = createTextureFromInt(0xd3d3d3);
		blackTexture = createTextureFromInt(0x000000);
	}
	
	public LowLevelTexture loadCubemapTexture(String fileName)
	{
		int textureID = GL11.glGenTextures();
		DDSFile dds = loadDDSFile(new File(fileName), textureID);
		if(!dds.isCubeMap()) {
			Log.error("This file is not a cubemap!");
			return null;
		}
		textureIDs.add(textureID);
		return new LowLevelTexture(textureID, fileName);
	}
	
	/**
	 * Loads a texture from the supported extensions and returns the generated OpenGL ID for it contained inside a LowLevelTexture object.
	 * @param String path
	 * @return
	 */
	public LowLevelTexture loadTexture(String fileName) 
	{
		return loadTexture(new File(fileName));
	}
	
	/**
	 * Loads a texture from the supported extensions and returns the generated OpenGL ID for it contained inside a LowLevelTexture object.
	 * @param file
	 * @return
	 */
	public LowLevelTexture loadTexture(File file) 
	{
		try {
			
			if(file == null)
			{
				return loadTexture("resources/core/error.png");
			}
			
			// If texture is already loaded, reuse it instead of loading another copy.
			if(engine.getMemoryManager().isFileLoaded(file))
			{
				Log.info("Texture already loaded '"+file+"'");
				return (LowLevelTexture) engine.getMemoryManager().getLoadedFile(file);
			}
			
			ESupportedExtensions ext = ESupportedExtensions.getFromString(Utils.getFileExtension(file));
						
			int id = -1;
			
			switch(ext)
			{
			case DDS:
				id = loadDDS(file);
				break;
			case TGA:
				id = loadTGA(file);
				break;
			case PNG:
				id = loadSlick(file, ext.toString());
				break;
			case BMP:
				id = loadBMP(file);
				break;
			case GIF:
				id = loadSlick(file, ext.toString());
				break;
			case JPG:
				id = loadSlick(file, ext.toString());
				break;
			case NOT_SUPPORTED:
				Log.error("TEXLOADER: Format '"+ext.toString()+"' not supported!");
			}
			
			if(id == -1) return loadTexture("resources/core/error.png");
			
			LowLevelTexture llt = new LowLevelTexture(id, file.getAbsolutePath());
			textureIDs.add(id);
			engine.getMemoryManager().addFile(file, llt);
			return llt;
			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
			return loadTexture("resources/core/error.png");
		}
	}
	
	/**
	 * Deletes all saved textures from OpenGL.
	 */
	public void cleanUp()
	{
		for(int textureID : textureIDs) GL11.glDeleteTextures(textureID);
		textureIDs.clear();
	}
	
	public LowLevelTexture getWhiteTexture() {
		return whiteTexture;
	}
	
	public LowLevelTexture getGrayTexture() {
		return grayTexture;
	}
	
	public LowLevelTexture getBlackTexture() {
		return blackTexture;
	}
	
	//==============================================================================
	// Package private methods
	//==============================================================================

	LowLevelTexture createTextureFromInt(int value)
	{
		ByteBuffer buffer = BufferUtils.createByteBuffer(4);
		buffer.putInt(value);
		buffer.flip();
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 1, 1, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);
		LowLevelTexture texture = new LowLevelTexture(textureID, null);
//		Log.info("Creating a texture from %s. ID = %d", Integer.toHexString(value), textureID);
		textureIDs.add(textureID);
		return texture;
	}
	
	//==============================================================================
	// Private methods
	//==============================================================================

	private int loadTGA(File file)
	{
		
		return -1;
	}
	
	private int loadBMP(File bmpFile)
	{
		long startTime = System.currentTimeMillis();
		
		// Read the BMP header.
		int height = 0;
		int width = 0;
		int dataPos;
		int imageSize;
		ByteBuffer dataBuffer = null;

		try
		{
			byte[] data;
			byte[] header = new byte[54];

			FileInputStream fis = new FileInputStream(bmpFile);
			fis.read(header);

			if(header[0] != 'B' || header[1] != 'M')
			{
				System.err.println("Not a BMP file!");
				fis.close();
				return -1;
			}

			ByteBuffer buffer = BufferUtils.createByteBuffer(header.length);
			buffer.put(header);
			buffer.flip();

			dataPos 	= buffer.getInt(0x0A);
			imageSize 	= buffer.getInt(0x22);
			width 		= buffer.getInt(0x12);
			height 		= buffer.getInt(0x16);

			if(imageSize == 0)	imageSize = width*height*3;
			if(dataPos == 0)	dataPos = 54;

			data = new byte[imageSize];
			fis.read(data);

			dataBuffer = BufferUtils.createByteBuffer(imageSize);
			dataBuffer.put(data);
			dataBuffer.flip();

			fis.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, EXTBgra.GL_BGR_EXT, GL11.GL_UNSIGNED_BYTE, dataBuffer);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
		long endTime = System.currentTimeMillis();
		Log.info("Loaded BMP '" + bmpFile.getAbsolutePath() + "' in " + (endTime - startTime) + " ms.");
		
		return textureID;
	}
	
	/**
	 * Reads the byte data from the given DDS formatted image file and creates an OpenGL texture object for it, before returning the ID.
	 * @param ddsFile
	 * @return
	 */
	private int loadDDS(File ddsFile)
	{
		if(Utils.getFileExtension(ddsFile).equalsIgnoreCase("dds") == false)
		{
			System.err.println("Not a DDS file!");
			return -1;
		}
		
		if(ddsFile.isFile() == false)
		{
			System.err.println("DDS file not found: "+ddsFile.getPath());
//			return -1;
		}
		
		long startTime = System.currentTimeMillis();
		
		int textureID = GL11.glGenTextures();
		
		loadDDSFile(ddsFile, textureID);
		
		long endTime = System.currentTimeMillis();
		Log.info("Loaded DDS '" + ddsFile.getAbsolutePath() + "' in " + (endTime - startTime) + " ms. ID = "+textureID);
		
		return textureID;
	}
	
	/**
	 * Reads the byte data from the given DDS formatted image file and loads it to the given texture ID generated.
	 * @param ddsFile
	 * @param textureID
	 * @return
	 */
	private DDSFile loadDDSFile(File ddsFile, int textureID)
	{
		DDSFile dds = new DDSFile();
//		dds.printDebug = true;
		dds.loadFile(ddsFile);
		
		if(dds.isCubeMap())
		{
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureID);
			GL13.glCompressedTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, dds.getDXTFormat(), dds.getWidth(), dds.getHeight(), 0, dds.getCubeMapPositiveX());
			GL13.glCompressedTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, dds.getDXTFormat(), dds.getWidth(), dds.getHeight(), 0, dds.getCubeMapNegativeX());
			GL13.glCompressedTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, dds.getDXTFormat(), dds.getWidth(), dds.getHeight(), 0, dds.getCubeMapPositiveY());
			GL13.glCompressedTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, dds.getDXTFormat(), dds.getWidth(), dds.getHeight(), 0, dds.getCubeMapNegativeY());
			GL13.glCompressedTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, dds.getDXTFormat(), dds.getWidth(), dds.getHeight(), 0, dds.getCubeMapPositiveZ());
			GL13.glCompressedTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, dds.getDXTFormat(), dds.getWidth(), dds.getHeight(), 0, dds.getCubeMapNegativeZ());
//			GL30.glGenerateMipmap(GL13.GL_TEXTURE_CUBE_MAP);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE); 
		}
		else
		{
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
			GL13.glCompressedTexImage2D(GL11.GL_TEXTURE_2D, 0, dds.getDXTFormat(), dds.getWidth(), dds.getHeight(), 0, dds.getBuffer());
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		}
		
		return dds;
	}

	/**
	 * Loads a texture file using the SlickUtils library, which supports PNG, TGA, JPG and GIF. Returns the OpenGL texture object ID.
	 * @param file
	 * @param extension
	 * @return
	 */
	private int loadSlick(File file, String extension)
	{
		Texture texture = null;
		String format = extension.toUpperCase();
		
		long startTime = System.currentTimeMillis();
		
		try
		{
//			Log.info("SLICK: Loading texture '%s'", file.getAbsolutePath());
			texture = TextureLoader.getTexture(format, new FileInputStream(file));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
//			glTexParameterf(GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
		} 
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		long endTime = System.currentTimeMillis();
		int textureID = texture.getTextureID();
		Log.info("Loaded "+format+" '"+file.getAbsolutePath()+"' in " + (endTime - startTime) + " ms. ID = " +textureID);
		return textureID;
	}
	
}
