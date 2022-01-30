/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.hash;

import java.io.BufferedInputStream;
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
	private MessageDigest md;

	/**
	 * Create a new {@link HashMaker} with the default (SHA-256) algorithm.
	 */
	public HashMaker() {
		createDigest(DEFAULT_ALGORITHM);
	}

	/**
	 * Create a new {@link HashMaker} with the specified algorithm.
	 * 
	 * @param algorithm
	 *            to use for hashing
	 */
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
	 * Calculate the hash for the provided binary data, represented as a hex string.
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

	/**
	 * Calculate the hash for the provided path, represented as a hex string.
	 * 
	 * @param file
	 *            to read and hash
	 * @return the hash of the file or null if there was an error
	 */
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
}
