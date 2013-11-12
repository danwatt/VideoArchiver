package org.danwatt.videoarchiver;

import org.apache.commons.exec.CommandLine;
import static org.apache.commons.lang.StringUtils.*;
import org.danwatt.videoarchiver.config.ArchiverConfiguration;
import org.danwatt.videoarchiver.config.Setting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FfMpegCommandLineBuilder {

	public static CommandLine buildCommandLine(MediaSourceFile file, String output, ArchiverConfiguration config) {
		Setting defaultSetting = config.getSetting("default");
		System.out.println("Looking for specific setting for " + file.getFormatIdentifier());
		Setting specificSetting = config.getSetting(file.getFormatIdentifier());
		if (null == specificSetting) {
			System.out.println("No specific setting found for '"+file.getFormatIdentifier()+"'");
			specificSetting = defaultSetting;
		}
		String vScale = defaultIfBlank(defaultSetting.getVideoScale(), specificSetting.getVideoScale());
		String qScale = defaultIfBlank(defaultSetting.getVideoQualityFactor(), specificSetting.getVideoQualityFactor());
		String vBitrate = defaultIfBlank(defaultSetting.getVideoBitrate(), specificSetting.getVideoBitrate());
		String aBitrate = defaultIfBlank(defaultSetting.getAudioBitrate(), specificSetting.getAudioBitrate());
		try {
			System.out.println(new ObjectMapper().writeValueAsString(specificSetting));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Audio bitrate: " + aBitrate);

		CommandLine cl = CommandLine.parse("/usr/local/bin/ffmpeg");
		String input = file.getPath();
		cl.addArgument("-i").addArgument(input);
		if (isNotBlank(vScale)) {
			cl.addArgument("-s").addArgument(vScale);
		}
		if (isNotBlank(qScale)) {
			cl.addArgument("-qscale:v").addArgument(qScale);
		} else {
			cl.addArgument("-b:v").addArgument(vBitrate);
		}
		cl.addArgument("-vcodec").addArgument("h264");
		if (isNotBlank(aBitrate)) {
			cl.addArgument("-b:a").addArgument(aBitrate);
		}
		cl.addArgument(output);
		cl.addArgument("-y");
		return cl;
	}
}
