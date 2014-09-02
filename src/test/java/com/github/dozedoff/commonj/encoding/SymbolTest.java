/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.encoding;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class SymbolTest {
	Symbol symbol;

	@Before
	public void setup() {
		symbol = new Symbol('a');
	}

	@Test
	public void testGetCount() {
		assertThat(symbol.count, is(1));
	}

	@Test
	public void testInc() {
		symbol.inc();
		assertThat(symbol.count, is(2));
	}
}
