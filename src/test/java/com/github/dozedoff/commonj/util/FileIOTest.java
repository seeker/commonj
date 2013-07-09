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

public class FileIOTest {
	private final int SAMPLE_SIZE = 10;
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
