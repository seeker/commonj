/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.encoding;

public class Symbol {
	private int count = 1;
	private char character;

	public Symbol(char character) {
		this.character = character;
	}

	public int getCount() {
		return count;
	}

	public void inc() {
		count++;
	}
}