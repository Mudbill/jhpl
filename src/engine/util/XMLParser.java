package engine.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class XMLParser {
	
	/** Set to true to enable debug messages */
	public boolean printDebug = false;
	
	/** If reading a file, this contains the raw content of that file */
	private String strContent;
	
	/** If file contains a declaration, this is it */
	private Element declaration;
	
	/** A list of all the elements located at the top of the document */
	private List<Element> elements;
	
	private String indent = "";
	private int mul = 0;

//	public static void main(String[] args) 
//	{
//		String s = "D:/temp/example.xml";
//		XMLParser xml = new XMLParser();
//		xml.printDebug = true;
//		xml.read(new File(s));
//		System.out.println(xml.getDeclaration());
////		System.out.println(xmlDoc.getElementByPath("LANGUAGE/RESOURCES/Directory").getAttribute("Path"));
////		System.out.println(xml.getElementByPath("Material/TextureUnits/NMap").getAttribute("AnimMode").getValue());
//		xml.write(new File("D:/temp/testing.xml"));
//	}
	
	/**
	 * Create a new instance of an XML parser and immediately read the input file.
	 * @param file - An XML formatted file.
	 */
	public XMLParser(String file)
	{
		this.elements = new ArrayList<Element>();
		this.read(new File(file));
	}
	
	/**
	 * Create a new instance of an XML parser and immediately read the input file.
	 * @param file - An XML formatted file.
	 */
	public XMLParser(File file)
	{
		this.elements = new ArrayList<Element>();
		this.read(file);
	}
	
	/**
	 * Create a new instance of an XML parser, ready to input elements into.
	 */
	public XMLParser() 
	{
		this.elements = new ArrayList<Element>();
	}
	
	/**
	 * Reads the XML file supplied and parses it into a hierarchy of Elements.
	 * @return true if successful
	 */
	public boolean read(File file) 
	{
		if(file == null)
		{
			System.err.println("XML PARSER: Null file given!");
			return false;
		}
		
		if(!file.isFile())
		{
			System.err.printf("XML PARSER: File not found: '%s'%n", file.getPath());
			return false;
		}
		
		try {
			Scanner scanner = new Scanner(file);
			StringBuilder sb = new StringBuilder();
			
			while(scanner.hasNext()) sb.append(scanner.nextLine() + System.lineSeparator());
			
			scanner.close();
			strContent = sb.toString().trim();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		this.parseXML(strContent);
		return true;
	}
	
	private void parseXML(String content) 
	{
		Element parent = null;
		
		int start = 0;
		int end = 0;
		int level = 0;
		String line;
		String name;
		String delimiter;
		String debugIndent = "";
		
		while (end < content.length()) 
		{
			start = content.indexOf("<", end);
			end = content.indexOf(">", start) + 1;
			line = content.substring(start, end);
			
			if(isDeclaration(line)) {
				if(printDebug) System.out.println("Declaration found: " + line);
				Element e = new Element(parent, "DECLARATION");
				e.setAttributes(extractAttributes(line));
				setDeclaration(e);
				continue;
			}
			
			for(int i = 0; i < level; i++) debugIndent += "\t"; 
			
			if(isOpenElement(line)) 
			{
				delimiter = line.contains(" ") ? " " : ">";
				name = line.substring(1, line.indexOf(delimiter));

				Element e = new Element(parent, name);
				Map<String, String> attrs = extractAttributes(line);
				if(attrs != null && attrs.size() > 0) 
				{
					e.setAttributes(attrs);
					if(printDebug) 
					{
						System.out.println("ATTRS:\t\t"+debugIndent+"Adding "+attrs.size()+" attributes to '"+name+"'");
						for(String a : attrs.keySet()) System.out.println("ATTRS:\t\t\t"+debugIndent+"Attribute '"+a + "' has value '"+attrs.get(a)+"'");
					}
				}
				
				if(parent == null) 
				{
					
				}
				
				if(printDebug) {
					if(parent != null)
						System.out.println("OPEN:\t\t"+debugIndent+"Adding '"+name+"' to '"+parent.getName()+"'");
					else 
						System.out.println("OPEN:\t\t"+debugIndent+"Adding '"+name+"' to document.");
				}
				parent = e;

				if(level == 0) addElement(e);
				level++;
			} 
			else if(isInlineElement(line)) 
			{
				if(line.contains(" "))
				{
					name = line.substring(1, line.indexOf(" "));
				}
				else
				{
					name = line.substring(1, line.indexOf("/"));
				}
								
				Element e = new Element(parent, name);
				Map<String, String> attrs = extractAttributes(line);
				if(attrs != null && attrs.size() > 0) 
				{
					e.setAttributes(attrs);
					if(printDebug) 
					{
						System.out.println("ATTRS:\t\t"+debugIndent+"Adding "+attrs.size()+" attributes to '"+name+"'");
						for(String a : attrs.keySet()) System.out.println("ATTRS:\t\t\t"+debugIndent+"Attribute '"+a + "' has value '"+attrs.get(a)+"'");
					}
				}
				if(printDebug) System.out.println("INLINE:\t\t"+debugIndent+"Adding '"+name+"' to '"+parent.getName()+"'");
			}
			else if(isClosingElement(line)) 
			{
				name = line.substring(2, line.indexOf(">"));
				
				if(parent.getElements().size() <= 0) 
				{					
					line = content.substring(content.lastIndexOf(">", start)+1, content.lastIndexOf("<", start));
					if(printDebug) System.out.println("INFO:\t\t"+debugIndent+"'"+name+"' has no children. Text set to '" + line + "'");
					parent.setValue(this.getRealCharsFrom(line));
				}
				
				parent = parent.getParent();
				level--;
				
				if(printDebug) System.out.println("CLOSE:\t\t"+debugIndent+"Closing '"+name+"'");
			}
			
			debugIndent = "";
		}
	}
	
	private Map<String, String> extractAttributes(String content)
	{		
		Map<String, String> list = new HashMap<String, String>();
		List<String> nameList = new ArrayList<String>();
		int iStart = 0, iEnd = 0;
		String sub;
		String contentProcessed = content.replace(System.lineSeparator(), "").replaceAll("\t* *=", "="); //Removes spaces and tabs between the name and =
		
		while(iEnd <= contentProcessed.lastIndexOf("=")) {
			iEnd = contentProcessed.indexOf("=", iEnd + 1);
			iStart = contentProcessed.lastIndexOf(" ", iEnd)+1;
			
			if(iEnd < iStart) break;
			
			sub = contentProcessed.substring(iStart, iEnd).trim();
			
			nameList.add(sub);
		}
		
		iStart = 0;
		iEnd = 0;
		int loop = 0;
		
		while(iEnd < content.lastIndexOf("\"")) {
			iStart = content.indexOf("\"", iEnd);
			iEnd = content.indexOf("\"", iStart+1) + 1;
			sub = content.substring(iStart + 1, iEnd - 1);
			String n = nameList.get(loop++);
			String v = this.getRealCharsFrom(sub);
			list.put(n, v);
		}
		
		return list;
	}
	
	/**
	 * Writes the current XML configuration to a file.
	 * @param file
	 */
	public void write(File file)
	{
		mul = 0;
		indent = "";
		StringBuilder sb = new StringBuilder();
		
//		int child = 0;
		if(hasDeclaration()) {
			sb.append(getDeclarationString() + System.lineSeparator());
			if(printDebug) System.out.println("Printing declaration");
			//child++;
		}
		
		for(Element e : elements)
		{
			if(printDebug) System.out.println("Printing element: "+e.getName());
			if(printDebug) System.out.println(sb.toString());
			sb.append(constructXML(new StringBuilder(), e));
			if(printDebug) System.out.println(sb.toString());
		}
		
		try {
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.println(sb.toString().trim());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(printDebug) System.out.println("Writing to file: "+file.getAbsolutePath());
	}
	
	private String constructXML(StringBuilder builder, Element element)
	{
		if(!element.hasValue()) builder.append(indent + wrapOpenElement(element) + System.lineSeparator());
		else
		{
			if(printDebug) System.out.println("'"+element.printAncestry()+"' has no attributes or children.");
			builder.append(indent + wrapOpenElement(element) + this.getReplacementCharsFrom(element.getValue()) + wrapClosingElement(element) + System.lineSeparator());
		}
		
		for(Element e : element.getElements())
		{
//			if(printDebug) System.out.println("'"+e.getAncestry()+"' has "+e.getElements().size()+" children.");
			indent(1);
			this.constructXML(builder, e);
			indent(-1);
		}
		
		if(element.hasElements()) builder.append(indent + wrapClosingElement(element) + System.lineSeparator());
//		System.out.println("\n\n\n" + builder.toString());
		return builder.toString();
	}
	
	/**
	 * Changes the level of indentation to print to the file.
	 * @param change
	 */
	private void indent(int change)
	{
		mul += change;
		indent = "";
		for(int i = 0; i < mul; i++) {
			indent += "\t";
		}
	}
	
	private String wrapClosingElement(Element e)
	{
		if(printDebug) System.out.println("Closing '"+e.printAncestry()+"'");
		return "</"+e.getName()+">";
	}
	
	private String wrapOpenElement(Element e)
	{
		String s = "<"+e.getName();
		for(String a : e.getAttributes().keySet()) s += " " + a + "=\"" + this.getReplacementCharsFrom(e.getAttributes().get(a)) + "\"";
		
		if(!e.hasElements() && !e.hasValue() && e.hasAttributes()) {
			if(printDebug) System.out.println("'"+e.printAncestry()+"' is an inline element with no children.");
			s += " /";
		} else {
			if(printDebug) System.out.println("Opening '"+e.printAncestry()+"' as an opening element with "+e.getElements().size()+" children.");
			if(!e.hasElements() && !e.hasValue()) {
				s += "></"+e.getName();
				if(printDebug) System.out.println("Closing '"+e.getName()+"' immediately.");
			}
		}
		s += ">";
		
		return s;
	}
	
    private String getReplacementCharsFrom(String text) {
        return text.replace("&", "&amp;")
                .replace("'", "&apos;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
    
    private String getRealCharsFrom(String text) {
        return text.replace("&amp;", "&")
                .replace("&apos;", "'")
                .replace("&quot;", "\"")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }
	
	private boolean isDeclaration(String node) {
		return (node.startsWith("<?")
				&& node.endsWith("?>"));
	}
	
	private boolean isOpenElement(String node) {
//		String s = node.replaceAll("\".*\"", "\"\"");
//		char c = node.charAt(s.length()-2);
//		System.out.println(s);
		return (node.startsWith("<") 
				&& node.endsWith(">") 
				&& node.charAt(1)!='/'
				&& node.charAt(node.length()-2)!='/'
//				&& node.matches("\\ *?>")
//				&& node.endsWith("/>")
//				&& s.charAt(s.length()-2)!=c
//				&& !node.matches(".*(/[ ?]>)$")
//				&& !node.contains("/")
				&& !node.startsWith("<?"));
	}
	
	private boolean isClosingElement(String node) {
		return (node.startsWith("</")
				&& node.endsWith(">"));
	}
	
	private boolean isInlineElement(String node) {
		return (node.startsWith("<")
				&& node.endsWith("/>"));
	}
		
	public void setDeclaration(Element declaration) {this.declaration = declaration;}
	public Element getDeclaration() {return this.declaration;}
	public List<Element> getElements() {return this.elements;}
	public Element getElement(String name) throws NullPointerException
	{
		for(Element e : elements) if(e.getName().equals(name)) return e;
		if(printDebug) System.err.println("WARN: Document has no child with name: '"+name+"'");
		return null;
	}
	/**
	 * Returns the Element located at the path given, starting from the root.
	 * @param path - a forward-slash separated path of Element names.
	 * @return the Element at the final destination.
	 */
	public Element getElementByPath(String path) 
	{
		String[] elementNames = path.split("/");
		Element e = getElement(elementNames[0]);
		for(int i = 1; i < elementNames.length; i++)
		{
			e = e.getElement(elementNames[i]);
			if(elementNames[elementNames.length-1].equals(e.getName())) return e;
		}
		if(printDebug) System.err.println("Path not found");
		return null;
	}
	
	public String getDeclarationString() 
	{
		if(hasDeclaration()) {
			String output = "<?xml ";
			for(String a : declaration.getAttributes().keySet()) {
				output += a + "=\"" + declaration.getAttributes().get(a) + "\" ";
			}
			output += "?>";
			return output;
		}
		if(printDebug) System.err.println("Document has no declaration!");
		return "";
	}
	
	public boolean hasDeclaration() {return declaration!=null;};
	public boolean hasElement(String name) {return getElement(name)!=null;}
	public Element addElement(Element e) {
		e.setIndex(elements.size());
		elements.add(e);
		return e;
	}
	public Element addElement(String name) {
		Element e = new Element(null, name);
		e.setIndex(elements.size());
		elements.add(e);
		return e;
	}
	
	/**
	 * Removes the Element of the given name, as well as any of its children and attributes.
	 * If multiple children have the same Element name, the first one is removed. To avoid ambiguity, use .getElementByAttribValue and .remove instead.
	 * @param child - the name of the child.
	 */
	public void removeElement(String child)
	{
		Element e;
		for(int i = 0; i < elements.size(); i++) {
			e = elements.get(i);
			if(e.getName().equals(child)) {
				if(printDebug) System.out.println("Removing child '"+child+"'");
				elements.remove(i);
			}
		}
	}

	//--------------------------------------------------------------
	public class Element
	{
		/** Name of this Element */
		private String name;
		
		/** String value that this Element contains, if any */
		private String value = "";
		
		/** Index of this value in the parent's list */
		private int index;
		
		/** The parent of this Element */
		private Element parent;
		
		/** A list of Elements that reside inside this Element */
		private List<Element> elements;
		
		/** A list of Attributes that this Element has in its opening tag */
		private Map<String, String> attributes;
		
		/**
		 * Create a new Element, specifying the parent Element and name of this Element.
		 * @param parent - if null, this Element is orphaned unless contained in another list.
		 * @param name - The name of this Element
		 */
		public Element(Element parent, String name)
		{
			this.parent = parent;
			this.name = name;
			this.elements = new ArrayList<Element>();
			this.attributes = new HashMap<String, String>();
			if(parent != null)
			{				
				this.index = parent.getElements().size();
				parent.addElement(this);
			} 
			else 
			{
				this.index = 0;
			}
		}
		
		public String 			getName() 							{return this.name;}
		public List<Element> 	getElements() 						{return this.elements;}
		public Map<String, String> 	getAttributes()					{return this.attributes;}
		public Element 			getParent() 						{return this.parent;}
		public String 			getValue() 							{return this.value;}
		public int 				getIndex() 							{return this.index;}

		public void 			setName(String name) 				{this.name = name;}
		public void 			setValue(String text) 				{this.value = text;}
		public void 			setAttributes(Map<String, String> list){this.attributes = list;};
		public void 			setElements(List<Element> list) 	{this.elements = list;};
		public void 			setIndex(int index) 				{this.index = index;}
		
		public boolean 			hasValue() 							{return this.value == null || this.value.isEmpty() ? false : true;}
		public boolean 			hasElements() 						{return this.elements != null && this.elements.size() > 0 ? true : false;}
		public boolean 			hasAttributes() 					{return this.attributes != null && !this.attributes.isEmpty() ? true : false;}
		public boolean 			hasElement(String name) 			{for(Element e : elements) if(e.getName().equals(name)) return true; return false;}
		public boolean 			hasAttribute(String name) 			{for(String a : attributes.keySet()) if(a.equals(name)) return true; return false;}
		
		public void 			addAttribute(String name, String value) {attributes.put(name, value);}
		public void 			addElement(Element child) 
		{
			child.setIndex(elements.size());
			elements.add(child);
		}
		public Element 			addElement(String name) 
		{
			Element child = new Element(parent, name);
			child.setIndex(elements.size());
			elements.add(child);
			return child;
		}
		
		
		/**
		 * Prints out a human-readable string displaying the family tree of this Element. Often used for debugging.
		 * @return
		 */
		public String printAncestry() 
		{
			StringBuilder sb = new StringBuilder();
			sb.append(name);
			Element e = parent;
			while(e != null) {
				sb.insert(0, e.getName() + " > ");
				e = e.getParent();
			}
			return sb.toString();
		}
		
		/**
		 * Get the first Element with the given name that resides under this Element.
		 * @param name - Name of the Element to fetch.
		 * @return The Element
		 * @throws NullPointerException if no Element of given name is found.
		 */
		public Element getElement(String name) throws NullPointerException
		{
			for(Element e : elements) if(e.getName().equals(name)) return e;
			if(printDebug) System.err.println("WARN: Element '"+this.name+"' has no child with name: '"+name+"'");
			return null;
		}
		
		/**
		 * Returns the Element containing the given value within any of its attributes. Useful for getting a specific Element where Elements have the same name.
		 * @param attr - The Attribute value to search for
		 * @return The Element at this position
		 * @throws NullPointerException if no Element with Attribute value of attr found.
		 */
		public Element getElementByAttribValue(String attr)
		{
			for(Element e : elements) {
				if(e.getAttributes().containsKey(attr)) return e;
			}
			if(printDebug) System.err.println("WARN: Element '"+this.name+"' has no child with attribute value of '"+attr+"'");
			return null;
		}
		
		/**
		 * Returns the child Element of name elementName that has an attribute with the value of attribVal.
		 * @param elementName
		 * @param attribVal
		 * @return
		 * @throws NullPointerException
		 */
		public Element getElementByAttribValue(String elementName, String attribVal)
		{
			for(Element e : elements) {
				if(!e.getName().equals(elementName)) continue;
				if(e.getAttributes().containsKey(attribVal)) return e;
			}
			if(printDebug) System.err.println("WARN: Element '"+this.name+"' has no child '"+elementName+"' with attribute value of '"+attribVal+"'");
			return null;
		}
		
		/**
		 * Returns the child Element named elementName that also has an attribute of attribName with the value of attribVal.
		 * @param elementName
		 * @param attribName
		 * @param attribVal
		 * @return
		 * @throws NullPointerException
		 */
		public Element getElementByAttribValue(String elementName, String attribName, String attribVal)
		{
			for(Element e : elements) {
				if(!e.getName().equals(elementName)) continue;
				for(String a : e.getAttributes().keySet()) {
					if(!a.equals(attribName)) continue;
					if(printDebug) System.out.println("Checking: " + a + " == " + attribVal);
					if(a.equals(attribVal)) return e;
				}
			}
			if(printDebug) System.err.println("WARN: Element '"+this.name+"' has no child '"+elementName+"' with attribute name '"+attribName+"' and value of '"+attribVal+"'");
			return null;
		}
		
		/**
		 * Returns a list of only the children whose name matches the specified String.
		 * @param name
		 * @return
		 */
		public List<Element> getElementsOfName(String name) {
			List<Element> list = new ArrayList<Element>();
			for(Element e : elements) {
				if(e.getName().equals(name)) list.add(e);
			}
			if(list.size() == 0 && printDebug) System.err.println("Element '"+getName()+"' has no children with name '"+name+"'");
			return list;
		}
		
		public String getAttribute(String name)
		{
			for(String a : attributes.keySet()) if(a.equals(name)) return attributes.get(a);
			if(printDebug) System.err.println("WARN: Element '"+this.name+"' has no attribute with name: '"+name+"'");
			return "";
		}
		
		@Override
		public String toString() 
		{
			String output = name + "(";
			for(String a : attributes.keySet()) output += a+"="+attributes.get(a)+",";
			if(attributes.size() > 0) output = output.substring(0, output.length()-1);
			output += "){";
			for(Element e : elements) output += e.getName()+",";
			if(elements.size() > 0) output = output.substring(0, output.length()-1);
			output += "}";
			return output;
		}
		
		/**
		 * Removes this Element from the parent.
		 */
		public void remove()
		{
			if(parent == null) {
				if(printDebug) System.err.println("Can't remove element '"+name+"'");
				return;
			}
			if(printDebug) System.out.println("Removing index "+index+" AKA '"+name+"' from parent '"+parent.getName()+"'");
			parent.elements.remove(index);
		}
		
		public void removeElement(int index) {elements.remove(index);}
		
		/**
		 * Removes the Element of the given name, as well as any of its children and attributes.
		 * If multiple children have the same Element name, the first one is removed. To avoid ambiguity, use .getElementByAttribValue and .remove instead.
		 * @param child - the name of the child.
		 */
		public void removeElement(String child)
		{
			Element e;
			for(int i = 0; i < elements.size(); i++) {
				e = elements.get(i);
				if(e.getName().equals(child)) {
					if(printDebug) System.out.println("Removing child '"+child+"'");
					elements.remove(i);
				}
			}
		}
		
		/**
		 * Removes the Attribute of the given name.
		 * @param attr
		 */
		public void removeAttribute(String attr)
		{
			if(printDebug) System.out.println("Removing attribute '"+attr+"'");
			attributes.remove(attr);
		}
	}
}
