/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.io;

/**
 * This exception is thrown if a resource could not be created.
 */
@SuppressWarnings("serial")
public class ResourceCreationException extends Exception {
	Exception e;

	public ResourceCreationException(Exception e) {
		this.e = e;
	}

	@Override
	public String getMessage() {
		return e.getMessage();
	}
}
