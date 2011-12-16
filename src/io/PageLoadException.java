/*  Copyright (C) 2011  Nicholas Wright
	
	part of 'Aid', an imageboard downloader.

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
package io;

/**
 * This exception is thrown if the page could not be loaded.
 */

public class PageLoadException extends Exception{
	int responseCode = -1;
	private static final long serialVersionUID = 1L;
	
	public PageLoadException(String message){
		super(message);
	}
	
	public PageLoadException(){
		super();
	}
	
	public PageLoadException(int responseCode){
		super();
		this.responseCode = responseCode;
	}
	
	public PageLoadException(String message, int responseCode){
		super(message);
		this.responseCode = responseCode;
	}
	
	public int getResponseCode(){
		return responseCode;
	}
	
}
