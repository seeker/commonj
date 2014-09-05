/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.settings;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

	private static final String TEST_KEY = "test";

	@BeforeClass
	public static void classSetup() throws Exception {
		testProperties = new Properties();
		testProperties.put(TEST_KEY, Integer.toString(42));

		propertiesPath = Files.createTempFile(AbstractSettingsTest.class.getCanonicalName(), "properties");
		Files.newOutputStream(propertiesPath);

		testProperties.store(Files.newOutputStream(propertiesPath), "");
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		Files.deleteIfExists(propertiesPath);
	}

	@Before
	public void setup() {
		validator = mock(ISettingsValidator.class);
		abstractSettings = new AbstractSettings(validator) {
		};
	}

	@Test
	public void testLoadPropertiesNoFile() throws Exception {
		abstractSettings.loadPropertiesFromFile("foo");
		assertThat(abstractSettings.properties.isEmpty(), is(true));
	}

	@Test
	public void testLoadPropertiesFromFileString() throws Exception {
		abstractSettings.loadPropertiesFromFile(propertiesPath.toString());
		assertThat(abstractSettings.properties.containsKey(TEST_KEY), is(true));
	}

	@Test
	public void testLoadPropertiesFromFilePathString() throws Exception {
		abstractSettings.loadPropertiesFromFile(propertiesPath.getParent(), propertiesPath.getFileName().toString());
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
