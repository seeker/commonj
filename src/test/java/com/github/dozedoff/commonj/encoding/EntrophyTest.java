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

public class EntrophyTest {
	Entrophy entrophy;
	
	@Before
	public void setup() {
		entrophy = new Entrophy();
	}
	
	
	@Test
	public void testSetSymbolFilter() {
		entrophy.setSymbolFilter(new TestFilter());
		
		entrophy.addSymbol('a');
		entrophy.addSymbol('b');
		entrophy.addSymbol('c');
		
		assertThat(entrophy.getSymbolCount(), is(1));
	}

	@Test
	public void testAddSymbol() {
		entrophy.addSymbol('a');
		
		assertThat(entrophy.getSymbolCount(), is(1));
	}

	@Test
	public void testProbability() {
		entrophy.addSymbol('a');
		entrophy.addSymbol('b');
		
		assertThat(entrophy.probability('a'), is(0.5));
		assertThat(entrophy.probability('b'), is(0.5));
	}

	@Test
	public void testInformation() {
		for(int i=0; i < 5; i++) {
			entrophy.addSymbol('a');
		}
		
		for(int i=0; i < 5; i++) {
			entrophy.addSymbol('b');
		}
		
		assertThat(entrophy.information('a'), is(1.0));
	}

	@Test
	public void testIsEmpty() {
		assertThat(entrophy.isEmpty(), is(true));
	}
	
	@Test
	public void testIsNotEmpty() {
		entrophy.addSymbol('a');
		assertThat(entrophy.isEmpty(), is(false));
	}

	@Test
	public void testReset() {
		entrophy.addSymbol('a');
		assertThat(entrophy.isEmpty(), is(false));
		assertThat(entrophy.getSymbolCount(), is(1));
		
		entrophy.reset();
		
		assertThat(entrophy.isEmpty(), is(true));
		assertThat(entrophy.getSymbolCount(), is(0));
	}

	@Test
	public void testGetSymbols() {
		final Character[] expected = {'a','j','z'};
		
		entrophy.addSymbol('a');
		entrophy.addSymbol('j');
		entrophy.addSymbol('z');

		List<Character> symbolsCharacters = entrophy.getSymbols();
		
		assertThat(symbolsCharacters, hasItems(expected));
		assertThat(entrophy.getSymbolCount(), is(3));
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
