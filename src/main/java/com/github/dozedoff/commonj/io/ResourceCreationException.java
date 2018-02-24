/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.io;

/**
 * This exception is thrown if a resource could not be created.
 */
@SuppressWarnings("serial")
public class ResourceCreationException extends Exception {
	private Exception e;

	/**
	 * Create a new {@link ResourceCreationException}, wrapping the given
	 * {@link Exception}.
	 * 
	 * @param e
	 *            underlying {@link Exception} that caused this exception to be
	 *            thrown
	 */
	public ResourceCreationException(Exception e) {
		this.e = e;
	}

	/**
	 * Get the message for this {@link ResourceCreationException}, using the
	 * message of the cause.
	 * 
	 * @return the message of the {@link ResourceCreationException}
	 */
	@Override
	public String getMessage() {
		return e.getMessage();
	}
}
