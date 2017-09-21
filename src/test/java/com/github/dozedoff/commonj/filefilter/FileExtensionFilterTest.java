/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
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

public class FileExtensionFilterTest {
	private final String testExtensions[] = { "a", "foo", "bar" };
	private FileExtensionFilter fef;

	private File createTempFile(String suffix) throws IOException {
		File file = Files.createTempFile("", suffix).toFile();
		return file;
	}

	@Before
	public void setUp() throws Exception {
		fef = new FileExtensionFilter(testExtensions);
	}

	@SuppressWarnings("all")
	@Test
	public void testNullParameter() throws IOException {
		fef = new FileExtensionFilter(null);
		File file = createTempFile(".tmp");
		assertFalse(fef.accept(file));
	}

	@Test
	public void testNullParameterCast() throws IOException {
		fef = new FileExtensionFilter("foo", null, "bar");

		File file = createTempFile("test.foo");
		assertTrue(fef.accept(file));

		file = createTempFile("test.bar");
		assertTrue(fef.accept(file));
	}

	@Test
	public void testNonExistingValidFile() {
		File file = new File("test.a");
		assertFalse(fef.accept(file));
	}

	@Test
	public void testExistingFileWithInvalidExtension() throws IOException {
		File file = createTempFile("test.dat");
		assertFalse(fef.accept(file));
	}

	@Test
	public void testExistingValidExtension() throws IOException {
		File file = createTempFile("test.foo");
		assertTrue(fef.accept(file));
	}

	@Test
	public void testExistingDirectory() throws IOException {
		File directory = Files.createTempDirectory("test").toFile();
		assertFalse(fef.accept(directory));
	}

	@Test
	public void testExtensionLowerCase() throws IOException {
		File file = createTempFile("test.a");
		assertTrue(fef.accept(file));
	}

	@Test
	public void testExtensionUpperCase() throws IOException {
		File file = createTempFile("test.A");
		assertTrue(fef.accept(file));
	}

	@Test
	public void testExtensionMixedCase() throws IOException {
		File file = createTempFile("test.fOo");
		assertTrue(fef.accept(file));
	}

	@Test
	public void testNoDot() throws IOException {
		File file = createTempFile("testfoo");
		assertFalse(fef.accept(file));
	}

	@Test
	public void testNameContainsExtensionWithDot() throws IOException {
		File file = createTempFile("test.foo.img");
		assertFalse(fef.accept(file));
	}

	@Test
	public void testNameContainsExtensionWithoutDot() throws IOException {
		File file = createTempFile("testfoo.img");
		assertFalse(fef.accept(file));
	}

	@Test
	public void testNameContainsExtensionWithDotAndExtensionWoDot() throws IOException {
		File file = createTempFile("test.fooimg");
		assertFalse(fef.accept(file));
	}

	@Test
	public void testNoExtension() throws IOException {
		File file = createTempFile("test");
		assertFalse(fef.accept(file));
	}
}
