/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.settings;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AbstractSettingsTest {
	private ISettingsValidator validator;
	private AbstractSettings abstractSettings;
	private static Properties testProperties;
	private static Path propertiesPath;
	private static Path filename;

	private static final String TEST_KEY = "test";
	private static final String TEST_KEY_UNKNOWN = "foo";

	private static OutputStream outStream;

	private static final int TEST_VALUE = 42;

	@BeforeClass
	public static void classSetup() throws Exception {
		testProperties = new Properties();
		testProperties.put(TEST_KEY, Integer.toString(TEST_VALUE));

		propertiesPath = Files.createTempFile(AbstractSettingsTest.class.getCanonicalName(), "properties");
		filename = propertiesPath.getFileName();

		outStream = Files.newOutputStream(propertiesPath);

		testProperties.store(outStream, "");
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		outStream.close();
		Files.deleteIfExists(propertiesPath);
	}

	@Before
	public void setUp() {
		validator = mock(ISettingsValidator.class);
		abstractSettings = new AbstractSettings(validator) {
		};
	}

	@Test
	public void testLoadPropertiesNoFile() throws Exception {
		abstractSettings.loadPropertiesFromFile(TEST_KEY_UNKNOWN);
		assertThat(abstractSettings.properties.isEmpty(), is(true));
	}

	@Test
	public void testLoadPropertiesFromFileString() throws Exception {
		abstractSettings.loadPropertiesFromFile(propertiesPath.toString());
		assertThat(abstractSettings.properties.containsKey(TEST_KEY), is(true));
	}

	@Test
	public void testLoadPropertiesFromFilePathString() throws Exception {
		abstractSettings.loadPropertiesFromFile(propertiesPath.getParent(), filename.toString());
		assertThat(abstractSettings.properties.containsKey(TEST_KEY), is(true));
	}

	@Test
	public void testLoadPropertiesFromFileAbsolute() throws Exception {
		abstractSettings.loadPropertiesFromFileAbsolute(propertiesPath);
		assertThat(abstractSettings.properties.containsKey(TEST_KEY), is(true));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testValidateSettingsNoValidator() throws Exception {
		abstractSettings = new AbstractSettings(null) {
		};
		abstractSettings.validateSettings();
	}

	@Test
	public void testValidateSettingsValid() throws Exception {
		when(validator.validate(abstractSettings)).thenReturn(true);
		assertThat(abstractSettings.validateSettings(), is(true));
	}

	@Test
	public void testSaveSettings() throws Exception {
		abstractSettings.loadPropertiesFromFileAbsolute(propertiesPath);
		abstractSettings.properties.put("foo", "bar");
		abstractSettings.saveSettings();

		AbstractSettings as = new AbstractSettings(validator) {
		};

		// guard
		assertThat(as.properties.containsKey("foo"), is(false));
		as.loadPropertiesFromFileAbsolute(propertiesPath);

		assertThat(as.properties.containsKey("foo"), is(true));
	}
}
