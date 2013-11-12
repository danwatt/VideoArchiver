

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang.StringUtils;
import org.danwatt.videoarchiver.Archive;
import org.danwatt.videoarchiver.FfMpegCommandLineBuilder;
import org.danwatt.videoarchiver.MediaSource;
import org.danwatt.videoarchiver.MediaSourceFile;
import org.danwatt.videoarchiver.SourceScanner;
import org.danwatt.videoarchiver.config.ArchiverConfiguration;
import org.danwatt.videoarchiver.config.Setting;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class MediaArchiver {
	static final ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).configure(
			SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	static {
		mapper.registerModule(new JodaModule());
	}

	public static void main(String[] args) throws IOException {
		String archivePath = args[0];
		String sourcePath = args[1];

		File configFile = new File(archivePath + File.separator + "videoArchiveConfig.json");
		ArchiverConfiguration config = new ArchiverConfiguration();
		if (configFile.exists()) {
			config = mapper.readValue(configFile, ArchiverConfiguration.class);
		}
		if (!config.getSettings().containsKey("default")) {
			System.out.println("The provided configuration is empty. Please specify a default configuration");
			printExampleConfiguration();
			return;
		}
		Archive archive = new Archive(new File(archivePath));
		archive.load();
		System.out.println("The archive currently has " + archive.getChecksums().size() + " archived files");
		
		MediaSource source = new SourceScanner().gatherSourceFiles(config,sourcePath);
		System.out.println("Source has " + source.getChecksums().size() + " files");
		Set<String> checksumsToProcess = new LinkedHashSet<String>();
		checksumsToProcess.addAll(source.getChecksums());
		checksumsToProcess.removeAll(archive.getChecksums());
		System.out.println("There are a total of " + checksumsToProcess.size() + " unique files to archive");
		Map<String,MediaSourceFile> filesToArchive = new LinkedHashMap<String, MediaSourceFile>(source.getFiles());
		filesToArchive.keySet().retainAll(checksumsToProcess);
		runArchiver(filesToArchive,config,archivePath);
	}

	private static void printExampleConfiguration() throws IOException, JsonGenerationException, JsonMappingException {
		ArchiverConfiguration ec = new ArchiverConfiguration();
		ec.getSettings().put("default", new Setting());
		ec.getSettings().get("default").setAudioBitrate("");
		ec.getSettings().get("default").setAudioSamplerate("");
		ec.getSettings().get("default").setMaximumAudioChannels(2);
		ec.getSettings().get("default").setVideoBitrate("");
		ec.getSettings().get("default").setVideoQualityFactor("");
		ec.getSettings().get("default").setVideoScale("");			
		mapper.writeValue(System.out, ec);
	}

	private static void runArchiver(Map<String, MediaSourceFile> filesToArchive, ArchiverConfiguration config, String archivePath) throws IOException {
		for (MediaSourceFile file : filesToArchive.values()) {
			System.out.println("Archiving " + file.getPath());
			File targetDirectory = new File(archivePath + File.separator + file.getCaptureDate().getYear() +File.separator + file.getCaptureDate().getYear()+"-"+file.getCaptureDate().getMonthOfYear());
			if (targetDirectory.exists() || targetDirectory.mkdirs()) {
				String namePart = StringUtils.substringAfterLast(StringUtils.substringBeforeLast(file.getPath(), "."),File.separator);
				String output = targetDirectory.getAbsolutePath()+File.separator + namePart+".m4v";
				File outputFile = new File(output);
				outputFile.delete();
				CommandLine cl = FfMpegCommandLineBuilder.buildCommandLine(file, output,config);
				System.out.println("Executing " + cl.toString());
				DefaultExecutor executor = new DefaultExecutor();
				executor.setStreamHandler(new PumpStreamHandler(System.out));
				int result= executor.execute(cl);
				System.out.println("Result: " + result);
			} else {
				System.out.println("Unable to create directory " + targetDirectory.getAbsolutePath());
				//...
			}
		}
		
	}
}
