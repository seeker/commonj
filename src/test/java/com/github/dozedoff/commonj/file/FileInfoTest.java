/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.file;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class FileInfoTest {
	private FileInfo fi;

	private final String testFile1 = "/foo/bar/test.dat";
	private final String testFile2 = "/foo/bar.dat";

	private final String testHash1 = "ABCDEF";
	private final String testHash2 = "ABC";

	@Before
	public void setUp() throws Exception {
		fi = new FileInfo(Paths.get(testFile1), testHash1);
	}

	@Test
	public void testFileInfoFileString() {
		fi = new FileInfo(new File(testFile2), testHash2);
		assertThat(fi.getFile(), is(new File(testFile2)));
		assertThat(fi.getHash(), is(testHash2));
	}

	@Test
	public void testFileInfoPathString() {
		fi = new FileInfo(Paths.get(testFile2), "ABC");
		assertThat(fi.getFilePath(), is(Paths.get(testFile2)));
		assertThat(fi.getHash(), is(testHash2));
	}

	@Test
	public void testFileInfoFile() {
		fi = new FileInfo(new File(testFile2));
		assertThat(fi.getFile(), is(new File(testFile2)));
		assertNull(fi.getHash());
	}

	@Test
	public void testFileInfoPath() {
		fi = new FileInfo(Paths.get(testFile2));
		assertThat(fi.getFilePath(), is(Paths.get(testFile2)));
		assertNull(fi.getHash());
	}

	@Test
	public void testGetFile() {
		assertThat(fi.getFile(), is(new File(testFile1)));
	}

	@Test
	public void testGetFileNull() {
		fi = new FileInfo((File) null);
		assertNull(fi.getFile());
	}

	@Test
	public void testGetSize() {
		assertThat(fi.getSize(), is(-1L));
	}

	@Test
	public void testGetHash() {
		assertThat(fi.getHash(), is(testHash1));
	}

	@Test
	public void testSetFileFile() {
		fi.setFile(new File(testFile2));
		assertThat(fi.getFile(), is(new File(testFile2)));
		assertThat(fi.getFilePath(), is(Paths.get(testFile2)));
	}

	@Test
	public void testSetFilePath() {
		fi.setFile(Paths.get(testFile2));
		assertThat(fi.getFilePath(), is(Paths.get(testFile2)));
		assertThat(fi.getFile(), is(new File(testFile2)));
	}

	@Test
	public void testGetFilePath() {
		assertThat(fi.getFilePath(), is(Paths.get(testFile1)));
	}

	@Test
	public void testGetFilePathNull() {
		fi = new FileInfo((File) null);
		assertNull(fi.getFilePath());
	}

	@Test
	public void testSetSize() {
		fi.setSize(10L);
		assertThat(fi.getSize(), is(10L));
	}

	@Test
	public void testSetHash() {
		fi.setHash(testHash2);
		assertThat(fi.getHash(), is(testHash2));
	}

	@Test
	public void testHasHash() {
		assertTrue(fi.hasHash());
	}

	@Test
	public void testHasNoHash() {
		fi.setHash(null);
		assertFalse(fi.hasHash());
	}

	@Test
	public void testHasPath() {
		assertTrue(fi.hasPath());
	}

	@Test
	public void testHasNoPathFile() {
		fi.setFile((File) null);
		assertFalse(fi.hasPath());
	}

	@Test
	public void testHasNoPathPath() {
		fi.setFile((Path) null);
		assertFalse(fi.hasPath());
	}

	@Test
	public void testHasSize() {
		assertFalse(fi.hasSize());
	}

	@Test
	public void testHasSize2() {
		fi.setSize(1L);
		assertTrue(fi.hasSize());
	}
}
