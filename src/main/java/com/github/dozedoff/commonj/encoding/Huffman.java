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

public class Huffman {
	StringBuffer data = new StringBuffer();
	
	public void addSymbol(char symbol){
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
		
		for(int i=0; i<string.length(); i++){
			entropy.addSymbol(string.charAt(i));
		}
		
		return entropy;
	}
}
