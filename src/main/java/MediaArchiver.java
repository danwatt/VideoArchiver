import java.io.File;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.danwatt.videoarchiver.encoder.CombinedEncoder;
import org.danwatt.videoarchiver.source.ExifSummary;
import org.danwatt.videoarchiver.source.SourceDb;

public class MediaArchiver {
	private static final Logger logger = Logger.getLogger(MediaArchiver.class.getName());

	public static void main(String[] args) throws Exception {
		setupLogging();
		CommandLineParser parser = new BasicParser();
		Options options = new Options();
		CommandLine cmd = parser.parse(options, args);
		SourceDb db = new SourceDb(new File(cmd.getArgs()[1]));
		db.load();
		String command = cmd.getArgs()[0];
		logger.info("Command: " + command);
		System.out.println("here");
		if ("makeModel".equalsIgnoreCase(command)) {
			new ExifSummary().outputMakeModelList(db);
		} else if ("cacheSource".equalsIgnoreCase(command)) {
			db.scan(new CombinedEncoder().getSupportedExceptions());
		}
	}

	public static void setupLogging() {
	}
}
