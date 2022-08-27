package engine.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import engine.util.XMLParser;
import engine.util.XMLParser.Element;

public class ConfigMaterials {

	private List<MaterialEntry> entries = new ArrayList<MaterialEntry>();
	
	public ConfigMaterials(String path) {
		this.readFile(new File(path));
	}
	
	public ConfigMaterials(File file) {
		this.readFile(file);
	}
	
	private boolean readFile(File file) {
		XMLParser parser = new XMLParser();
//		parser.printDebug = true;
		
		if(parser.read(file) == false) {
			System.err.println("Failed to load materials config: " + file.getAbsolutePath());
			return false;
		}
		
		Element materials = parser.getElement("Materials");
		
		for(Element material : materials.getElementsOfName("Material")) {
			String entryName = material.getAttribute("Name");
			MaterialEntry m = new MaterialEntry(entryName);
			entries.add(m);
//			System.out.println(entryName);
		}
		
		return true;
	}
	
	public String[] getMaterialNames() {
		String[] s = new String[entries.size()];
		for(int i = 0; i < s.length; i++) s[i] = entries.get(i).getName();
		Arrays.sort(s);
		return s;
	}
	
}

class MaterialEntry {
	
	private String name;
	
	public MaterialEntry(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
}
