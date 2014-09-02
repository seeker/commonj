/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
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
