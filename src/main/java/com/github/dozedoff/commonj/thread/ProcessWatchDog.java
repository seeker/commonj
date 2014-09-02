/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessWatchDog extends Thread {
	String processDescription;
	Process process;
	long timeout;
	final static Logger logger = LoggerFactory.getLogger(ProcessWatchDog.class);

	public ProcessWatchDog(String processDescription, Process process, long timeout) {
		this.process = process;
		this.timeout = timeout;
		this.processDescription = processDescription;
	}

	@Override
	public void run() {
		try {
			sleep(timeout);
		} catch (InterruptedException e) {
			return; // all is well
		}

		logger.error("Process timed out. Description: " + processDescription);
		process.destroy();
	}
}
