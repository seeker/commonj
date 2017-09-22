/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.time;

import java.util.Calendar;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class StopWatchTest {
	private static final long SHORT_DURATION = 1000L;
	private static final long LONG_DURATION = 3000L;

	private Calendar calendar;

	private StopWatch stopWatch;

	private void setElapsedTime(long timeInMillis, Long... more) {
		when(calendar.getTimeInMillis()).thenReturn(timeInMillis, more);
	}

	@Before
	public void setUp() throws Exception {
		calendar = mock(Calendar.class);
		setElapsedTime(1000L, 2000L, 4000L, 7000L);
		stopWatch = new StopWatch(calendar);
	}

	@Test
	public void testStartStop() throws InterruptedException {
		stopWatch.start();
		stopWatch.stop();

		assertThat(stopWatch.getTimeMilli(), is(SHORT_DURATION));
	}

	@Test
	public void testDoubleStart() throws InterruptedException {
		stopWatch.start();
		stopWatch.start();
		stopWatch.stop();

		assertThat(stopWatch.getTimeMilli(), is(SHORT_DURATION));
	}

	@Test
	public void testDoubleStop() throws InterruptedException {
		stopWatch.start();
		stopWatch.stop();
		stopWatch.stop();

		assertThat(stopWatch.getTimeMilli(), is(LONG_DURATION));
	}

	@Test
	public void testGetTime() throws InterruptedException {
		stopWatch.start();
		stopWatch.stop();

		assertThat(stopWatch.getTime(), is("00:00:01.000"));
	}

	@Ignore
	@Test
	public void testGetTimeWhileRunning() throws InterruptedException {
		stopWatch.start();
		assertThat(stopWatch.getTime(), startsWith("00:00:00.100"));
	}

	@Test
	public void testGetTimeMilliWhileRunning() throws InterruptedException {
		stopWatch.start();
		assertThat(stopWatch.getTimeMilli(), is(SHORT_DURATION));
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
		stopWatch.stop();

		assertThat(stopWatch.getTimeMilli(), is(SHORT_DURATION));

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
		assertThat(stopWatch.convertTime(LONG_DURATION), is("00:00:03.000"));
	}

	@Test
	public void testReRun() throws InterruptedException {
		stopWatch.start();
		stopWatch.stop();

		stopWatch.reset();

		stopWatch.start();
		assertThat(stopWatch.getTimeMilli(), is(LONG_DURATION));
	}
}
