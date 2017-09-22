/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.encoding;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class SymbolTest {
	private Symbol symbol;

	@Before
	public void setup() {
		symbol = new Symbol('a');
	}

	@Test
	public void testGetCount() {
		assertThat(symbol.getCount(), is(1));
	}

	@Test
	public void testInc() {
		symbol.inc();
		assertThat(symbol.getCount(), is(2));
	}
}
