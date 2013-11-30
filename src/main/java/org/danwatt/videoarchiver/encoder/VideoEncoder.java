package org.danwatt.videoarchiver.encoder;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.exec.CommandLine;
import org.danwatt.videoarchiver.config.ArchiverConfiguration;
import org.danwatt.videoarchiver.config.ArgumentBuilder;
import org.danwatt.videoarchiver.config.EncoderOption;
import org.danwatt.videoarchiver.source.SourceItem;

import com.google.common.collect.Sets;

public class VideoEncoder extends Encoder {
	public static final String FFMPEG_PATH = System.getProperty("ffmpeg.path", "ffmpeg");

	@Override
	public Collection<String> getSupportedExceptions() {
		return Sets.newHashSet("M4V", "QT", "MOV", "MPG", "AVI");
	}

	@Override
	public CommandLine buildCommandLine(ArchiverConfiguration config, File sourceRoot, SourceItem sourceItem, File desitnationFile) {
		CommandLine cl = null;
		List<EncoderOption> opts = config.getEncoderOptions().get(getIdentifier());
		Map<String, String> arguments = new ArgumentBuilder().buildArguments(opts, sourceItem);
		if (!arguments.isEmpty()) {
			cl = CommandLine.parse(FFMPEG_PATH);
			String input = new File(sourceRoot.getAbsolutePath() + File.separator + sourceItem.getRelativePath()).getAbsolutePath();
			cl.addArgument("-i").addArgument(input);
			for (Entry<String, String> arg : arguments.entrySet()) {
				cl.addArgument(arg.getKey()).addArgument(arg.getValue());
			}
			cl.addArgument(desitnationFile.getAbsolutePath() + ".m4v");
		}
		return cl;
	}

	@Override
	public String getIdentifier() {
		return "video";
	}
}
