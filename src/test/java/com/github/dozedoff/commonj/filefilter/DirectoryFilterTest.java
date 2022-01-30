/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.filefilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

public class DirectoryFilterTest {
	private static final String TEST_NAME = "test";

	private DirectoryFilter df;

	@Before
	public void setUp() {
		df = new DirectoryFilter();
	}

	@Test
	public void testNotDirectory() throws IOException {
		Path file = Files.createTempFile(TEST_NAME, ".dat");
		assertFalse(df.accept(file));
	}

	@Test
	public void testDirectory() throws IOException {
		Path directory = Files.createTempDirectory(TEST_NAME);
		assertTrue(df.accept(directory));
	}

	@Test
	public void testInaccessibleDirectory() throws Exception {
		Path directory = new File(".").toPath();
		assertTrue(df.accept(directory));
	}

	@Test
	public void testNonExistantFile() throws Exception {
		Path directory = new File(TEST_NAME).toPath();
		assertFalse(df.accept(directory));
	}

	@Test
	public void testNull() throws Exception {
		assertFalse(df.accept((Path) null));
	}
}
