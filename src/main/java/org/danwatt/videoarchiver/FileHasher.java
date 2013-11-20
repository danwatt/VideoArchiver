package org.danwatt.videoarchiver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

public class FileHasher {

	public static String quickHash(File f, long maxBytesToHash) throws FileNotFoundException, IOException {
		if (f.length() < maxBytesToHash || Integer.MAX_VALUE < maxBytesToHash) {
			return FileHasher.hashFile(f);
		}
		int s = (int) maxBytesToHash;
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f), s);
		byte[] bytes = new byte[s];
		try {
			if (IOUtils.read(bis, bytes) != s) {
				throw new IOException("Tried to read " + s + " bytes, only read " + IOUtils.read(bis, bytes) + " for " + f.getAbsolutePath());
			}
		} finally {
			IOUtils.closeQuietly(bis);
		}
		return Hashing.sha1().hashBytes(bytes).toString();
	}

	public static String hashFile(File file) throws IOException {
		return Files.hash(file, Hashing.sha1()).toString();
	}

}
