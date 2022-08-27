package engine;

import java.util.HashMap;
import java.util.Map;

import engine.util.Log;

public class StateMachine {

	//==============================================================================
	// Private static fields
	//==============================================================================
	
	private static Map<String, Boolean> booleans = new HashMap<String, Boolean>();
	private static Map<String, Integer> integers = new HashMap<String, Integer>();
	
	//==============================================================================
	// Public static fields
	//==============================================================================

	public static boolean enableCulling = false;
	public static boolean enableDepthTest = true;
	
	public static boolean drawModelWireframe = false;
	public static boolean drawModelFaces = true;
	public static boolean drawEntityIcons = true;
	
	public static boolean renderNormalsDebug = false;
	
	/** The render mode tells which method to use to display models.<ul><li>0 = default</li><li>1 = tangent debug</li></ul> */
	public static int renderMode = 0;
	
	//==============================================================================
	// Public static methods
	//==============================================================================

	/**
	 * Set a globally accessible boolean variable.
	 * @param name - the name of the variable
	 * @param value - the value of the variable
	 */
	public static void setBoolean(String name, boolean value)
	{
		Log.info("%s = %b", name, value);
		booleans.put(name, value);
	}
	
	/**
	 * Get a global boolean variable previously set using <code>setBoolean</code>
	 * @param name - the name of the variable
	 * @return
	 */
	public static boolean getBoolean(String name)
	{
		if(booleans.containsKey(name)) return booleans.get(name);
		return false;
	}
	
	public static void setInteger(String name, int value)
	{
		Log.info("%s = %d", name, value);
		integers.put(name, value);
	}
	
	public static int getInteger(String name)
	{
		if(integers.containsKey(name)) return integers.get(name);
		return 0;
	}
	
	public static void addInteger(String name, int change)
	{
		int newValue = change;
		if(integers.containsKey(name)) newValue += integers.get(name);
		integers.put(name, newValue);
	}
}
