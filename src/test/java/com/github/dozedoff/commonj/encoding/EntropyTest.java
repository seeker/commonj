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
package com.github.dozedoff.commonj.encoding;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.hasItems;

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
		for(int i=0; i < 5; i++) {
			entropy.addSymbol('a');
		}
		
		for(int i=0; i < 5; i++) {
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
		final Character[] expected = {'a','j','z'};
		
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
			if(symbol == 'a') {
				return true;
			} else {
				return false;
			}
		}
	}
}
