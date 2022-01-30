/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for monitoring if a {@link Process} terminates within a set time. The
 * thread must be started and interrupted manually.
 * 
 * @author Nicholas Wright
 *
 */
public class ProcessWatchDog extends Thread {
	private String processDescription;
	private Process process;
	private long timeout;
	private final static Logger logger = LoggerFactory.getLogger(ProcessWatchDog.class);

	/**
	 * Create a new {@link ProcessWatchDog} to monitor a {@link Process}.
	 * 
	 * @param processDescription
	 *            a descriptive name, used for logging
	 * @param process
	 *            to monitor
	 * @param timeout
	 *            timeout in milliseconds
	 */
	public ProcessWatchDog(String processDescription, Process process, long timeout) {
		this.process = process;
		this.timeout = timeout;
		this.processDescription = processDescription;
	}

	/**
	 * Running the thread starts the {@link ProcessWatchDog}. If the thread is
	 * not interrupted before the timeout is reached, a error is logged and the
	 * process terminated.
	 */
	@Override
	public void run() {
		try {
			sleep(timeout);
		} catch (InterruptedException e) {
			return; // all is well
		}

		logger.error("Process {} timed out.", processDescription);
		process.destroy();
	}
}
