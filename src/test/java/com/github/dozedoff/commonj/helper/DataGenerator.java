/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.helper;

public class DataGenerator {
	/**
	 * Generate a array of the given length with Values between 0 and Byte.MAX_VALUE
	 * 
	 * @param numOfBytes
	 *            the size of the array
	 * @return the array with random values
	 */
	public static byte[] generateRandomByteArray(int numOfBytes) {
		byte[] randomData = new byte[numOfBytes];
		for (int i = 0; i < numOfBytes; i++) {
			randomData[i] = (byte) (Math.random() * Byte.MAX_VALUE);
		}

		return randomData;
	}
}
