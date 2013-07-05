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
}