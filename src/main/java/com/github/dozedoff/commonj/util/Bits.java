package com.github.dozedoff.commonj.util;

public class Bits {
	public static int hammingDistance(String left, String right) {
		int counter = 0;
		for (int k = 0; k < left.length(); k++) {
			if (left.charAt(k) != right.charAt(k)) {
				counter++;
			}
		}

		return counter;
	}
}
