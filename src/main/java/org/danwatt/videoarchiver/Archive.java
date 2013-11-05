package org.danwatt.videoarchiver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public class Archive {
	private Map<String, ArchivedFileMetadata> files = new LinkedHashMap<String, ArchivedFileMetadata>();
	private File archiveDirectory;

	public Archive(File archiveDirectory) {
		this.archiveDirectory = archiveDirectory;
	}

	public void load() throws IOException {
		File archiveFile = archiveFileName(archiveDirectory);
		if (archiveFile.exists()) {
			List<String> lines = FileUtils.readLines(archiveFile);
			for (String line : lines) {
				String[] parts = StringUtils.split(line, "|");
				ArchivedFileMetadata af = new ArchivedFileMetadata();
				af.setSha1sum(parts[0]);
				af.setArchivedAt(DateTime.parse(parts[1]));
				af.setOriginalName(parts[2]);
				af.setArchivedRelativePath(parts[3]);
				files.put(af.getSha1sum(), af);
			}
		}

	}

	private File archiveFileName(File archiveDirectory) {
		return new File(archiveDirectory.getAbsolutePath() + File.separator + "mediaArchive.dat");
	}

	public void save(File archiveDirectory) throws IOException {
		archiveDirectory.mkdirs();
		List<String> lines = new ArrayList<String>();
		for (ArchivedFileMetadata f : files.values()) {
			lines.add(f.getSha1sum() + "|" + f.getArchivedAt().toString() + "|" + f.getOriginalName() + "|" + f.getArchivedRelativePath());
		}
		FileUtils.writeLines(archiveFileName(archiveDirectory), lines);
	}

	public boolean isArchived(String sha1sum) {
		return files.containsKey(sha1sum);
	}

	public Set<String> getChecksums() {
		return this.files.keySet();
	}
}
