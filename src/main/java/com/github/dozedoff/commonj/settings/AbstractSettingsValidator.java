/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSettingsValidator implements ISettingsValidator {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractSettingsValidator.class);
	private boolean allOk = true;

	protected void checkGreaterZero(Object param, int value) {
		if (!(value > 0)) {
			setOk(false);
			logger.warn("Value for {} must be greater than 0, currently set to {}", param.toString(), value);
		}
	}

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

	protected boolean getOkState() {
		return allOk;
	}
}
