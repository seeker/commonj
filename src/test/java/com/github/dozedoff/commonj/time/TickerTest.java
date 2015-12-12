package com.github.dozedoff.commonj.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TickerTest {
	private DummyTicker dt;

	@Test(timeout = 200)
	public void testTickEventShort() throws Exception {
		dt = new DummyTicker(10, TimeUnit.MILLISECONDS);
		Thread.sleep(120);
		assertThat(dt.getTickCount(), is(greaterThanOrEqualTo(10)));
	}

	@Test(timeout = 400)
	public void testTickEventLong() throws Exception {
		dt = new DummyTicker(100, TimeUnit.MILLISECONDS);
		Thread.sleep(220);
		assertThat(dt.getTickCount(), is(greaterThanOrEqualTo(2)));
	}

	@Test(timeout = 400)
	public void testCancel() throws Exception {
		dt = new DummyTicker(2, TimeUnit.SECONDS);
		Thread.sleep(100);
		dt.cancel();
		assertThat(dt.getTickCount(), is(1));
	}

	@Test(timeout = 200)
	public void testNamedTicker() throws Exception {
		dt = new DummyTicker("Foobar", 10, TimeUnit.MILLISECONDS);
		Thread.sleep(120);
		assertThat(dt.getTickCount(), is(greaterThanOrEqualTo(10)));
	}

	class DummyTicker extends Ticker {
		private int tickCount;

		public DummyTicker(long time, TimeUnit timeUnit) {
			super(time, timeUnit);
			tickCount = 0;
		}

		public DummyTicker(String tickerName, long time, TimeUnit timeUnit) {
			super(tickerName, time, timeUnit);
			tickCount = 0;
		}

		@Override
		public void tickEvent() {
			tickCount++;
		}

		public int getTickCount() {
			return tickCount;
		}
	}
}
