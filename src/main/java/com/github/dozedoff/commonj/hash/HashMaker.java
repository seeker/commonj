/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.hash;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dozedoff.commonj.string.Convert;

/**
 * Generates a SHA-2 Hash (default) for binary data, and formats the value into a Hex representation.
 */
public class HashMaker {
	private static Logger logger = LoggerFactory.getLogger(HashMaker.class);
	private static final String DEFAULT_ALGORITHM = "SHA-256";
	MessageDigest md = null;

	public HashMaker() {
		createDigest(DEFAULT_ALGORITHM);
	}

	public HashMaker(String algorithm) {
		createDigest(algorithm);
	}

	private void createDigest(String algorithm) {
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e1) {
			logger.error("Unable to find Algorithm {}, falling back to {}", algorithm, DEFAULT_ALGORITHM);
			createDigest(DEFAULT_ALGORITHM);
		}
	}

	/**
	 * Generate a Hash value for binary data.
	 * 
	 * @param data
	 *            Binary data
	 * @return hash Hash as a hex value
	 */
	public String hash(byte[] data) {
		if (data == null) {
			logger.error("No data");
			return null;
		}

		md.update(data);
		byte[] rawHash = md.digest();

		return Convert.byteToHex(rawHash);
	}

	public String hashFile(Path file) {
		byte[] rawHash;

		if (file == null || !Files.exists(file)) {
			return null;
		}

		try (InputStream stream = new BufferedInputStream(Files.newInputStream(file))) {
			while (true) {
				int data = stream.read();

				if (data == -1) {
					break;
				}

				md.update((byte) data);
			}

			rawHash = md.digest();

			return Convert.byteToHex(rawHash);
		} catch (IOException e) {
			logger.error("Failed to generate hash for {}, reason: {}", file, e);
		}

		return null;
	}

	/**
	 * Use {@link HashMaker#hashFile(Path)} instead.
	 * 
	 * @param file
	 *            to hash
	 * @return Hexadecimal encoded hash
	 */
	// TODO REMOVE after 0.1.1
	@Deprecated
	public String hashFile(File file) {
		if (file == null) {
			return null;
		}

		return hashFile(file.toPath());
	}
}
