package org.danwatt.videoarchiver.destination;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.danwatt.videoarchiver.source.SourceItem;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DestinationDbTests {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void determeDestinationDirectory() {
		SourceItem si = new SourceItem();
		si.setRelativePath("a/b/c/d.efg");

		DestinationDb db = new DestinationDb(folder.getRoot());

		assertEquals(folder.getRoot().getAbsolutePath() + File.separator + "a" + File.separator + "b" + File.separator + "c", db.determineDestinationDirectory(si).getAbsolutePath());

	}
}
