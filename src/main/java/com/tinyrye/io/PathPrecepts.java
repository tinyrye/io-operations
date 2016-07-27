package com.tinyrye.io;

import java.io.File;
import java.io.IOException;

public class PathPrecepts
{
	/**
	 * Helper method for implemention: ensure file and its parent
	 * directories exist. Make necessary nesting directories in
	 * addition to creating an empty file by the requested path.
	 */
	public static boolean ensureFile(File file) throws IOException
	{
		if (! file.exists())
		{
			if (ensureDirectories(file.getParentFile())) {
				return file.createNewFile();
			}
			else {
				return false;
			}
		}
		else return true;
	}

	/**
	 * Helper method for implemention: ensure directory exists; if parent
	 * directories do not exist however, then this will not succeed: this
	 * method will not create nesting directories.
	 */
	public static boolean ensureDirectory(File directory) throws IOException {
		if (! directory.exists()) return directory.mkdir();
		else return true;
	}

	/**
	 * Helper method for implemention: ensure directory and its parent
	 * directories exist. Make necessary nesting directories in addition
	 * to creating the leaf directory by the requested path.
	 */
	public static boolean ensureDirectories(File directory) throws IOException {
		if (! directory.exists()) return directory.mkdirs();
		else return true;
	}
}