package engine.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.util.Log;

import engine.util.XMLParser;
import engine.util.XMLParser.Element;

public class ConfigResources {

	private Map<String, Boolean> entries = new HashMap<String, Boolean>();
	
	/**
	 * Creates a new instance of this class, and loads the file at the given path.
	 * @param path - The file location for the <code>resources.cfg</code> to load.
	 */
	public ConfigResources(String path) {
		this(new File(path));
	}
	
	/**
	 * Creates a new instance of this class, and loads the file at the given file.
	 * @param file - The file for the <code>resources.cfg</code> to load.
	 */
	public ConfigResources(File file) {
		this.readFile(file);
	}
	
	private boolean readFile(File file) {
		XMLParser parser = new XMLParser();
//		parser.printDebug = true;
		
		if(!file.isFile() || parser.read(file) == false) {
			System.err.println("Failed to load resources config: " + file.getAbsolutePath());
			return false;
		}
		
		Element resources = parser.getElement("Resources");
		if(resources == null) {
			Log.error("Resources file is incomplete!");
			return false;
		}
		
		for(Element directory : resources.getElementsOfName("Directory")) {
			String entryName = directory.getAttribute("Path");
			boolean addSubDirs = Boolean.parseBoolean(directory.getAttribute("AddSubDirs"));
			
			if(!entries.containsKey(entryName)) entries.put(entryName, addSubDirs);
		}
		
		return true;
	}
	
	/**
	 * Get an array listing of all directories stored in this resource config.
	 * @return 
	 */
	public String[] getDirectories() {
		String[] s1 = new String[entries.size()];
		int idx = 0;
		for(String dir : entries.keySet()) s1[idx++] = dir;
		return s1;
	}
	
	/**
	 * Get the AddSubDirs-state of the given path; whether or not it's meant to be recursive.
	 * @param path - the path matching in the config to check
	 * @return
	 */
	public boolean getAddSubDirs(String path) {
		if(entries.containsKey(path)) return entries.get(path);
		return false;
	}
	
}