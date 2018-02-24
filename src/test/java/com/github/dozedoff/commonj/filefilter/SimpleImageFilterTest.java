/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.filefilter;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

public class SimpleImageFilterTest {
	private SimpleImageFilter sif;

	@Before
	public void setUp() {
		sif = new SimpleImageFilter();
	}

	private Path createTempFile(String suffix) throws IOException {
		return Files.createTempFile("", suffix);
	}

	@Test
	public void testgif() throws IOException {
		Path file = createTempFile("test.gif");
		assertTrue(sif.accept(file));
	}

	@Test
	public void testjpg() throws IOException {
		Path file = createTempFile("test.jpg");
		assertTrue(sif.accept(file));
	}
}
