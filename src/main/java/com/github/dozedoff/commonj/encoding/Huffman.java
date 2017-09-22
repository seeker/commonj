/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.encoding;

public class Huffman {
	private StringBuffer data = new StringBuffer();

	public void addSymbol(char symbol) {
		data.append(symbol);
	}

	public void addData(String data) {
		this.data.append(data);
	}

	public String digest() {
		Entropy entropy = calcSymbolStats();

		return null;
	}

	private Entropy calcSymbolStats() {
		Entropy entropy = new Entropy();

		String string = data.toString();

		for (int i = 0; i < string.length(); i++) {
			entropy.addSymbol(string.charAt(i));
		}

		return entropy;
	}
}
