package javaNK.util.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class FileLoader
{
	public static final String HEADER_PATH = "res/";
	
	/**
	 * Verify a file's availability by trying to load it.
	 * Use this method if you don't want to check whether the "load()" method returns null or not.
	 * @param path - Full login path of the file
	 * @return true if the file is able to be loaded of false if not.
	 */
	public static boolean test(String path) {
		path = HEADER_PATH + path;
		InputStream inputStream;
		
		try	{
			File file = new File(path);
			inputStream = new FileInputStream(file);
			inputStream.close();
			return true;
		}
		catch (IOException e) { return false;	}
	}
	
	/**
	 * Print an error to the console.
	 * @param fileType - Type of file that throws the error (image/tune/font/etc...)
	 * @param fullPath - Full logic path of the file
	 * @param error - The thrown exception to print out
	 */
	protected static void error(String fileType, String fullPath, Exception error) {
		System.err.println("Could not find the " + fileType + " file "
						 + "'" + extractFileName(fullPath) + "' "
						 + "in '" + eraseFileNameOffPath(fullPath) + "'.");
		
		error.printStackTrace();
	}
	
	/**
	 * Trim the name of the file off its path, leaving only the directory folder.
	 * @param path - Full path of a file (logic or absolute)
	 * @return the full path without the file's name.
	 */
	private static String eraseFileNameOffPath(String path) {
		int dotIndex = path.lastIndexOf('.');
		int slashIndex = path.lastIndexOf('/');
		
		if (dotIndex == -1 && slashIndex != -1) return path;
		else if (slashIndex == -1) return "invalid file path";
		else {
			//dotIndex - 2 is the maximal place a '/' can be found
			for (int i = dotIndex - 2; i >= 0; i--)
				if (path.charAt(i) == '/')
					return path.substring(0, i + 1);
			
			return path; //formal return statement
		}
	}
	
	/**
	 * Extract the name of the file from the full path.
	 * @param path - Full path of a file (logic or absolute)
	 * @return the file's name.
	 */
	private static String extractFileName(String path) {
		int slashIndex = path.lastIndexOf('/');
		
		if (slashIndex == -1) return "[invalid file name]";
		else {
			path = path.substring(slashIndex + 1);
			return path.replace("/", "");
		}
	}
}