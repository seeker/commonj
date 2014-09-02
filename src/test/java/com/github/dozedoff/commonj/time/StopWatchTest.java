/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.time;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class StopWatchTest {
	StopWatch stopWatch;

	private static final long SLEEP_TIME = 100L;
	private static final long LOWER_LIMIT = 80L;
	private static final long UPPER_LIMIT = 160L;

	@Before
	public void setUp() throws Exception {
		stopWatch = new StopWatch();
	}

	@Test
	public void testStart() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(SLEEP_TIME);
		stopWatch.start();
		Thread.sleep(SLEEP_TIME);
		stopWatch.stop();

		assertThat(stopWatch.getTimeMilli(), allOf(greaterThanOrEqualTo(2 * LOWER_LIMIT), lessThanOrEqualTo(2 * UPPER_LIMIT)));
	}

	@Test
	public void testStop() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(SLEEP_TIME);
		stopWatch.stop();
		stopWatch.stop();

		assertThat(stopWatch.getTimeMilli(), allOf(greaterThanOrEqualTo(LOWER_LIMIT), lessThanOrEqualTo(UPPER_LIMIT)));
	}

	@Test
	public void testGetTime() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(SLEEP_TIME);
		stopWatch.stop();

		assertThat(stopWatch.getTime(), startsWith("00:00:00.1"));
	}

	@Test
	public void testGetTimeWhileRunning() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(SLEEP_TIME);
		assertThat(stopWatch.getTime(), startsWith("00:00:00.1"));
		stopWatch.stop();
	}

	@Test
	public void testGetTimeMilli() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(SLEEP_TIME);
		stopWatch.stop();

		assertThat(stopWatch.getTimeMilli(), allOf(greaterThanOrEqualTo(LOWER_LIMIT), lessThanOrEqualTo(UPPER_LIMIT)));
	}

	@Test
	public void testGetTimeMilliWhileRunning() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(SLEEP_TIME);
		assertThat(stopWatch.getTimeMilli(), allOf(greaterThanOrEqualTo(LOWER_LIMIT), lessThanOrEqualTo(UPPER_LIMIT)));
		stopWatch.stop();
	}

	@Test
	public void testIsRunning() {
		assertThat(stopWatch.isRunning(), is(false));

		stopWatch.start();
		assertThat(stopWatch.isRunning(), is(true));

		stopWatch.stop();
		assertThat(stopWatch.isRunning(), is(false));
	}

	@Test
	public void testReset() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(SLEEP_TIME);
		stopWatch.stop();

		assertThat(stopWatch.getTimeMilli(), allOf(greaterThanOrEqualTo(LOWER_LIMIT), lessThanOrEqualTo(UPPER_LIMIT)));

		stopWatch.reset();

		assertThat(stopWatch.getTimeMilli(), is(0L));
	}

	@Test
	public void testInitialState() {
		assertThat(stopWatch.getTimeMilli(), is(0L));
	}

	@Test
	public void testConvertTimeZero() {
		assertThat(stopWatch.convertTime(0), is("00:00:00.000"));
	}

	@Test
	public void testConvertTimeInvalid() {
		assertThat(stopWatch.convertTime(-1), is("--:--:--.---"));
	}

	@Test
	public void testConvertTime() {
		assertThat(stopWatch.convertTime(SLEEP_TIME), is("00:00:00.100"));
	}

	@Test
	public void testReRun() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(SLEEP_TIME);
		stopWatch.stop();

		stopWatch.reset();

		stopWatch.start();
		Thread.sleep(SLEEP_TIME);
		assertThat(stopWatch.getTimeMilli(), allOf(greaterThanOrEqualTo(LOWER_LIMIT), lessThanOrEqualTo(UPPER_LIMIT)));
		stopWatch.stop();
	}
}
