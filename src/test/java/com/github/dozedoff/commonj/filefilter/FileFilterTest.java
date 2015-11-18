/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.filefilter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

public class FileFilterTest {
	FileFilter ff;

	@Before
	public void setUp() {
		ff = new FileFilter();
	}

	@Test
	public void testFile() throws IOException {
		File file = Files.createTempFile("test", ".dat").toFile();
		assertTrue(ff.accept(file));
	}

	@Test
	public void testDirectory() throws IOException {
		File directory = Files.createTempDirectory("test").toFile();
		assertFalse(ff.accept(directory));
	}

	@Test
	public void testNonExistantFile() {
		File none = new File("foobar.txt");

		// Guard condition
		assertThat("The file must not exist", none.exists(), is(false));

		assertFalse(ff.accept(none));
	}

	@Test
	public void testNull() {
		assertFalse(ff.accept((File) null));
	}
}
