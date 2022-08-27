package engine.materials;

public enum EMaterialType {

	DECAL("Decal"),
	SOLIDDIFFUSE("SolidDiffuse"),
	WATER("Water"),
	TRANSLUCENT("Translucent"),
	INVALID("null");
	
	private String string;
	
	private EMaterialType(String string) {
		this.string = string;
	}
	
	public String getString() {
		return this.string;
	}
	
	public static EMaterialType getTypeFromString(String string) {
		for(EMaterialType type : values()) {
			if(type.getString().equalsIgnoreCase(string)) return type;
		}
		return INVALID;
	}
	
}
