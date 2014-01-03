package edu.purdue.munin.util;

import java.io.File;

public class FileLib {
	public static String getRelativePath(File file) { 
		String filename = file.getAbsolutePath();
		File base = new File(".");
		if (filename.length() < base.getAbsolutePath().length() - 1) return "./";
		return filename.substring(base.getAbsolutePath().length() - 1, filename.length());
	}
}
