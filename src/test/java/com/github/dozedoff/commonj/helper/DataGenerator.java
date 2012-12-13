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
package com.github.dozedoff.commonj.helper;

public class DataGenerator {
	/**
	 * Generate a array of the given length with Values
	 * between 0 and Byte.MAX_VALUE
	 * 
	 * @param numOfBytes the size of the array
	 * @return the array with random values
	 */
	public static byte[] generateRandomByteArray(int numOfBytes){
		byte[] randomData = new byte[numOfBytes];
		for(int i = 0; i<numOfBytes; i++){
			randomData[i] = (byte)(Math.random()*Byte.MAX_VALUE);
		}

		return randomData;
	}
}
