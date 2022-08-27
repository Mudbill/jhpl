package engine.main;

import java.io.File;

import engine.EnginePreview;
import engine.EngineTextureUnit;
import engine.file.ConfigResources;
import engine.forms.Form;
import engine.forms.FormDefault;
import engine.forms.editor.material.ShellMaterialEditor;
import engine.materials.ETextureUnitType;
import engine.system.DirectoryManager;
import engine.util.Log;

public class Application {
	
	public static final String ROOT_DIR = "/home/mudbill/.local/share/Steam/steamapps/common/Amnesia The Dark Descent";
	public static final String APP_NAME = "JHPL Engine";
	public static final String APP_VERSION  = "a1.0.0";
	public static final long START_TIME = System.currentTimeMillis();

	private static EnginePreview engine;
	private static EngineTextureUnit engineTextureUnit;
	private static ConfigResources resources;
	private static Form form;
	
	private static enum FormType {
		DEFAULT, MATERIAL_EDITOR
	}

	/**
	 * Initialize the application.
	 */
	public static void init()
	{
//		Log.setLoggingToFile("log.txt");
		Log.info("Starting %s version %s", APP_NAME, APP_VERSION);
		Log.info("--------------------------------------------------------------------------------");
				
		engine = new EnginePreview();
		engineTextureUnit = new EngineTextureUnit();

		Log.info("Initializing form...");
		form = initForm(FormType.MATERIAL_EDITOR);

//		engineInstance = new EngineMain("Main");
//		engineInstance.init();
//		
//		mainLoop.postInit(engineInstance);
		
		createFolderIndex();
		Log.info("Entering GUI...");
		form.open(); // Running.
		
		shutdown();
	}
	
	public static EnginePreview getEngine() {
		return engine;
	}
	
	public static EngineTextureUnit getTextureUnitEngine() {
		return engineTextureUnit;
	}
	
	public static void setActiveModelTextureUnit(ETextureUnitType type, String filePath) {
		engine.getModel().setTextureMap(type, filePath);
	}
	
	public static Form getForm() {
		return form;
	}
		
	private static Form initForm(FormType formType)
	{
		Form form = null;
		
		switch(formType)
		{
		case DEFAULT:
			form = new FormDefault(); 
			break;
		case MATERIAL_EDITOR:
			form = new ShellMaterialEditor();
			break;
		}
		
		form.init();
		
		Log.info("Startup in %d ms.", System.currentTimeMillis() - START_TIME);
		return form;
	}
	
	private static void createFolderIndex()
	{
		new Thread("FolderIndexingThread")
		{
			@Override
			public void run()
			{
				resources = new ConfigResources(ROOT_DIR + File.separator + "resources.cfg");
				DirectoryManager.buildFolderIndex(resources);
				form.setOpenOptionActive(true, true);
			}
		}.start();
	}
	
	private static void shutdown()
	{
		Log.info("Shutting down.");
	}
}
