package com.github.dozedoff.commonj.io;

import static org.hamcrest.CoreMatchers.is;
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
		cut.start();
	}

	@Test
	public void testGetBuffer() throws Exception {
		Thread.sleep(100);
		assertThat(cut.getBuffer(), is(TEST_TEXT));
	}
}
