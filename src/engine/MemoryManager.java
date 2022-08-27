package engine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import engine.util.Log;

public class MemoryManager
{
	private Map<File, Map<Long, Object>> loadedFiles = new HashMap<File, Map<Long, Object>>();
	
	public MemoryManager()
	{
		Log.info("Creating memory manager...");
	}
	
	/**
	 * Check whether the given file is already loaded into this memory manager.
	 * @param file - The file to check
	 * @return true if loaded, false if not.
	 */
	public boolean isFileLoaded(File file)
	{
		if(!loadedFiles.containsKey(file)) return false;
		return loadedFiles.get(file).containsKey(file.lastModified());
	}
	
	/**
	 * Gets the already loaded and processed contents of a given file.
	 * @param file - The file to load
	 * @return the loaded file, or null if not loaded.
	 */
	public Object getLoadedFile(File file)
	{
		if(!loadedFiles.containsKey(file)) return null;
		return loadedFiles.get(file).get(file.lastModified());
	}
	
	/**
	 * Add a new file to the memory manager, linking the file on disk to the object of the loaded contents.
	 * @param file - The file on disk to add.
	 * @param object - The loaded representation of the file.
	 */
	public void addFile(File file, Object object)
	{
		if(loadedFiles.containsKey(file) && loadedFiles.get(file).containsKey(file.lastModified())) return;
		Map<Long, Object> map = new HashMap<Long, Object>();
		map.put(file.lastModified(), object);
		loadedFiles.put(file, map);
		Log.info("Added file to memory manager: %s", file);
	}
	
	/**
	 * Clear all loaded files from this memory manager. Presents as empty.
	 */
	public void cleanUp()
	{
		for(Map<Long, Object> map : loadedFiles.values())
		{
			map.clear();
		}
		loadedFiles.clear();
	}
	
}
