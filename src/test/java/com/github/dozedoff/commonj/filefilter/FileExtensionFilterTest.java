/*  Copyright (C) 2013  Nicholas Wright

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
