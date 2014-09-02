/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.filefilter;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

public class ArchiveFilterTest {
	private ArchiveFilter af;

	@Before
	public void setUp() {
		af = new ArchiveFilter();
	}

	private File createTempFile(String suffix) throws IOException {
		File file = Files.createTempFile("", suffix).toFile();
		return file;
	}

	@Test
	public void test7zArchive() throws IOException {
		File file = createTempFile("test.7z");
		assertTrue(af.accept(file));
	}

	@Test
	public void testZipArchive() throws IOException {
		File file = createTempFile("test.zip");
		assertTrue(af.accept(file));
	}
}
