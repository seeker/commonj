/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.string;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience class for converting strings and other types.
 */
public class Convert {
	private static Logger logger = LoggerFactory.getLogger(Convert.class);
	final static char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * Converts byte values to Hex. (replaced with a better version from http://stackoverflow.com/a/9855338/891292 )
	 * 
	 * @param bytes
	 *            binary value to convert to a string.
	 * @return Hex representation of byte values
	 */
	public static String byteToHex(byte... bytes) {
		if (bytes == null) {
			return null;
		}

		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static int stringToInt(String value, int defaultValue) {
		int converted = defaultValue;

		if (value == null) {
			return defaultValue;
		}

		try {
			converted = Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			logger.debug("Unable to convert String {} to int, returning default ({})", value, defaultValue);
		}

		return converted;
	}

	/**
	 * Converts the given String to boolean using {@link Boolean#parseBoolean(String)}. In case of an error, the default value is returned.
	 * 
	 * @param value
	 *            to convert
	 * @param defaultValue
	 *            to use in case of an error
	 * @return the boolean representation of the string
	 */
	public static boolean stringToBoolean(String value, boolean defaultValue) {
		boolean converted = defaultValue;

		if (value == null) {
			return defaultValue;
		}

		try {
			converted = Boolean.parseBoolean(value);
		} catch (NumberFormatException nfe) {
			logger.debug("Unable to convert String {} to boolean, returning default ({})", value, defaultValue);
		}

		return converted;
	}
}
