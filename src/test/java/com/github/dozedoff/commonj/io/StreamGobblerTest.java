/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.io;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;

public class StreamGobblerTest {
	private StreamGobbler cut;
	private static final String TEST_TEXT = "This is a test.";

	@Before
	public void setUp() throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(TEST_TEXT.getBytes(StandardCharsets.UTF_8));
		cut = new StreamGobbler(bais);

	}

	@Test
	public void testGetBuffer() throws Exception {
		cut.start();

		Thread.sleep(100);

		assertThat(cut.getBuffer(), is(TEST_TEXT));
	}

	@Test
	public void testGetBufferOnThreadInNewState() throws Exception {
		assertThat(cut.getBuffer(), isEmptyString());
	}
}
