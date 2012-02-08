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
package io;

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
	public String getMessage(){
		return e.getMessage();
	}
}
