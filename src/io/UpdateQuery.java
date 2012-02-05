package io;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class UpdateQuery {
	private static final String BUNDLE_NAME = "io.query";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private UpdateQuery() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
