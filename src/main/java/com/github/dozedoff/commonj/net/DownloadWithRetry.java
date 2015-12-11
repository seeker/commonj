package com.github.dozedoff.commonj.net;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadWithRetry {
	private static final Logger logger = LoggerFactory.getLogger(DownloadWithRetry.class);
	private GetBinary getBinary;

	public DownloadWithRetry(GetBinary getBinary) {
		this.getBinary = getBinary;
	}
	
	public byte[] download(URL url, int maxRetries) throws IOException {
		long contentLenght = getBinary.getLenght(url);
		ByteBuffer dataBuffer = ByteBuffer.allocate((int)contentLenght);
		
		retry(url, dataBuffer,maxRetries, contentLenght);
		
		dataBuffer.flip();
		byte[] varBuffer = new byte[dataBuffer.limit()];
		dataBuffer.get(varBuffer);
		dataBuffer.clear();
		return varBuffer;
	}
	
	private boolean retry(URL url, ByteBuffer buffer, int maxRetries, long contentLength) throws IOException {
		int failCount = 0;

		while (true) {
			Object[] logData = { url, maxRetries - failCount, buffer.position(), buffer.limit() };
			logger.info("Retrying {}, {} tries left, got {} of {} bytes", logData);

			try {
				failCount++;
				byte[] data = getBinary.getRange(url, buffer.position(), contentLength);
				buffer.put(data);
				failCount--;
			} catch (PageLoadException ple) {
				logger.warn("Failed to load page with {} during retry for {}", ple.getMessage(), url);
			} catch (IOException ioe) {
				logger.warn("Connection issue ({}) during retry for {}", ioe.getMessage(), url);
			}

			if (!buffer.hasRemaining()) {
				// all data received
				break;
			}

			if (failCount >= maxRetries) {
				logger.warn("Out of retries for {}, giving up...", url);
				return false;
			}
		}

		return true;
	}
}
