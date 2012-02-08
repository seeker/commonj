/*  Copyright (C) 2012  Nicholas Wright

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
package net;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GetHtmlTest {
String refData = "<html><head><title>Test Page</title></head><body><p>Test Page</p></body></html>";
String testString = null;

	@Test
	public void testGetString() throws Exception {
		GetHtml getHtml = new GetHtml();
		
		testString = getHtml.get("https://home.zhaw.ch/~wrighnic/test.html");
		assertTrue(testString.equals(refData));
	}
}
