/*  Copyright (C) 2012  Nicholas Wright

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
	 * Converts byte values to Hex. (replaced with a better version from
	 * http://stackoverflow.com/a/9855338/891292 )
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
	 * Converts the given String to boolean using
	 * {@link Boolean#parseBoolean(String)}. In case of an error, the default
	 * value is returned.
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
