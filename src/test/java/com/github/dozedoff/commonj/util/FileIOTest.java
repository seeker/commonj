/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.dozedoff.commonj.file.BinaryFileWriter;

public class FileIOTest {
	private static final int SAMPLE_SIZE = 10;
	private byte[] randomData;
	private File testFile;
	private final BinaryFileWriter bfr = new BinaryFileWriter();

	@BeforeClass
	public static void setupClass() {
		new FileIO();
	}

	@Before
	public void setup() throws Exception {
		randomData = Random.createRandomByteArray(SAMPLE_SIZE);
		testFile = Files.createTempFile("", "FileIOTest.dat").toFile();
		bfr.write(randomData, testFile.toPath());
	}

	@Test
	public void testCloseFileInputStreamNull() {
		FileIO.closeFileInputStream(null);
	}

	@Test
	public void testCloseFileInputStreamError() throws IOException {
		FileInputStream mockFis = mock(FileInputStream.class);
		Mockito.doThrow(IOException.class).when(mockFis).close();
		FileIO.closeFileInputStream(mockFis);
	}

	@Test
	public void testCloseFileInputStream() throws Exception {
		FileIO.closeFileInputStream(new FileInputStream(testFile));
	}
}
