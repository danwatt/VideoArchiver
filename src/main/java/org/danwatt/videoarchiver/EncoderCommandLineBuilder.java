package org.danwatt.videoarchiver;

import org.apache.commons.exec.CommandLine;

public class EncoderCommandLineBuilder {

	public static CommandLine buildCommandLine(MediaSourceFile file, String output) {
		CommandLine cl = CommandLine.parse("/usr/local/bin/ffmpeg");
		String input = file.getPath();
		cl.addArgument("-i").addArgument(input);
		cl.addArgument("-s").addArgument("hd480");
		cl.addArgument("-qscale:v").addArgument("5");
		cl.addArgument("-vcodec").addArgument("h264");
		if (file.getPath().toLowerCase().endsWith("avi")) {
			cl.addArgument("-b:a").addArgument("32k");
		} else {
			
		}
		cl.addArgument(output);
		return cl;
	}

}
