package org.danwatt.videoarchiver.source;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SourceIOTest {
	SourceIO io;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private SourceDb db;

	@Before
	public void setup() {
		io = new SourceIO(folder.getRoot());
		db = new SourceDb();
	}

	@Test
	public void saveEmptyDb() throws IOException {
		io.save(db);
		File[] files = folder.getRoot().listFiles();
		assertEquals(1, files.length);
		assertTrue(files[0].length() > 1);
		assertEquals(SourceIO.MEDIA_ARCHIVER_DB, files[0].getName());
	}
	@Test
	public void saveAndLoad() throws IOException {
		db.getItems().put("test", new SourceItem());
		io.save(db);
		SourceDb loaded = io.load();
		assertEquals(1,loaded.getItems().size());
		assertEquals(1,loaded.getItems().get("test").size());
	}

}
