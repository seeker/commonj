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
