/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.file;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

public class TextFileReaderTest {
	TextFileReader tfr;
	private final String TEST_STRING = "The red fox\nfoo bar";
	private final String TEST_FILE_PATH = "testdata.txt";

	@Before
	public void setUp() throws Exception {
		tfr = new TextFileReader();
	}

	@Test
	public void testReadFile() throws IOException, URISyntaxException {
		URL url = Thread.currentThread().getContextClassLoader().getResource(TEST_FILE_PATH);
		File file = new File(url.toURI());

		String text = tfr.read(file);
		assertThat(text, is(TEST_STRING));
	}

	@Test
	public void testReadInputStream() throws IOException {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(TEST_FILE_PATH);
		String text = tfr.read(is);
		assertThat(text, is(TEST_STRING));
	}

	@Test
	public void testReadString() throws IOException, URISyntaxException {
		URL url = Thread.currentThread().getContextClassLoader().getResource(TEST_FILE_PATH);
		File file = new File(url.toURI());
		String filePath = file.toString();

		String text = tfr.read(filePath);
		assertThat(text, is(TEST_STRING));
	}

}
