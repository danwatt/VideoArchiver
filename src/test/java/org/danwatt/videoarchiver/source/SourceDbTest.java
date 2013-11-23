package org.danwatt.videoarchiver.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.danwatt.videoarchiver.FileHasher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SourceDbTest {

	private static final List<String> JPEGS = Arrays.asList("jpg");

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private SourceDb source;
	private SourceDb source2;
	private SourceItem item1;

	@Before
	public void setup() {
		source = new SourceDb(folder.getRoot());
		source2 = new SourceDb(folder.getRoot());
		item1 = new SourceItem();
	}

	@Test
	public void loadNonExistant() throws Exception {
		source.load();
		assertEquals(0, source.getItems().size());
	}

	@Test
	public void saveSingleAndLoad() throws Exception {
		source.getItems().put("test", item1);
		source.save();

		source2.load();
		assertEquals(1, source2.getItems().size());
	}

	@Test
	public void emptyDirectory() throws Exception {
		assertEquals(0, folder.getRoot().listFiles().length);

		source.scan(JPEGS);
	}

	@Test
	public void singleMatchedFile() throws Exception {
		// Sample image from exif.org:
		// http://www.exif.org/samples/fujifilm-mx1700.jpg
		File f = copySampleJpg();

		source.scan(JPEGS);

		assertEquals(1, source.getItems().size());
		String hash = FileHasher.quickHash(f, SourceDb.QUICK_HASH_SIZE);
		assertTrue(source.getItems().containsKey(hash));
		SourceItem item = source.getItems().get(hash).get(0);
		assertEquals(f.length(), item.getLength());
		assertEquals("sample.jpg", item.getRelativePath());
		assertEquals(FileHasher.hashFile(f),item.getHash());
		assertFalse(item.getCachedExifTool().isEmpty());
	}

	private File copySampleJpg() throws IOException, URISyntaxException {
		File f = folder.newFile("sample.jpg");
		FileUtils.copyFile(new File(SourceDbTest.class.getClassLoader().getResource("sample.jpg").toURI()), f);
		return f;
	}

	@Test
	public void subDirectory() throws Exception {
		folder.newFolder("test");
		File f = folder.newFile("test" + File.separator + "sample.jpg");

		FileUtils.copyFile(new File(SourceDbTest.class.getClassLoader().getResource("sample.jpg").toURI()), f);

		source.scan(JPEGS);
		assertEquals("test/sample.jpg", source.getItems().get(FileHasher.quickHash(f, SourceDb.QUICK_HASH_SIZE)).get(0).getRelativePath());
	}

	@Test
	public void noMatches() throws Exception {
		File f = folder.newFile("sample.txt");
		FileUtils.write(f, "dummy");

		source.scan(JPEGS);

		assertEquals(0, source.getItems().size());
	}

	@Test
	public void disregardQuickScanWhenMatchExists() throws Exception {
		File f = copySampleJpg();
		String quickHash = FileHasher.quickHash(f, SourceDb.QUICK_HASH_SIZE);
		item1.setQuickHash(quickHash);
		item1.setLength(f.length());
		item1.setRelativePath(source.relativePath(f));
		item1.setHash("full");
		source.getItems().put(quickHash, item1);

		source.scan(JPEGS);

		assertEquals(1, source.getItems().size());
		assertEquals("full", source.getItems().get(quickHash).get(0).getHash());
	}

	@Test
	public void whenScanRevealsFileHasChangedRemoveExistingData() throws Exception {
		File f = copySampleJpg();
		String quickHash = FileHasher.quickHash(f, SourceDb.QUICK_HASH_SIZE);
		item1.setQuickHash("hashIsAcutallyDifferent");
		item1.setLength(f.length());
		item1.setRelativePath(source.relativePath(f));
		item1.setHash("full");
		source.add(item1);

		source.scan(JPEGS);

		assertEquals(1, source.getItems().size());
		assertEquals(quickHash, source.getItems().get(quickHash).get(0).getQuickHash());
	}
}
