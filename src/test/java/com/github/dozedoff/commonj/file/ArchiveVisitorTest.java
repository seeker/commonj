/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
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
