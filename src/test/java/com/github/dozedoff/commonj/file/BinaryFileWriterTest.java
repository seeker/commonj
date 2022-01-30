/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.file;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import com.github.dozedoff.commonj.util.Random;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("FCBL_FIELD_COULD_BE_LOCAL")
public class BinaryFileWriterTest {
	private final int SAMPLE_SIZE = 16384;

	private Path testfile;
	private byte[] testData;
	private BinaryFileWriter bfw;

	@Before
	public void setUp() throws Exception {
		testfile = Files.createTempFile("BinaryFileWriterTest", "");
		testData = Random.createRandomByteArray(SAMPLE_SIZE);
		bfw = new BinaryFileWriter();
	}

	@Test
	public void testWrite() throws Exception {
		bfw.write(testData, testfile);

		byte[] data = new byte[SAMPLE_SIZE];
		FileInputStream fis = new FileInputStream(testfile.toFile());

		fis.read(data);
		fis.close();

		assertArrayEquals(testData, data);
	}

	@Test
	public void testWriteSmallData() throws Exception {
		final int SMALL_SIZE = 10;
		testData = Random.createRandomByteArray(SMALL_SIZE);
		bfw.write(testData, testfile);

		byte[] data = new byte[SMALL_SIZE];
		FileInputStream fis = new FileInputStream(testfile.toFile());

		fis.read(data);
		fis.close();

		assertArrayEquals(testData, data);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWriteInvalidFile() throws Exception {
		bfw.write(testData, Paths.get(""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWriteNullPath() throws Exception {
		bfw.write(testData, (Path) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWriteNullData() throws Exception {
		bfw.write(null, testfile);
	}

	@Test(expected = IOException.class)
	public void testWriteFolder() throws IOException {
		Path directory = Files.createTempDirectory("BinaryFileWriterTest");
		bfw.write(testData, directory);
	}
}