package org.danwatt.videoarchiver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.danwatt.videoarchiver.config.ArchiverConfiguration;
import org.joda.time.DateTime;

public class MetadataExtractor {
	private static final String EXIF_CREATE_DATE = "Create Date";
	private static final String EXIF_AUDIO_SAMPLE_RATE = "Audio Sample Rate";
	private static final String EXIF_IMAGE_SIZE = "Image Size";
	private static final String EXIF_MODEL = "Model";
	private static final String EXIF_MAKE = "Make";
	private ArchiverConfiguration config;

	public MetadataExtractor(ArchiverConfiguration config) {
		this.config = config;
	}

	public MediaSourceFile extractMetadata(File file) throws IOException {
		MediaSourceFile metadata = new MediaSourceFile();
		metadata.setChecksum(org.danwatt.videoarchiver.FileHasher.hashFile(file));
		metadata.setPath(file.getAbsolutePath());
		String cl = config.getExifToolPath() + " " + file.getAbsolutePath();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (0 == executeAndCapture(cl, baos)) {
			parseResults(file, metadata, baos.toByteArray());
			return metadata;
		}
		return null;
	}

	private int executeAndCapture(String cl, ByteArrayOutputStream baos) throws ExecuteException, IOException {
		DefaultExecutor executor = new DefaultExecutor();
		executor.setStreamHandler(new PumpStreamHandler(baos));
		return executor.execute(CommandLine.parse(cl));
	}

	private void parseResults(File f, MediaSourceFile metadata, byte[] exifToolOutpt) throws IOException {
		try {
			Map<String, String> meta = new HashMap<String, String>();
			List<String> lines = IOUtils.readLines(new ByteArrayInputStream(exifToolOutpt));
			for (String line : lines) {
				meta.put(StringUtils.substringBefore(line, ":").trim(), StringUtils.substringAfter(line, ":").trim());
			}
			metadata.setAudioRate(Integer.parseInt(meta.get(EXIF_AUDIO_SAMPLE_RATE)));
			metadata.setImageDimensions(meta.get(EXIF_IMAGE_SIZE));
			metadata.setModel(meta.get(EXIF_MODEL));
			metadata.setMake(meta.get(EXIF_MAKE));
			metadata.setCaptureDate(DateTime.parse(StringUtils.replace(meta.get(EXIF_CREATE_DATE), ":", "-", 2).replace(" ", "T")));
		} catch (Exception e) {
			System.out.println("Problem with " + f.getAbsolutePath());
			e.printStackTrace();
		}
	}
}
