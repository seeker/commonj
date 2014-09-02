/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.encoding;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class EntropyTest {
	Entropy entropy;

	@Before
	public void setup() {
		entropy = new Entropy();
	}

	@Test
	public void testSetSymbolFilter() {
		entropy.setSymbolFilter(new TestFilter());

		entropy.addSymbol('a');
		entropy.addSymbol('b');
		entropy.addSymbol('c');

		assertThat(entropy.getSymbolCount(), is(1));
	}

	@Test
	public void testAddSymbol() {
		entropy.addSymbol('a');

		assertThat(entropy.getSymbolCount(), is(1));
	}

	@Test
	public void testProbability() {
		entropy.addSymbol('a');
		entropy.addSymbol('b');

		assertThat(entropy.probability('a'), is(0.5));
		assertThat(entropy.probability('b'), is(0.5));
	}

	@Test
	public void testInformation() {
		for (int i = 0; i < 5; i++) {
			entropy.addSymbol('a');
		}

		for (int i = 0; i < 5; i++) {
			entropy.addSymbol('b');
		}

		assertThat(entropy.information('a'), is(1.0));
	}

	@Test
	public void testIsEmpty() {
		assertThat(entropy.isEmpty(), is(true));
	}

	@Test
	public void testIsNotEmpty() {
		entropy.addSymbol('a');
		assertThat(entropy.isEmpty(), is(false));
	}

	@Test
	public void testReset() {
		entropy.addSymbol('a');
		assertThat(entropy.isEmpty(), is(false));
		assertThat(entropy.getSymbolCount(), is(1));

		entropy.reset();

		assertThat(entropy.isEmpty(), is(true));
		assertThat(entropy.getSymbolCount(), is(0));
	}

	@Test
	public void testGetSymbols() {
		final Character[] expected = { 'a', 'j', 'z' };

		entropy.addSymbol('a');
		entropy.addSymbol('j');
		entropy.addSymbol('z');

		List<Character> symbolsCharacters = entropy.getSymbols();

		assertThat(symbolsCharacters, hasItems(expected));
		assertThat(entropy.getSymbolCount(), is(3));
	}

	class TestFilter implements SymbolFilter {
		@Override
		public boolean accept(char symbol) {
			if (symbol == 'a') {
				return true;
			} else {
				return false;
			}
		}
	}
}
