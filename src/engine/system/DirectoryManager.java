package engine.system;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.file.ConfigResources;
import engine.main.Application;
import engine.util.Log;

public class DirectoryManager {

	private static Map<String, List<String>> folderIndex = new HashMap<String, List<String>>();
	private static int directoryCount = 0;
	
	/**
	 * Creates an updated folder index, listing all folders from the root directory and down, based on the contents of the given resources.
	 * @param resources
	 */
	public static void buildFolderIndex(ConfigResources resources)
	{
		String[] dirNames = resources.getDirectories();
		
		Application.getForm().configureProgressBar(dirNames.length, true);
		
		Log.info("Start indexing directories.");
		long time = System.currentTimeMillis();
		
		for(int i = 0; i < dirNames.length; i++)
		{
			processConfigEntry(Paths.get(Application.ROOT_DIR + "/" + dirNames[i]), resources.getAddSubDirs(dirNames[i]));
			Application.getForm().increaseProgressBarState(true);
		}
		
		long ms = System.currentTimeMillis() - time;
		Log.info("Finished indexing %d directories in %d ms.", directoryCount, ms);
		Application.getForm().setLoadingLabelText("Finished loading "+directoryCount+" directories (took "+ms+" ms)", true);
		Application.getForm().setProgressAmount(0, true);
	}
	
	/**
	 * Use the current folder index to find the first instance of the given file name. There are a few conditions:<ul>
	 * <li>If the file name does not contain a path, only the starting directory is checked.</li>
	 * <li>If the file name has a preceding path, it will attempt to match as many folders as possible directly before the file name.</li>
	 * <li>If no folders match, it will check the starting directory before failing. If some folders match, it will find the first instance.</li></ul>
	 * @param fileName - The file name to search for. Can include a path of folders.
	 * @return The found file, or null if not found.
	 */
	public static File locate(String fileName)
	{
		return locate(fileName, null);
	}
	
	/**
	 * Use the current folder index to find the first instance of the given file name. There are a few conditions:<ul>
	 * <li>If the file name does not contain a path, only the starting directory is checked.</li>
	 * <li>If the file name has a preceding path, it will attempt to match as many folders as possible directly before the file name.</li>
	 * <li>If no folders match, it will check the starting directory before failing. If some folders match, it will find the first instance.</li>
	 * <li>The ordering is based on priority in the resources.cfg file, and alphabetically in the sub directories.</li></ul>
	 * @param fileName - The file name to search for. Can include a path of folders.
	 * @param startingDirectory - The folder to start searching in. This will only be used as a default if {@link fileName} has no matching folder path.
	 * @return The found file, or null if not found.
	 */
	public static File locate(String fileName, File startingDirectory)
	{
		if(fileName.startsWith("file://")) fileName = fileName.substring(7);
		
		if(fileName.contains("/"))
		{
			String[] pathParts = fileName.split("/");
			fileName = pathParts[pathParts.length-1];
			
			for(String folderName : pathParts)
			{
				if(folderIndex.containsKey(folderName))
				{
					for(String relativePath : folderIndex.get(folderName))
					{
						File f = new File(Application.ROOT_DIR + "/" + relativePath + "/" + fileName);
						if(f.isFile()) 
						{
							Log.info("Found file '%s'", f.getAbsolutePath());
							return f;
						}
					}
				}
			}
		}
		
		if(startingDirectory != null)
		{
			File f = new File(startingDirectory + "/" + fileName);
			if(f.isFile()) 
			{
				Log.info("Found file '%s'", f.getAbsolutePath());
				return f;
			}
			Log.error("Could not find file by name '%s', starting in '%s'", fileName, startingDirectory);
			return null;
		}
		
		Log.error("Could not find file by name '%s'", fileName);
		return null;
	}
	
	private static void processConfigEntry(Path path, boolean recursive)
	{
		try
		{
			if(recursive) Files.walkFileTree(path, new SimpleFileVisitor<Path>() 
			{
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException 
				{
					addToIndex(dir); return FileVisitResult.CONTINUE;
					//else return FileVisitResult.SKIP_SUBTREE;
				}
				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
//					if(!file.toFile().isDirectory()) Log.warn("Directory not found '%s'", file);
					return FileVisitResult.CONTINUE;
				}
			});
			else addToIndex(path);
		} 
		catch (IOException e) 
		{
			Log.error("A file error occurred in DirectoryManager!");
			e.printStackTrace();
		}
	}
	
	private static boolean addToIndex(Path dir)
	{
		String folderName = dir.getFileName().toString();
		String folderPath = dir.toString().replace("\\", "/").substring(Application.ROOT_DIR.length());
		
		directoryCount++;
		
		Application.getForm().setLoadingLabelText("Loading: "+folderPath, true);
		
		if(!folderIndex.containsKey(folderName))
		{
			List<String> list = new ArrayList<String>();
			list.add(folderPath);
			folderIndex.put(folderName, list);
//			Log.info("Adding key with relative path. '%s' : '%s'", folderName, folderPath);
			return true;
		}
		else
		{
			List<String> list = folderIndex.get(folderName);
			list.add(folderPath);
//			Log.info("Already contains key, adding path. '%s' : '%s'", folderName, folderPath);
			return false;
		}
	}
}
