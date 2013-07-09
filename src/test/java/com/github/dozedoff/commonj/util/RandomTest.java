/*  Copyright (C) 2013  Nicholas Wright

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
package com.github.dozedoff.commonj.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

public class RandomTest {
	private Byte[] expectedValues = { -128, 0, 127 };
	private final int SAMPLE_SIZE = 100000;

	private ArrayList<Byte> arrayToList(byte[] data) {
		ArrayList<Byte> dataList = new ArrayList<>(data.length);
		for (int i = 0; i < data.length; i++) {
			dataList.add(new Byte(data[i]));
		}
		return dataList;
	}

	@BeforeClass
	public static void setupBefore() {
		new Random();
	}

	@Test
	public void testCreateRandomByteArraySize() {
		final int SIZE = 5;

		byte[] data = Random.createRandomByteArray(SIZE);
		assertThat(data.length, is(SIZE));
	}

	@Test(expected = NegativeArraySizeException.class)
	public void testCreateRandomByteArrayNegativeSize() {
		final int SIZE = -5;
		Random.createRandomByteArray(SIZE);
	}

	@Test
	public void testCreateRandomByteArrayValueRange() {
		byte[] data = Random.createRandomByteArray(SAMPLE_SIZE);
		ArrayList<Byte> dataList = arrayToList(data);

		assertThat(dataList, hasItems(expectedValues));
	}
}
