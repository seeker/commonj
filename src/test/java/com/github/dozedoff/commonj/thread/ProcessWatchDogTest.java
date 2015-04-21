package com.github.dozedoff.commonj.thread;

import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class ProcessWatchDogTest {
	private Process mockProcess;

	@Before
	public void setup() {
		mockProcess = mock(Process.class);
	}
	
	@Test
	public void testCompleteBeforeTimeout() throws InterruptedException {
		ProcessWatchDog cut = new ProcessWatchDog("test", mockProcess, 10000);
		cut.start();

		Thread.sleep(100);

		cut.interrupt();
	}

	@Test
	public void testWatchdogTimeout() throws InterruptedException {
		ProcessWatchDog cut = new ProcessWatchDog("test", mockProcess, 20);
		cut.start();

		Thread.sleep(100);
	}
}
