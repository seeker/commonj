/*  Copyright (C) 2012  Nicholas Wright

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
package net;

import java.io.IOException;

/**
 * This exception is thrown if the page could not be loaded.
 */

public class PageLoadException extends IOException{
	private int responseCode = -1;
	private String url = null;
	
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
	
	public PageLoadException(String message, int responseCode, String url){
		super(message);
		this.responseCode = responseCode;
		this.url = url;
	}
	
	public PageLoadException(int responseCode, String url){
		super();
		this.responseCode = responseCode;
		this.url = url;
	}
	
	public int getResponseCode(){
		return responseCode;
	}
	
	public String getUrl(){
		return url;
	}
}
