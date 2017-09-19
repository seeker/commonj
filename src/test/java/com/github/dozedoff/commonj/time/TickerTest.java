package com.github.dozedoff.commonj.time;

import static org.awaitility.Awaitility.await;
import static org.awaitility.proxy.AwaitilityClassProxy.to;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

// FIXME find a better way to test this, depending on timing is too unreliable
@Ignore("This tests are flaky, needs to be reworked")
public class TickerTest {
	private DummyTicker dt;

	@Test
	public void testTickEventShort() throws Exception {
		dt = new DummyTicker(10, TimeUnit.MILLISECONDS);

		await().between(90, TimeUnit.MILLISECONDS, 150, TimeUnit.MILLISECONDS).untilCall(to(dt).getTickCount(),
				is(greaterThanOrEqualTo(10)));
	}

	@Test
	public void testTickEventLong() throws Exception {
		dt = new DummyTicker(100, TimeUnit.MILLISECONDS);

		await().between(190, TimeUnit.MILLISECONDS, 220, TimeUnit.MILLISECONDS).untilCall(to(dt).getTickCount(),
				is(greaterThanOrEqualTo(3)));
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
