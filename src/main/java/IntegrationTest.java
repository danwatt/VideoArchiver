import java.io.File;
import java.util.Arrays;

import org.danwatt.videoarchiver.source.SourceDb;
import org.danwatt.videoarchiver.source.SourceIO;
import org.danwatt.videoarchiver.source.SourceScanner;

public class IntegrationTest {
	// private static final int QUICK_HASH_SIZE = 256 * 1024;

	public static void main(String[] args) throws Exception {
		File source = new File(args[0]);
		SourceIO io = new SourceIO(source);
		SourceDb existingdb = io.load();
		System.out.println("Loaded " + existingdb.getItems().size() + " files");
		SourceScanner ss = new SourceScanner();
		SourceDb quickScanned = ss.quickScan(source, Arrays.asList("jpg", "nef", "cr2", "dng", "m4v", "avi", "mov"));
		System.out.println("Identified " + quickScanned.getItems().size() + " by quick scan");
		existingdb.merge(quickScanned);
		ss.fillInMissingData(source, existingdb);
		System.out.println("Process complete");
		io.save(existingdb);
	}
}
