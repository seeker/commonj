package com.github.dozedoff.commonj.io;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class DataProducerTest {
	final int SLEEP_TIME = 100;
	DataProducerDummy dummy;
	
	@Before
	public void setUp() throws Exception {
		dummy = new DataProducerDummy();
	}

	@Test(timeout=1000)
	public void testClear() throws InterruptedException {
		String test[] = {"1", "2", "3"};
		LinkedList<Integer> data = new LinkedList<>();
		
		dummy.addToLoad(test);
		Thread.sleep(SLEEP_TIME);
		dummy.drainTo(data, 10);
		assertThat(data.size(), is(3));
		
		data = new LinkedList<>();
		
		dummy.addToLoad(test);
		dummy.clear();
		dummy.addToLoad("5");
		dummy.drainTo(data, 10);
		assertThat(data.size(), is(1));
	}

	@Test
	public void testAddToLoadIArray() throws InterruptedException {
		String test[] = {"1", "2", "3"};
		
		dummy.addToLoad(test);
		Thread.sleep(SLEEP_TIME);
		assertThat(dummy.getProcessed(), is(3));
	}

	@Test
	public void testAddToLoadListOfI() throws InterruptedException {
		LinkedList<String> test = new LinkedList<>();
		test.add("1");
		test.add("2");
		test.add("3");

		dummy.addToLoad(test);
		Thread.sleep(SLEEP_TIME);
		assertThat(dummy.getProcessed(), is(3));
	}

	@Test(timeout=200)
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
		Thread.sleep(SLEEP_TIME);
		LinkedList<Integer> result = new LinkedList<>();
		dummy.drainTo(result, 10);
		
		assertThat(result, hasItems(1,2,3));
		assertThat(dummy.getProcessed(), is(3));
	}
}
