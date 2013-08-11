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
package com.github.dozedoff.commonj.file;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class ArchiveVisitorTest {
	private ArchiveVisitor av;
	private LinkedList<Path> archiveList;
	private Path testDir;
	private Path[] valid = new Path[2];

	@Before
	public void setUp() throws Exception {
		archiveList = new LinkedList<>();
		av = new ArchiveVisitor(archiveList);
		buildFileStructure();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testArchiveVisitorNullList() {
		av = new ArchiveVisitor(null);
	}

	@Test
	public void testArchiveVisitor() throws IOException {
		av = new ArchiveVisitor(archiveList);
		Files.walkFileTree(testDir, av);
		assertThat(archiveList, hasItems(valid));
	}

	private void buildFileStructure() throws IOException {
		testDir = Files.createTempDirectory("ArchiveVisitorTest");

		Files.createFile(testDir.resolve("a.img"));

		Path dirA = Files.createDirectories(testDir.resolve("dirA"));
		Path dirB = Files.createDirectories(testDir.resolve("dirB"));

		valid[0] = Files.createFile(dirA.resolve("foo.7z"));
		Files.createFile(dirA.resolve("foo2.tmp"));

		valid[1] = Files.createFile(dirB.resolve("bar.rar"));
		Files.createFile(dirB.resolve("bar2.jpg"));
	}
}
