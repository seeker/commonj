/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@SuppressFBWarnings("FCBL_FIELD_COULD_BE_LOCAL")
public class RandomTest {
	private Byte[] expectedValues = { -128, 0, 127 };
	private static final int SAMPLE_SIZE = 100000;

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
