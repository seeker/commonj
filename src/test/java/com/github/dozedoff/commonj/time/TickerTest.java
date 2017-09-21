/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.time;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.awaitility.Duration;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Test;

public class TickerTest {
	private DummyTicker dt;

	private static final int TICK_DURATION = 10;
	private static final TimeUnit TICK_UNIT = TimeUnit.MILLISECONDS;

	private static final Duration AWAIT_DURATION = new Duration(750, TimeUnit.MILLISECONDS);

	private static final String THREAD_NAME = "Foobar";
	private static final String DEFAULT_THREAD_NAME = "Timer";

	@After
	public void tearDown() {
		dt.cancel();
	}

	private List<String> getThreadNames() {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		return threadSet.stream().map(thread -> thread.getName()).collect(Collectors.toList());
	}

	private void assertThreadNames(Matcher<Iterable<? super String>> matcher) {
		await().pollDelay(TICK_DURATION, TICK_UNIT).atMost(AWAIT_DURATION).until(new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				return getThreadNames();
			}
		}, matcher);
	}

	@Test
	public void testTickEvent() throws Exception {
		dt = new DummyTicker(TICK_DURATION, TICK_UNIT);

		await().pollDelay(TICK_DURATION, TICK_UNIT).atMost(AWAIT_DURATION).until(new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				return dt.getTickCount();
			}
		}, is(greaterThanOrEqualTo(1)));
	}


	@Test
	public void testCancel() throws Exception {
		dt = new DummyTicker(THREAD_NAME, TICK_DURATION, TICK_UNIT);

		assertThreadNames(hasItem(THREAD_NAME));

		dt.cancel();

		assertThreadNames(not(hasItem(THREAD_NAME)));
	}

	@Test
	public void testNamedTicker() throws Exception {
		dt = new DummyTicker(THREAD_NAME, TICK_DURATION, TICK_UNIT);

		assertThreadNames(hasItem(THREAD_NAME));
	}

	@Test
	public void testDefaultNamedTicker() throws Exception {
		dt = new DummyTicker(TICK_DURATION, TICK_UNIT);

		assertThreadNames(hasItem(DEFAULT_THREAD_NAME));
	}

	static class DummyTicker extends Ticker {
		private int tickCount;

		/**
		 * Create a new {@link DummyTicker} for testing.
		 * 
		 * @param time
		 *            {@inheritDoc}
		 * @param timeUnit
		 *            {@inheritDoc}
		 */
		DummyTicker(long time, TimeUnit timeUnit) {
			super(time, timeUnit);
			tickCount = 0;
		}

		/**
		 * Create a new {@link DummyTicker} for testing.
		 * 
		 * @param tickerName
		 *            {@inheritDoc}
		 * @param time
		 *            {@inheritDoc}
		 * @param timeUnit
		 *            {@inheritDoc}
		 */
		DummyTicker(String tickerName, long time, TimeUnit timeUnit) {
			super(tickerName, time, timeUnit);
			tickCount = 0;
		}

		/**
		 * Executed on each tick.
		 */
		@Override
		public void tickEvent() {
			tickCount++;
		}

		/**
		 * Get the number of tick events.
		 * 
		 * @return count of tick events
		 */
		public int getTickCount() {
			return tickCount;
		}
	}
}
