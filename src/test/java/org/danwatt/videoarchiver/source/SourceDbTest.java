package org.danwatt.videoarchiver.source;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SourceDbTest {

	private SourceDb source;
	private SourceDb merge;
	private SourceItem item1;
	private SourceItem item2;

	@Before
	public void setup() {
		source = new SourceDb();
		merge = new SourceDb();
		item1 = new SourceItem();
		item2 = new SourceItem();
	}

	@Test
	public void mergeEmpty() {

		assertEquals(0, source.getItems().size());
		source.merge(merge);
		assertEquals(0, source.getItems().size());
	}

	@Test
	public void mergeIntoAnEmptySOurce() {
		merge.getItems().put("test", item1);

		assertEquals(0, source.getItems().size());
		source.merge(merge);
		assertEquals(1, source.getItems().size());
		assertTrue(source.getItems().get("test").contains(item1));
	}

	@Test
	public void mergeNoChangeWhenExactMatch() {
		item1.setLength(10);
		item1.setRelativePath("test.jpg");
		item2.setLength(item1.getLength());
		item2.setRelativePath(item1.getRelativePath());

		source.getItems().put("test", item1);
		merge.getItems().put("test", item2);

		assertEquals(1, source.getItems().size());
		source.merge(merge);
		assertEquals(1, source.getItems().size());
		assertSame(item1, source.getItems().get("test").get(0));
	}

	@Test
	public void replaceWhenThereIsADifferenceInSize() {
		item1.setLength(10);
		item1.setRelativePath("test.jpg");
		item2.setLength(item1.getLength() + 1);
		item2.setRelativePath(item1.getRelativePath());

		source.getItems().put("test", item1);
		merge.getItems().put("test", item2);

		assertEquals(1, source.getItems().size());
		source.merge(merge);
		assertEquals(1, source.getItems().size());
		assertSame(item2, source.getItems().get("test").get(0));
	}
}
