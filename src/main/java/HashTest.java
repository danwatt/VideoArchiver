import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.danwatt.videoarchiver.FileHasher;
import org.danwatt.videoarchiver.source.SourceDb;
import org.danwatt.videoarchiver.source.SourceIO;
import org.danwatt.videoarchiver.source.SourceScanner;

public class HashTest {
//	private static final int QUICK_HASH_SIZE = 256 * 1024;

	public static void main(String[] args) throws Exception {
		File source = new File(args[0]);
		SourceScanner ss = new SourceScanner();
		SourceDb scanned = ss.scan(source, Arrays.asList("jpg", "nef", "cr2", "dng", "m4v", "avi", "mov"));
		SourceIO io = new SourceIO(source);
		io.save(scanned);
//		Collection<File> files = FileUtils.listFiles(source, extensionFileFilter, FileFilterUtils.trueFileFilter());
//		System.out.println("Found " + files.size() + " files");
//		int c = 0;
//		for (File f : files) {
//			String hash = FileHasher.quickHash(f, QUICK_HASH_SIZE);
//			if ((++c) % 100 == 0) {
//				System.out.println("Done with " + c);
//			}
//		}
	}
}
