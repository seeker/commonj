/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.file;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import com.github.dozedoff.commonj.util.Random;

public class BinaryFileWriterTest {
	private final int SAMPLE_SIZE = 16384;

	private String testfile;
	private byte[] testData;
	private BinaryFileWriter bfw;

	@Before
	public void setUp() throws Exception {
		testfile = Files.createTempFile("BinaryFileWriterTest", "").toString();
		testData = Random.createRandomByteArray(SAMPLE_SIZE);
		bfw = new BinaryFileWriter();
	}

	@Test
	public void testWrite() throws Exception {
		bfw.write(testData, testfile);

		byte[] data = new byte[SAMPLE_SIZE];
		FileInputStream fis = new FileInputStream(new File(testfile));

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
		FileInputStream fis = new FileInputStream(new File(testfile));

		fis.read(data);
		fis.close();

		assertArrayEquals(testData, data);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWriteInvalidFile() throws Exception {
		bfw.write(testData, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWriteNullPath() throws Exception {
		bfw.write(testData, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWriteNullData() throws Exception {
		bfw.write(null, testfile);
	}

	@Test(expected = FileNotFoundException.class)
	public void testWriteFolder() throws IOException {
		String directory = Files.createTempDirectory("BinaryFileWriterTest").toString();
		bfw.write(testData, directory);
	}
}