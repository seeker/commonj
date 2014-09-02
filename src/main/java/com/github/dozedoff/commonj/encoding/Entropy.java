/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.encoding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Entropy {
	private HashMap<Character, Symbol> symbols = new HashMap<>();
	private int symbolCount;
	private SymbolFilter symbolFilter = null;

	public void setSymbolFilter(SymbolFilter filter) {
		this.symbolFilter = filter;
	}

	public boolean addSymbol(char symbol) {
		if (symbolFilter == null) {
			insertSymbol(symbol);
			return true;
		}

		if (symbolFilter.accept(symbol)) {
			insertSymbol(symbol);
			return true;
		} else {
			return false;
		}
	}

	private void insertSymbol(char symbol) {
		symbolCount++;

		if (symbols.containsKey(symbol)) {
			symbols.get(symbol).inc();
		} else {
			symbols.put(symbol, new Symbol(symbol));
		}
	}

	public double probability(char symbol) {
		Symbol symbolData = symbols.get(symbol);

		if (symbolData == null) {
			return 0.0;
		}

		return (double) symbolData.getCount() / (double) symbolCount;
	}

	public double information(char symbol) {
		Symbol symbolData = symbols.get(symbol);

		if (symbolData == null) {
			return 0.0;
		}

		return Math.log(1.0 / probability(symbol)) / Math.log(2.0);
	}

	// TODO move this somewhere else
	// public void printStats(int symbols) {
	// calcStats(symbols);
	// System.out.printf("%c : %d (P: %.4f) (I: %.4f)\n", character, count,
	// probability, information);
	// }

	public int getSymbolCount() {
		return symbolCount;
	}

	public boolean isEmpty() {
		return symbols.isEmpty();
	}

	public void reset() {
		symbols = new HashMap<>();
		symbolCount = 0;
	}

	public List<Character> getSymbols() {
		List<Character> symbolList = new ArrayList<>();
		symbolList = new ArrayList<>(symbols.keySet());

		return symbolList;
	}
}