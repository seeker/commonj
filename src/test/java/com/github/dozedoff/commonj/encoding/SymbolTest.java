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
import static org.junit.Assert.*;

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
