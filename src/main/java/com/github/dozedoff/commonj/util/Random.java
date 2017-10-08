/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.util;

/**
 * Util class for generating random data.
 * 
 * @author Nicholas Wright
 *
 */
public class Random {
	/**
	 * Generate a byte array with random values.
	 * 
	 * @param arrayLength
	 *            of the array to create
	 * @return a array with specified length, containing random data
	 */
	public static byte[] createRandomByteArray(int arrayLength) {
		byte[] array = new byte[arrayLength];

		for (int i = 0; i < arrayLength; i++) {
			array[i] = (byte) (127 - (int) (Math.random() * 256));
		}

		return array;
	}
}
