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
package com.github.dozedoff.commonj.util;

public class Pair<V, T> {
	private V left;
	private T right;

	public Pair(V left, T right) {
		this.left = left;
		this.right = right;
	}

	public V getLeft() {
		return left;
	}

	public void setLeft(V left) {
		this.left = left;
	}

	public T getRight() {
		return right;
	}

	public void setRight(T right) {
		this.right = right;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair)) {
			return false;
		}

		Pair pair = (Pair) obj;

		if (!pair.getLeft().equals(getLeft())) {
			return false;
		}

		if (!pair.getRight().equals(getRight())) {
			return false;
		}

		return true;
	}
}
