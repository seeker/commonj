/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.github.dozedoff.commonj.file.BinaryFileWriter;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("FCBL_FIELD_COULD_BE_LOCAL")
@SuppressWarnings("deprecation")
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
		bfr.write(randomData, testFile.toString());
	}

	@Test
	public void testOpenAsFileInputStream() throws IOException {
		FileInputStream fis = FileIO.openAsFileInputStream(testFile);
		byte[] data = new byte[SAMPLE_SIZE];
		int read = fis.read(data, 0, SAMPLE_SIZE);
		fis.close();

		assertThat(read, is(SAMPLE_SIZE));
		assertArrayEquals(randomData, data);
	}

	@Test
	public void testOpenAsFileInputStreamInvalidFile() throws IOException {
		File file = new File("foo");
		FileInputStream fis = FileIO.openAsFileInputStream(file);
		assertNull(fis);
	}

	@Test
	public void testOpenReadOnlyBuffer() {
		MappedByteBuffer mbb = FileIO.openReadOnlyBuffer(testFile);
		byte[] data = new byte[SAMPLE_SIZE];
		mbb.get(data);

		assertArrayEquals(randomData, data);
	}

	@Test
	public void testOpenReadOnlyBufferInvalidFile() {
		MappedByteBuffer mbb = FileIO.openReadOnlyBuffer(new File("foo"));
		assertNull(mbb);
	}

	@Test
	public void testCloseFileInputStream() {
		FileInputStream fis = FileIO.openAsFileInputStream(testFile);
		FileIO.closeFileInputStream(fis);
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
}
