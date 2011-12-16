package io;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GetHtmlTest {
String refData = "<html><head><title>Test Page</title></head><body><p>Test Page</p></body></html>";
String testString = null;

	@Test
	public void testGetString() throws Exception {
		GetHtml getHtml = new GetHtml();
		
		testString = getHtml.get("https://home.zhaw.ch/~wrighnic/test.html");
		//System.out.println(testString);
		assertTrue(testString.equals(refData));
	}

}
