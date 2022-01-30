/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.net;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URL;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Before;
import org.junit.Test;

public class DownloadItemTest {
	private DownloadItem cut;

	private static final String URL_STRING = "http://example.com";
	private static final String FILE_NAME = "test.txt";

	@Before
	public void setUp() throws Exception {
		cut = new DownloadItem(new URL(URL_STRING), FILE_NAME);
	}

	@Test
	public void testHashCode() throws Exception {
		DownloadItem cut2 = new DownloadItem(new URL(URL_STRING), FILE_NAME);

		assertThat(cut.hashCode(), is(cut2.hashCode()));
	}

	@Test
	public void testGetImageUrl() throws Exception {
		assertThat(cut.getImageUrl(), is(new URL(URL_STRING)));
	}

	@Test
	public void testGetImageName() throws Exception {
		assertThat(cut.getImageName(), is(FILE_NAME));
	}

	@Test
	public void testEquals() throws Exception {
		EqualsVerifier.forClass(DownloadItem.class).withPrefabValues(URL.class,new URL(URL_STRING), new URL(URL_STRING + "/about")).suppress(Warning.STRICT_INHERITANCE).verify();
	}
}
