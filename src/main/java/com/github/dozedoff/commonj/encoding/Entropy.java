/*  Copyright (C) 2013  Nicholas Wright

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

	//TODO move this somewhere else
//	public void printStats(int symbols) {
//		calcStats(symbols);
//		System.out.printf("%c : %d (P: %.4f) (I: %.4f)\n", character, count,
//				probability, information);
//	}
	
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