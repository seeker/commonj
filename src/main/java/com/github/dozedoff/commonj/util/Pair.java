/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.util;

/**
 * Simple data structure for storing pairs of values. e.g. for nodes in trees.
 * 
 * @author Nicholas Wright
 *
 * @param <V>
 *            left hand value
 * @param <T>
 *            right hand value
 */
public class Pair<V, T> {
	private V left;
	private T right;

	/**
	 * Create a new {@link Pair} with the given values
	 * 
	 * @param left
	 *            left hand value
	 * @param right
	 *            right hand value
	 */
	public Pair(V left, T right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * Get the left hand value of this {@link Pair}
	 * 
	 * @return left hand value
	 */
	public V getLeft() {
		return left;
	}

	/**
	 * Get the right hand value of this {@link Pair}
	 * 
	 * @return right hand value
	 */
	public T getRight() {
		return right;
	}

	/**
	 * Calculate the hash value of this {@link Pair} by using the hash value
	 * from the left and right hand side.
	 * 
	 * @return hash value for this object
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	/**
	 * Check if two pairs are equal by comparing the value of both sides for
	 * equality.
	 * 
	 * @return true if both pairs are equal, else false
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Pair))
			return false;
		Pair other = (Pair) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
}
