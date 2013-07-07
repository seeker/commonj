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
}
