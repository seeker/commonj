/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.io;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class DataProducerTest {
	DataProducerDummy dummy;

	private static final int TEST_TIMEOUT = 5000;

	@Before
	public void setUp() throws Exception {
		dummy = new DataProducerDummy();
	}

	private void spinWaitUntil(int availableWork) {
		while (dummy.availableWork() != availableWork) {
		}
	}

	// TODO split clear test into testClearInputQueue and testClearOutputQueue
	@Test(timeout = TEST_TIMEOUT)
	public void testClear() throws InterruptedException {
		String test[] = { "1", "2", "3" };
		LinkedList<Integer> data = new LinkedList<>();

		dummy.addToLoad(test);
		spinWaitUntil(test.length);
		dummy.drainTo(data, 10);
		assertThat(data.size(), is(3));

		data = new LinkedList<>();

		dummy.addToLoad(test);
		dummy.clear();
		dummy.addToLoad("5");
		spinWaitUntil(1);
		dummy.drainTo(data, 10);

		assertThat(data.size(), is(1));
	}

	@Test
	public void testAddToLoadIArray() throws InterruptedException {
		String test[] = { "1", "2", "3" };

		dummy.addToLoad(test);
		spinWaitUntil(test.length);
		assertThat(dummy.getProcessed(), is(3));
	}

	@Test
	public void testAddToLoadListOfI() throws InterruptedException {
		LinkedList<String> test = new LinkedList<>();
		test.add("1");
		test.add("2");
		test.add("3");

		dummy.addToLoad(test);
		spinWaitUntil(test.size());
		assertThat(dummy.getProcessed(), is(3));
	}

	@Test(timeout = TEST_TIMEOUT)
	public void testTakeData() throws InterruptedException {
		String test[] = { "1", "2", "3" };

		dummy.addToLoad(test);
		assertThat(dummy.takeData(), is(1));
		assertThat(dummy.takeData(), is(2));
		assertThat(dummy.takeData(), is(3));
	}

	@Test
	public void testDrainTo() throws InterruptedException {
		String test[] = { "1", "2", "3" };

		dummy.addToLoad(test);
		spinWaitUntil(test.length);
		LinkedList<Integer> result = new LinkedList<>();
		dummy.drainTo(result, 10);

		assertThat(result, hasItems(1, 2, 3));
		assertThat(dummy.getProcessed(), is(3));
	}
}
