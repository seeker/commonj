/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.filefilter;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

public class SimpleImageFilterTest {
	SimpleImageFilter sif;

	@Before
	public void setUp() {
		sif = new SimpleImageFilter();
	}

	private File createTempFile(String suffix) throws IOException {
		File file = Files.createTempFile("", suffix).toFile();
		return file;
	}

	@Test
	public void testgif() throws IOException {
		File file = createTempFile("test.gif");
		assertTrue(sif.accept(file));
	}

	@Test
	public void testjpg() throws IOException {
		File file = createTempFile("test.jpg");
		assertTrue(sif.accept(file));
	}
}
