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

public class FileExtensionFilterTest {
	private final String testExtensions[] = { "a", "foo", "bar" };
	private FileExtensionFilter fef;

	private Path createTempFile(String suffix) throws IOException {
		return Files.createTempFile("", suffix);
	}

	@Before
	public void setUp() throws Exception {
		fef = new FileExtensionFilter(testExtensions);
	}

	@Test
	public void testNullParameter() throws IOException {
		fef = new FileExtensionFilter((String[]) null);
		Path file = createTempFile(".tmp");
		assertFalse(fef.accept(file));
	}

	@Test
	public void testNullParameterCast() throws IOException {
		fef = new FileExtensionFilter("foo", null, "bar");

		Path file = createTempFile("test.foo");
		assertTrue(fef.accept(file));

		file = createTempFile("test.bar");
		assertTrue(fef.accept(file));
	}

	@Test
	public void testNonExistingValidFile() throws Exception {
		Path file = new File("test.a").toPath();
		assertFalse(fef.accept(file));
	}

	@Test
	public void testExistingFileWithInvalidExtension() throws IOException {
		Path file = createTempFile("test.dat");
		assertFalse(fef.accept(file));
	}

	@Test
	public void testExistingValidExtension() throws IOException {
		Path file = createTempFile("test.foo");
		assertTrue(fef.accept(file));
	}

	@Test
	public void testExistingDirectory() throws IOException {
		Path directory = Files.createTempDirectory("test");
		assertFalse(fef.accept(directory));
	}

	@Test
	public void testExtensionLowerCase() throws IOException {
		Path file = createTempFile("test.a");
		assertTrue(fef.accept(file));
	}

	@Test
	public void testExtensionUpperCase() throws IOException {
		Path file = createTempFile("test.A");
		assertTrue(fef.accept(file));
	}

	@Test
	public void testExtensionMixedCase() throws IOException {
		Path file = createTempFile("test.fOo");
		assertTrue(fef.accept(file));
	}

	@Test
	public void testNoDot() throws IOException {
		Path file = createTempFile("testfoo");
		assertFalse(fef.accept(file));
	}

	@Test
	public void testNameContainsExtensionWithDot() throws IOException {
		Path file = createTempFile("test.foo.img");
		assertFalse(fef.accept(file));
	}

	@Test
	public void testNameContainsExtensionWithoutDot() throws IOException {
		Path file = createTempFile("testfoo.img");
		assertFalse(fef.accept(file));
	}

	@Test
	public void testNameContainsExtensionWithDotAndExtensionWoDot() throws IOException {
		Path file = createTempFile("test.fooimg");
		assertFalse(fef.accept(file));
	}

	@Test
	public void testNoExtension() throws IOException {
		Path file = createTempFile("test");
		assertFalse(fef.accept(file));
	}
}
