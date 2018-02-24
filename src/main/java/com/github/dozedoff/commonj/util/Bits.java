/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.util;

/**
 * Utility class for bit operations.
 * 
 * @author Nicholas Wright
 *
 */
public class Bits {
	/**
	 * Convenience method to calculate the hamming distance string encoded long
	 * values.
	 * 
	 * @param left
	 *            value
	 * @param right
	 *            value
	 * @return the hamming distance between the two values
	 */
	public static int hammingDistance(String left, String right) {
		return hammingDistance(Long.parseLong(left, 2), Long.parseLong(right, 2));
	}

	/**
	 * Calculate the hamming distance between the left and right value.
	 * 
	 * @param left
	 *            value
	 * @param right
	 *            value
	 * @return the hamming distance between the two values
	 */
	public static int hammingDistance(long left, long right) {
		long xor = left ^ right;
		int distance = Long.bitCount(xor);
		return distance;
	}
}
