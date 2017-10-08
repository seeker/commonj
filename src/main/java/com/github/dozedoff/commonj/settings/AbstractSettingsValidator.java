/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validator for {@link AbstractSettings}.
 * 
 * @author Nicholas Wright
 *
 */
public abstract class AbstractSettingsValidator implements ISettingsValidator {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractSettingsValidator.class);
	private boolean allOk = true;

	/**
	 * Check if a value is greater than zero. Logs a warning if the check fails.
	 * 
	 * @param param
	 *            name of the parameter, used for logging
	 * @param value
	 *            to check
	 */
	protected void checkGreaterZero(Object param, int value) {
		if (!(value > 0)) {
			setOk(false);
			logger.warn("Value for {} must be greater than 0, currently set to {}", param.toString(), value);
		}
	}

	/**
	 * Check if a value is in the specified range. Logs a warning if the check
	 * fails.
	 * 
	 * @param param
	 *            name of the parameter, used for logging
	 * @param value
	 *            to check
	 * @param min
	 *            lower bound, inclusive
	 * @param max
	 *            upper bound, inclusive
	 */
	protected void checkRange(Object param, int value, int min, int max) {
		if (max < min) {
			throw new IllegalArgumentException("Upper bound must be equal or greater than the lower bound.");
		}

		if (!((value >= min) && (value <= max))) {
			setOk(false);
			Object loggerData[] = { param.toString(), min, max, value };
			logger.warn("Value for {} must be between {} and {}, currently set to {}", loggerData);
		}
	}

	private void setOk(boolean ok) {
		allOk &= ok;
	}

	/**
	 * Check if the {@link AbstractSettings} has passed all checks without
	 * errors.
	 * 
	 * @return true if all checks are ok
	 */
	protected boolean getOkState() {
		return allOk;
	}
}
