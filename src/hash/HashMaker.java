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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import string.Convert;

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
		key = Convert.byteToHex(rawHash);

		return key;
	}
	
	public String hashFile(File file) {
		if (md == null) {
			try {
				md = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e1) {
				logger.severe(e1.getMessage());
				return null;
			}
		}
		
		if(file == null){
			return null;
		}

		key = null;

		FileInputStream stream;

		stream = null;

		try {
			final FileChannel channel;
			final MappedByteBuffer buffer;
			final int fileSize;

			stream = new FileInputStream(file);
			channel = stream.getChannel();
			buffer = channel.map(MapMode.READ_ONLY, 0, file.length());
			fileSize = (int) file.length();
			
			for (int i = 0; i < fileSize; i++) {
				md.update(buffer.get());
			}

			rawHash = md.digest();
			key = Convert.byteToHex(rawHash);
		} catch (final IOException ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (final IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		return key;
	}
}