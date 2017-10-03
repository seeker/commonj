/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.settings;

public interface ISettingsValidator {
	/**
	 * Validate the passed {@link AbstractSettings} object.
	 * 
	 * @param settings
	 *            to validate
	 * @return true if the settings object passes all tests
	 */
	public boolean validate(AbstractSettings settings);
}
