import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.danwatt.videoarchiver.FileHasher;

public class HashTest {
	private static final int QUICK_HASH_SIZE = 256 * 1024;

	public static void main(String[] args) throws Exception {
		File source = new File(args[0]);
		SuffixFileFilter extensionFileFilter = new SuffixFileFilter(Arrays.asList("jpg", "nef", "cr2", "dng", "m4v", "avi", "mov"), IOCase.INSENSITIVE);
		Collection<File> files = FileUtils.listFiles(source, extensionFileFilter, FileFilterUtils.trueFileFilter());
		System.out.println("Found " + files.size() + " files");
		int c = 0;
		for (File f : files) {
			String hash = FileHasher.quickHash(f, QUICK_HASH_SIZE);

			System.out.println(hash);
			if ((++c) % 100 == 0) {
				System.out.println("Done with " + c);
			}
		}
	}
}
