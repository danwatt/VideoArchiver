package org.danwatt.videoarchiver.encoder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.danwatt.videoarchiver.config.ArchiverConfiguration;
import org.danwatt.videoarchiver.config.EncoderOption;
import org.danwatt.videoarchiver.source.SourceItem;

public abstract class Encoder {

	public abstract String getIdentifier();

	public abstract Collection<String> getSupportedExceptions();

	public abstract CommandLine buildCommandLine(ArchiverConfiguration config, File sourceRoot, SourceItem sourceItem, File desitnationFile);

	public boolean encode(ArchiverConfiguration config, File sourceRoot, SourceItem sourceItem, File destinationFile) {
		List<EncoderOption> opts = config.getEncoderOptions().get(getIdentifier());
		if (opts == null || opts.isEmpty()) {
			System.out.println("Please provide at least one encoder option for " + getIdentifier());
			return false;
		}
		CommandLine cl = buildCommandLine(config, sourceRoot, sourceItem, destinationFile);
		if (null == cl) {
			return false;
		}
		Executor exec = new DefaultExecutor();
		try {
			System.out.println("Executing " + cl.toString());
			exec.execute(cl);
		} catch (ExecuteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destinationFile.exists();
	}
}
