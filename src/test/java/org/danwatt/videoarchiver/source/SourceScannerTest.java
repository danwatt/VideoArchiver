package org.danwatt.videoarchiver.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.danwatt.videoarchiver.FileHasher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Sets;

public class SourceScannerTest {

	SourceScanner scanner = new SourceScanner();

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void emptyDirectory() throws Exception {
		assertEquals(0, folder.getRoot().listFiles().length);

		SourceDb source = scanner.quickScan(folder.getRoot(), Sets.newHashSet("jpg"));

		assertEquals(0, source.getItems().size());
	}

	@Test
	public void singleMatchedFile() throws Exception {
		// Sample image from exif.org:
		// http://www.exif.org/samples/fujifilm-mx1700.jpg
		File f = folder.newFile("sample.jpg");
		FileUtils.copyFile(new File(SourceScannerTest.class.getClassLoader().getResource("sample.jpg").toURI()), f);

		SourceDb source = scanner.quickScan(folder.getRoot(), Sets.newHashSet("jpg"));

		assertEquals(1, source.getItems().size());
		String hash = FileHasher.quickHash(f, SourceScanner.QUICK_HASH_SIZE);
		assertTrue(source.getItems().containsKey(hash));
		SourceItem item = source.getItems().get(hash).get(0);
		assertEquals(f.length(), item.getLength());
		assertEquals("sample.jpg", item.getRelativePath());
	}

	@Test
	public void subDirectory() throws Exception {
		folder.newFolder("test");
		File f = folder.newFile("test" + File.separator + "sample.jpg");

		FileUtils.copyFile(new File(SourceScannerTest.class.getClassLoader().getResource("sample.jpg").toURI()), f);

		SourceDb source = scanner.quickScan(folder.getRoot(), Sets.newHashSet("jpg"));
		assertEquals("test/sample.jpg", source.getItems().get(FileHasher.quickHash(f, SourceScanner.QUICK_HASH_SIZE)).get(0).getRelativePath());
	}

	@Test
	public void noMatches() throws Exception {
		File f = folder.newFile("sample.txt");
		FileUtils.write(f, "dummy");

		SourceDb source = scanner.quickScan(folder.getRoot(), Sets.newHashSet("jpg"));

		assertEquals(0, source.getItems().size());
	}

	@Test
	public void fillInMissing() throws Exception {
		// Sample image from exif.org:
		// http://www.exif.org/samples/fujifilm-mx1700.jpg
		File f = folder.newFile("sample.jpg");
		FileUtils.copyFile(new File(SourceScannerTest.class.getClassLoader().getResource("sample.jpg").toURI()), f);

		SourceDb source = scanner.quickScan(folder.getRoot(), Sets.newHashSet("jpg"));
		scanner.fillInMissingData(folder.getRoot(), source);
		
		String quickHash = FileHasher.quickHash(f, SourceScanner.QUICK_HASH_SIZE);
		String fullHash = FileHasher.hashFile(f);
		assertEquals(fullHash, source.getItems().get(quickHash).get(0).getHash());
	}
}
