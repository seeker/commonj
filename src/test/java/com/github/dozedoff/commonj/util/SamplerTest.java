/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SamplerTest {
	private static final int SAMPLER_SIZE = 10;

	private Sampler sampler;

	private int getSampleAt(int index) {
		List<Integer> samples = sampler.getSamples();
		return samples.get(index);
	}

	@Before
	public void setUp() throws Exception {
		sampler = new Sampler(SAMPLER_SIZE);
	}

	@Test
	public void testAddDelta() throws Exception {
		sampler.addDelta(5);
	}

	@Test
	public void testAddDeltaZero() throws Exception {
		sampler.addDelta(0);
	}

	@Test
	public void testAddDeltaCombination() throws Exception {
		sampler.addDelta(7);
		sampler.addDelta(3);

		sampler.sample();

		assertThat(getSampleAt(0), is(10));
	}

	@Test
	public void testAddDeltaNegative() throws Exception {
		sampler.addDelta(-5);
	}

	@Test
	public void testSampleZero() throws Exception {
		sampler.sample();
	}

	@Test
	public void testSample() throws Exception {
		sampler.addDelta(5);
		sampler.sample();
	}

	@Test
	public void testSampleNegative() throws Exception {
		sampler.addDelta(-42);
		sampler.sample();
	}

	@Test
	public void testGetAverageEmpty() throws Exception {
		double avg = sampler.getAverage();

		assertThat(avg, closeTo(0.0, 0.001));
	}

	@Test
	public void testGetAverageZero() throws Exception {
		sampler.sample();
		double avg = sampler.getAverage();

		assertThat(avg, closeTo(0.0, 0.001));
	}

	@Test
	public void testGetAverageSingleValue() throws Exception {
		sampler.addDelta(5);
		sampler.sample();
		double avg = sampler.getAverage();

		assertThat(avg, closeTo(5.0, 0.001));
	}

	@Test
	public void testGetAverageSingleValueNegative() throws Exception {
		sampler.addDelta(-5);
		sampler.sample();
		double avg = sampler.getAverage();

		assertThat(avg, closeTo(-5.0, 0.001));
	}

	@Test
	public void testGetAverageCancelOut() throws Exception {
		sampler.addDelta(-5);
		sampler.addDelta(5);
		sampler.sample();
		double avg = sampler.getAverage();

		assertThat(avg, closeTo(0.0, 0.001));
	}

	@Test
	public void testGetAverageFullBuffer() throws Exception {
		for (int i = 0; i < SAMPLER_SIZE; i++) {
			sampler.addDelta(i);
			sampler.sample();
		}

		double avg = sampler.getAverage();

		assertThat(avg, closeTo(4.5, 0.001));
	}

	@Test
	public void testGetSamplesEmpty() throws Exception {
		List<Integer> samples = sampler.getSamples();

		assertThat(samples.size(), is(0));
	}

	@Test
	public void testGetSamples() throws Exception {
		sampler.addDelta(42);
		sampler.sample();

		assertThat(getSampleAt(0), is(42));
	}

	@Test
	public void testGetSamplesNotSampled() throws Exception {
		sampler.addDelta(42);
		List<Integer> samples = sampler.getSamples();

		assertThat(samples.size(), is(0));
	}

	@Test
	public void testGetSamplesEviction() throws Exception {
		for (int i = 0; i < (SAMPLER_SIZE + 1); i++) {
			sampler.addDelta(i);
			sampler.sample();
		}

		assertThat(getSampleAt(0), is(1));
	}

	@Test
	public void testGetSamplesFull() throws Exception {
		for (int i = 0; i < SAMPLER_SIZE; i++) {
			sampler.addDelta(i);
			sampler.sample();
		}

		assertThat(getSampleAt(0), is(0));
	}
}
