/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.util;

public class Bits {
	public static int hammingDistance(String left, String right) {
		return hammingDistance(Long.parseLong(left, 2), Long.parseLong(right, 2));
	}

	public static int hammingDistance(long left, long right) {
		long xor = left ^ right;
		int distance = Long.bitCount(xor);
		return distance;
	}
}
