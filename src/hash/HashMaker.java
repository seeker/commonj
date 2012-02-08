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
package hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * Generates a SHA-2 Hash for binary data, and formats the value into
 * a Hex representation.
 */
public class HashMaker {
	private static Logger logger = Logger.getLogger(HashMaker.class.getName());

	MessageDigest md = null;
	String key;
	byte[] rawHash;

	/**
	 * Generate a Hash value for binary data.
	 * @param data Binary data
	 * @return hash Hash as a hex value 
	 */
	public String hash(byte[] data){
		if(md == null){
			try {
				md = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e1) {
				logger.severe(e1.getMessage());
				return null;
			}
		}
		
		if(data == null){
			logger.severe("No data");
			return null;
		}
		md.reset();
		rawHash = md.digest(data);
		key = toHex(rawHash);

		return key;
	}

	/**
	 * Converts binary values to Hex.
	 * @param conv binary value to convert to a string.
	 * @return string representation of binary value
	 */
	private String toHex (byte[] conv){
		String str ="";
		String chk = "";
		int tmp = 0;
		for (byte b : conv){
			tmp = b;
			if (tmp <0)
				tmp += 256;

			chk = Integer.toHexString(tmp).toUpperCase();
			if (chk.length() != 2)
				chk = "0"+chk;

			str += chk;
		}
		return str;
	} 
}