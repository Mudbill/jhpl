package engine.forms.editor.material;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

public enum EWrapMode {
	
	REPEAT("Repeat", GL11.GL_REPEAT),
	CLAMP("Clamp", GL11.GL_CLAMP),
	CLAMP_TO_EDGE("ClampToEdge", GL12.GL_CLAMP_TO_EDGE),
	CLAMP_TO_BORDER("ClampToBorder", GL13.GL_CLAMP_TO_BORDER);
	
	private String s;
	private int glMode;
	
	private EWrapMode(String s, int glMode) {
		this.s = s;
		this.glMode = glMode;
	}
	
	public String getText() {
		return s;
	}
	
	public int getWrapMode() {
		return glMode;
	}
	
	public static String[] getValues() {
		String[] s = new String[values().length];
		for(int i = 0; i < s.length; i++)
			s[i] = values()[i].getText();
		return s;
	}
}
