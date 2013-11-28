import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.danwatt.videoarchiver.source.SourceDb;

public class IntegrationTest {
	// private static final int QUICK_HASH_SIZE = 256 * 1024;

	public static void main(String[] args) throws Exception {
		setupLogging();
		File source = new File(args[0]);
		SourceDb existingdb = new SourceDb(source);
		existingdb.load();
		System.out.println("Loaded " + existingdb.getItems().size() + " files");
		existingdb.scan(Arrays.asList("jpg", "nef", "cr2", "dng", "m4v", "avi", "mov"));
		System.out.println("Process complete");
		existingdb.save();
	}

	public static void setupLogging() {
		Logger globalLogger = Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
		StreamHandler sh = new StreamHandler(System.out, new SimpleFormatter());
		globalLogger.setLevel(Level.INFO);
		globalLogger.addHandler(sh);
	}
}
