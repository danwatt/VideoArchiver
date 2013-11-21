package org.danwatt.videoarchiver.source;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Sets;
import com.google.common.io.Resources;

public class SourceScannerTest {

	SourceScanner scanner = new SourceScanner();
	
	@Rule
    public TemporaryFolder folder= new TemporaryFolder();

	@Test
	public void emptyDirectory() {
		assertEquals(0,folder.getRoot().listFiles().length);
		
		SourceDb source = scanner.scan(folder.getRoot(), Sets.newHashSet("jpg"));
		
		assertEquals(0,source.getItems().size());
	}
	
	@Test
	public void singleMatchedFile() throws Exception {
		//Sample image from exif.org: http://www.exif.org/samples/fujifilm-mx1700.jpg
		File f = folder.newFile("sample.jpg");
		FileUtils.copyFile(new File(SourceScannerTest.class.getClassLoader().getResource("sample.jpg").toURI()), f);
		
		SourceDb source = scanner.scan(folder.getRoot(), Sets.newHashSet("jpg"));
		
		assertEquals(1,source.getItems().size());
	}
	
	@Test
	public void noMatches() throws Exception {
		File f = folder.newFile("sample.txt");
		FileUtils.write(f, "dummy");
		
		SourceDb source = scanner.scan(folder.getRoot(), Sets.newHashSet("jpg"));
		
		assertEquals(0,source.getItems().size());
	}
}
