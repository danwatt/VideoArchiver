package org.danwatt.videoarchiver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;


public class MetadataExtractor {
	static final ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	static {
		mapper.registerModule(new JodaModule());
	}
	public static void main(String[] args) throws IOException {
		SuffixFileFilter suffix = new SuffixFileFilter(Arrays.asList(".MOV",".AVI"),IOCase.INSENSITIVE);
		Collection<File> files = FileUtils.listFiles(new File("/Users/danwatt/Downloads"), suffix, FileFilterUtils.trueFileFilter());
		for (File f : files) {
			System.out.println(f.getAbsolutePath());
			VideoFile vf = new VideoFile();
			vf.setFullPath(f.getAbsolutePath());
			new MetadataExtractor().extractMetadata(vf);
		}
	}

	public void extractMetadata(VideoFile file) throws IOException {
		File f = new File(file.getFullPath());
		hashFile(file, f);
		
		String cl = "/usr/local/bin/exiftool " + f.getAbsolutePath();
		CommandLine cmdLine = CommandLine.parse(cl);
		DefaultExecutor executor = new DefaultExecutor();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		executor.setStreamHandler(new PumpStreamHandler(baos));
		int exitValue = executor.execute(cmdLine);
		baos.close();
		System.out.println("Done, " + exitValue);
		if (exitValue == 0) {
			Map<String,String> meta = new HashMap<String, String>();
			List<String> lines = IOUtils.readLines(new ByteArrayInputStream(baos.toByteArray()));
			for (String line : lines) {
				meta.put(StringUtils.substringBefore(line, ":").trim(), StringUtils.substringAfter(line, ":").trim());
			}
			file.setAudioRate(Integer.parseInt(meta.get("Audio Sample Rate")));
			file.setImageDimensions(meta.get("Image Size"));
			file.setModel(meta.get("Model"));
			file.setOriginalSize(FileUtils.sizeOf(f));
			file.setMake(meta.get("Make"));
			file.setCaptureDate(DateTime.parse(StringUtils.replace(meta.get("Create Date"),":","-",2).replace(" ", "T")));
			System.out.println(mapper.writeValueAsString(file));
		}
	}

	private void hashFile(VideoFile file, File f) throws FileNotFoundException, IOException {
		FileInputStream s = new FileInputStream(f);
		file.setSha1sum(DigestUtils.sha1Hex(s));
		s.close();
	}
}
