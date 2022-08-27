package engine.textures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import engine.util.Utils;
import static org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT;
import static org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
import static org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;

/** 
 * A class for loading DirectDraw Surface (*.dds) files for OpenGL. DDS files have many variants and so this parser only supports the following:
 * - Standard 124 byte headers (not extended D3D headers by Microsoft).
 * - Compressed textures using DXT1, DXT3 and DXT5 formats.
 * - Partially supports mipmaps.
 * - Cubemaps.
 * - Not yet volume maps.
 * - Not yet legacy formats.
 * Copyright (c) "Buttology Inc" | Mudbill
 */
public class DDSFile {

	public boolean printDebug = false;
	
	public boolean readAlpha = false;
	
	/** A 32-bit representation of the character sequence "DDS " which is the magic word for DDS files. */
	private static final int DDS_MAGIC = 0x20534444;

	/** Stores the magic word for the binary document read */
	private int 			dwMagic;
	
	/** The header information for this DDS document */
	private DDSHeader 		header;
	
	/** Arrays of bytes that contain the main surface image data */
	private List<ByteBuffer> bdata;
	
	/** Arrays of bytes that contain the secondary surface data, like mipmap levels */
	private List<ByteBuffer> bdata2;
	
	/** The calculated size of the image */
	private int 			imageSize;
	
	/** The compression format for the current DDS document */
	private int 			dxtFormat;
	
	/** Whether this DDS file is a cubemap or not */
	private boolean			isCubeMap;
	
	/** Empty constructor */
	public DDSFile() {}
	
	/**
	 * Loads a DDS file from the given file name.
	 * @param fileName
	 */
	public DDSFile(String fileName) 
	{
		this.loadFile(new File(fileName));
	}
	
	/**
	 * Loads a DDS file from the given file.
	 * @param file
	 */
	public DDSFile(File file) 
	{
		this.loadFile(file);
	}
	
	/**
	 * Loads a DDS file.
	 * @param file
	 */
	public void loadFile(File file) 
	{
		bdata = new ArrayList<ByteBuffer>();
		bdata2 = new ArrayList<ByteBuffer>();
		
		FileInputStream fis = null;
		
		if(file.isFile() == false)
		{
			System.err.println("DDS: File not found: '"+file.getAbsolutePath()+"'");
			return;
		}
		
		try 
		{
			fis = new FileInputStream(file);
			
			int totalByteCount = fis.available();
			if(printDebug) System.out.println("Total bytes: "+totalByteCount);
			
			byte[] bMagic = new byte[4];
			fis.read(bMagic);
			dwMagic = Utils.newByteBuffer(bMagic).getInt();
			
			if(dwMagic != DDS_MAGIC) 
			{
				System.err.println("Wrong magic word! This is not a DDS file.");
				return;
			}
			
			byte[] bHeader = new byte[124];
			fis.read(bHeader);
			header = new DDSHeader(Utils.newByteBuffer(bHeader), printDebug);
			
			int block_size = 16;
			if(header.ddspf.sFourCC.equalsIgnoreCase("DXT1")) 
			{
				dxtFormat = GL_COMPRESSED_RGBA_S3TC_DXT1_EXT;
				block_size = 8;
			}
			else if(header.ddspf.sFourCC.equalsIgnoreCase("DXT3")) 
			{
				dxtFormat = GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
			}
			else if(header.ddspf.sFourCC.equalsIgnoreCase("DXT5")) 
			{
				dxtFormat = GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
			}
			else if(header.ddspf.sFourCC.equalsIgnoreCase("DX10")) 
			{
				System.err.println("Uses DX10 extended header, which is not supported!");
			}
			else 
			{
				System.err.println("Surface format unknown or not supported: "+header.ddspf.sFourCC);
			}
			
			int surfaceCount;
			totalByteCount -= 128;
			
			if(header.hasCaps2CubeMap) 
			{
				surfaceCount = 6;
				isCubeMap = true; 
			}
			else 
			{
				surfaceCount = 1;
				isCubeMap = false;
			}
			
			imageSize = calculatePitch(block_size);
			
			int size = header.dwPitchOrLinearSize;
			
			if(size == 0)
			{
				if(printDebug) System.err.println("WARN: File does not include a valid PitchOrLinearSize value, calculating based on dimensions...");
				size = header.dwWidth * header.dwHeight;
			}
			
			if(printDebug) System.out.println("Calculated Image Size: "+imageSize);
			if(printDebug) System.out.println("PitchOrLinearSize: "+size);
			if(printDebug) System.out.println("Mipcount: "+header.dwMipMapCount);
			
//			for(int i = 0; i < surfaceCount; i++)
//			{				
//				byte[] bytes = new byte[size];
//			
//				if(printDebug) System.out.println("Getting main surface "+i+". Bytes. "+bytes.length);
//
//				fis.read(bytes);
//				totalByteCount -= bytes.length;
//				bdata.add(Utils.newByteBuffer(bytes));
//
//				if(header.hasFlagMipMapCount)
//				{
//					int size2 = Math.max(size / 4, block_size);
//					
//					for(int j = 0; j < header.dwMipMapCount-1; j++)
//					{
//						byte[] bytes2 = new byte[size2];
//
//						if(printDebug) System.out.println("Getting secondary surface "+j+". Bytes: "+bytes2.length);
//						
//						fis.read(bytes2);
//						totalByteCount -= bytes2.length;
//						bdata2.add(Utils.newByteBuffer(bytes2));
//						size2 = Math.max(size2 / 4, block_size);
//					}
//				}
//			}
			
				
			for(int surfaceIndex = 0; surfaceIndex < surfaceCount; surfaceIndex++)
			{
				int currentSurfaceSize = size;
				
				int mipmapIndex = 0;
				do
				{
					byte[] bytes = new byte[currentSurfaceSize];
					if(printDebug) System.out.println("Getting surface "+surfaceIndex+", level "+mipmapIndex+". Bytes: "+bytes.length);
					
					fis.read(bytes);
					totalByteCount -= bytes.length;
					
					List<ByteBuffer> bufferList = mipmapIndex == 0 ? bdata : bdata2;
					bufferList.add(Utils.newByteBuffer(bytes));
					
					currentSurfaceSize = Math.max(block_size, currentSurfaceSize / 4);
					
					mipmapIndex++;
				}
				while(mipmapIndex < header.dwMipMapCount);
			}
			
//			if(!readAlpha)
//			{
//				for(ByteBuffer buffer : bdata)
//				{
//					buffer.rewind();
//					for(int i = 0; i < buffer.capacity(); i+=4)
//					{
//						buffer.put(i, (byte) 1);
//					}
//				}
//			}
			
			if(printDebug) System.out.printf("Remaining bytes: %d (%d)%n", fis.available(), totalByteCount);
			
		} catch (FileNotFoundException e) {
			System.err.println("ERR: File not found: "+file.getAbsolutePath());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int calculatePitch(int blockSize) {
		return Math.max(1, ((header.dwWidth + 3) / 4)) * blockSize;
	}
	
	public int getWidth() {
		return header.dwWidth;
	}
	
	public int getHeight() {
		return header.dwHeight;
	}
	
	public ByteBuffer getBuffer() {
		return getMainData();
	}
	
	public ByteBuffer getMainData() {
		return bdata.get(0);
	}
	
	public int getMipMapLevels() {
		return this.header.dwMipMapCount;
	}
	
	public ByteBuffer getMipMapLevel(int level) {
		level = Math.min(Math.min(header.dwMipMapCount-1, level), Math.max(level, 0));
		return this.bdata2.get(level);
	}
	
	public ByteBuffer getCubeMapPositiveX() {
		if(!header.hasCaps2CubeMap) return null;
		return bdata.get(0);
	}
	
	public ByteBuffer getCubeMapNegativeX() {
		if(!header.hasCaps2CubeMap) return null;
		return bdata.get(1);
	}
	
	public ByteBuffer getCubeMapPositiveY() {
		if(!header.hasCaps2CubeMap) return null;
		return bdata.get(2);
	}
	
	public ByteBuffer getCubeMapNegativeY() {
		if(!header.hasCaps2CubeMap) return null;
		return bdata.get(3);
	}
	
	public ByteBuffer getCubeMapPositiveZ() {
		if(!header.hasCaps2CubeMap) return null;
		return bdata.get(4);
	}
	
	public ByteBuffer getCubeMapNegativeZ() {
		if(!header.hasCaps2CubeMap) return null;
		return bdata.get(5);
	}
	
	public ByteBuffer getCubeMapMipPXLevel(int level) {
		level = Math.min(Math.min(header.dwMipMapCount, level), Math.max(level, 0));
		return this.bdata2.get((level*1)-1);
	}
	
	public ByteBuffer getCubeMapMipNXLevel(int level) {
		level = Math.min(Math.min(header.dwMipMapCount, level), Math.max(level, 0));
		return this.bdata2.get((level*2)-1);
	}
	
	public ByteBuffer getCubeMapMipPYLevel(int level) {
		level = Math.min(Math.min(header.dwMipMapCount, level), Math.max(level, 0));
		if(printDebug) System.out.println((level*3)-1);
		return this.bdata2.get((level*3)-1);
	}
	
	public ByteBuffer getCubeMapMipNYLevel(int level) {
		level = Math.min(Math.min(header.dwMipMapCount, level), Math.max(level, 0));
		if(printDebug) System.out.println((level*4)-1);
		return this.bdata2.get((level*4)-1);
	}
	
	public ByteBuffer getCubeMapMipPZLevel(int level) {
		level = Math.min(Math.min(header.dwMipMapCount, level), Math.max(level, 0));
		if(printDebug) System.out.println((level*5)-1);
		return this.bdata2.get((level*5)-1);
	}
	
	public ByteBuffer getCubeMapMipNZLevel(int level) {
		level = Math.min(Math.min(header.dwMipMapCount, level), Math.max(level, 0));
		if(printDebug) System.out.println((level*6)-1);
		return this.bdata2.get((level*6)-1);
	}
	
	public int getDXTFormat() {
		return dxtFormat;
	}
	
	public int getPitchOrLinearSize() {
		return imageSize;
	}
	
	public boolean isCubeMap() {
		return isCubeMap;
	}
}
