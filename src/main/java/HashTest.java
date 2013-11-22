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
		SourceIO io = new SourceIO(source);
		SourceScanner ss = new SourceScanner();
		SourceDb scanned = ss.quickScan(source, Arrays.asList("jpg", "nef", "cr2", "dng", "m4v", "avi", "mov"));
		ss.fillInMissingData(source, scanned);
		io.save(scanned);
	}
}
