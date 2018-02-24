/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
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
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

public class FileFilterTest {
	private FileFilter ff;

	@Before
	public void setUp() {
		ff = new FileFilter();
	}

	@Test
	public void testFile() throws IOException {
		Path file = Files.createTempFile("test", ".dat");
		assertTrue(ff.accept(file));
	}

	@Test
	public void testDirectory() throws IOException {
		Path directory = Files.createTempDirectory("test");
		assertFalse(ff.accept(directory));
	}

	@Test
	public void testNonExistantFile() throws IOException {
		Path none = new File("foobar.txt").toPath();

		// Guard condition
		assertThat("The file must not exist", Files.exists(none), is(false));

		assertFalse(ff.accept(none));
	}

	@Test
	public void testNull() throws IOException {
		assertFalse(ff.accept((Path) null));
	}
}
