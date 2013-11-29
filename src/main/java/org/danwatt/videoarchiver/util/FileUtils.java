package org.danwatt.videoarchiver.util;

import java.io.File;

public class FileUtils {

	public static String relativePath(File root, File actualFile) {
		return root.toURI().relativize(actualFile.toURI()).getPath();
	}

}
