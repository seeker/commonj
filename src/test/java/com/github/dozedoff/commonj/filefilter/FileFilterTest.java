/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.filefilter;

import static org.junit.Assert.assertFalse;
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
		File file = new File("test");
		assertFalse(ff.accept(file));
	}

	@Test
	public void testNull() {
		assertFalse(ff.accept(null));
	}
}
