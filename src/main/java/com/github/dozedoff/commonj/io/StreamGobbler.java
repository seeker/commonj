/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for consuming data provided by {@link InputStream}s.
 * Based on "When Runtime.exec() won't" from http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
 */
public class StreamGobbler extends Thread {
	private InputStream is;
	private final static Logger LOGGER = LoggerFactory.getLogger(StreamGobbler.class);
	private StringBuilder messageBuffer;

	/**
	 * Create a new {@link StreamGobbler} to consume the {@link InputStream}.
	 * 
	 * @param is
	 *            to consume
	 */
	public StreamGobbler(InputStream is) {
		this.is = is;
		this.messageBuffer = new StringBuilder();
	}

	/**
	 * Get the currently buffered text from the {@link InputStream}.
	 * 
	 * @return all the text that has been buffered so far
	 */
	public String getBuffer() {
		return messageBuffer.toString();
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				messageBuffer.append(line);
			}
		} catch (IOException ioe) {
			LOGGER.error("", ioe);
		}
	}
}
