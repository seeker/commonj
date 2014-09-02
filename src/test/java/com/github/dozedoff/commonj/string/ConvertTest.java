/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.string;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

public class ConvertTest {

	@BeforeClass
	public static void createInstance() {
		new Convert(); // Used to get 100% coverage for static only classes
	}

	@Test
	public final void testByteToHexNull() {
		String hex = Convert.byteToHex(null);
		assertNull(hex);
	}

	@Test
	public final void testByteToHex00() {
		String hex = Convert.byteToHex(Byte.decode("0"));
		assertThat(hex, is("00"));
	}

	@Test
	public final void testByteToHex01() {
		String hex = Convert.byteToHex(Byte.decode("1"));
		assertThat(hex, is("01"));
	}

	@Test
	public final void testByteToHexFF() {
		String hex = Convert.byteToHex(Byte.decode("-1"));
		assertThat(hex, is("FF"));
	}

	@Test
	public final void testByteToHexAA() {
		String hex = Convert.byteToHex(Byte.decode("-86"));
		assertThat(hex, is("AA"));
	}

	@Test
	public final void testByteToHexC0() {
		String hex = Convert.byteToHex(Byte.decode("-64"));
		assertThat(hex, is("C0"));
	}

	@Test
	public final void testStringToIntNull() {
		int value = Convert.stringToInt(null, 3);
		assertThat(value, is(3));
	}

	@Test
	public final void testStringToIntEmpty() {
		int value = Convert.stringToInt("", 3);
		assertThat(value, is(3));
	}

	@Test
	public final void testStringToIntNaN() {
		int value = Convert.stringToInt("jakdfhakjh", 3);
		assertThat(value, is(3));
	}

	@Test
	public final void testStringToIntFloat() {
		int value = Convert.stringToInt("2.56", 3);
		assertThat(value, is(3));
	}

	@Test
	public final void testStringToInt() {
		int value = Convert.stringToInt("5", 3);
		assertThat(value, is(5));
	}

	@Test
	public final void testStringToIntNegative() {
		int value = Convert.stringToInt("-7", 3);
		assertThat(value, is(-7));
	}

	@Test
	public void testStringToBooleanNullDefaultFalse() {
		boolean value = Convert.stringToBoolean(null, false);
		assertThat(value, is(false));
	}

	@Test
	public void testStringToBooleanNullDefaultTrue() {
		boolean value = Convert.stringToBoolean(null, true);
		assertThat(value, is(true));
	}

	@Test
	public void testStringToBooleanAllCaps() {
		boolean value = Convert.stringToBoolean("TRUE", false);
		assertThat(value, is(true));
	}

	@Test
	public void testStringToBooleanTrueCamelCase() {
		boolean value = Convert.stringToBoolean("TrUe", false);
		assertThat(value, is(true));
	}

	@Test
	public void testStringToBooleanFalseCamelCase() {
		boolean value = Convert.stringToBoolean("fAlSE", true);
		assertThat(value, is(false));
	}

	@Test
	public void testStringToBooleanEmpty() {
		boolean value = Convert.stringToBoolean("", true);
		assertThat(value, is(false));
	}

	@Test
	public void testStringToBooleanInvalid() {
		boolean value = Convert.stringToBoolean("foo", true);
		assertThat(value, is(false));
	}
}