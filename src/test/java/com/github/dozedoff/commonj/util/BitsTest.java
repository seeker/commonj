/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class BitsTest {
	private static final String testStringLeft = "1011101";
	private static final String testStringRight = "1001001";

	@Test
	public void testHammingDistanceStringString() throws Exception {
		assertThat(Bits.hammingDistance(testStringLeft, testStringRight), is(2));
	}

	@Test
	public void testHammingDistanceStringString2() throws Exception {
		assertThat(Bits.hammingDistance(testStringRight, testStringLeft), is(2));
	}

	@Test
	public void testHammingDistanceLongLong() throws Exception {
		assertThat(Bits.hammingDistance(2L, 3L), is(1));
	}

	@Test
	public void testHammingDistanceLongLong2() throws Exception {
		assertThat(Bits.hammingDistance(2L, 4L), is(2));
	}

	@Test
	public void testHammingDistanceLongLong3() throws Exception {
		assertThat(Bits.hammingDistance(3L, 5L), is(2));
	}
}
