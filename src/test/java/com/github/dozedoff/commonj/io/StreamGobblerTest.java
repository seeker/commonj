/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.io;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.awaitility.Duration;
import org.junit.Before;
import org.junit.Test;

public class StreamGobblerTest {
	private StreamGobbler cut;
	private static final String TEST_TEXT = "This is a test.";
	private static final Duration DURATION = new Duration(200, TimeUnit.MILLISECONDS);

	@Before
	public void setUp() throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(TEST_TEXT.getBytes(StandardCharsets.UTF_8));
		cut = new StreamGobbler(bais);

	}

	@Test
	public void testGetBuffer() throws Exception {
		cut.start();

		await().atMost(DURATION).until(cut::getBuffer, is(TEST_TEXT));
	}

	@Test
	public void testGetBufferOnThreadInNewState() throws Exception {
		assertThat(cut.getBuffer(), isEmptyString());
	}
}
