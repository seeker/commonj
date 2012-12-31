package com.github.dozedoff.commonj.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for consuming data provided by inputstreams.
 * 
 * Based on "When Runtime.exec() won't" from
 * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html
 */
public class StreamGobbler extends Thread {
	InputStream is;
	final static Logger logger = LoggerFactory.getLogger(StreamGobbler.class);
	private StringBuilder messageBuffer;

	public StreamGobbler(InputStream is) {
		this.is = is;
	}
	
	public String getBuffer() {
		return messageBuffer.toString();
	}

	public void run() {
		messageBuffer = new StringBuilder();

		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null){
				messageBuffer.append(line);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
