package com.github.dozedoff.commonj.encoding;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class LZ77EncoderTest {
	private LZ77Encoder encoder;
	private final String sampleText = "FISCHERS FRITZ FISCHT FRISCHE FISCHE.";
	private final String expectedTokens = "00F00S00C00H00E00R51 91R101T00Z62I153T134S233 306.";
	
	@Before
	public void setUp() throws Exception {
		encoder = new LZ77Encoder(6, 32);
	}

	@Test
	public void testAddData() {
		encoder.addData(sampleText);
	}

	@Test
	public void testClear() {
		fail("Not yet implemented");
	}

	@Test
	public void testEncode() {
		encoder.addData(sampleText);
		encoder.encode();
	}

	@Test
	public void testGetTokens() {
		encoder.addData(sampleText);
		encoder.encode();
		String tokens = encoder.getTokens();
		assertThat(tokens, is(expectedTokens));
	}

}
