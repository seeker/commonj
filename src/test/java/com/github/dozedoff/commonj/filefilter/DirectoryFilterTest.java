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

public class DirectoryFilterTest {
	DirectoryFilter df;

	@Before
	public void setUp() {
		df = new DirectoryFilter();
	}

	@Test
	public void testNotDirectory() throws IOException {
		File file = Files.createTempFile("test", ".dat").toFile();
		assertFalse(df.accept(file));
	}

	@Test
	public void testDirectory() throws IOException {
		File directory = Files.createTempDirectory("test").toFile();
		assertTrue(df.accept(directory));
	}

	@Test
	public void testInaccessibleDirectory() {
		File directory = new File(".");
		assertTrue(df.accept(directory));
	}

	@Test
	public void testNonExistantFile() {
		File directory = new File("test");
		assertFalse(df.accept(directory));
	}

	@Test
	public void testNull() {
		assertFalse(df.accept(null));
	}

}
