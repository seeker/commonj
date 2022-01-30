/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.EvictingQueue;

/**
 * Used for sampling values. Keeps a number of values, if the capacity is
 * exceeded, the oldest value is discarded. Allows for mathematical operations
 * to be performed on the stored values.
 * 
 * @author Nicholas Wright
 *
 */
public class Sampler {
	private EvictingQueue<Integer> ringBuffer;
	private AtomicInteger deltaSum = new AtomicInteger();
	private int capacity;

	/**
	 * Create a {@link Sampler} with the given size.
	 * 
	 * @param size
	 *            number of values this {@link Sampler} will hold
	 */
	public Sampler(int size) {
		ringBuffer = EvictingQueue.create(size);
		capacity = size;
	}

	/**
	 * Add the value to the current value.
	 * 
	 * @param delta
	 *            value to add
	 */
	public void addDelta(int delta) {
		deltaSum.addAndGet(delta);
	}

	/**
	 * Sample the current value, adding it to the current buffer and discarding
	 * old values if the buffer is too long. The current value is reset to 0.
	 */
	public void sample() {
		synchronized (ringBuffer) {
			ringBuffer.add(deltaSum.getAndSet(0));
		}
	}

	/**
	 * Calculate the average value of all values in the buffer.
	 * 
	 * @return the average of all values
	 */
	public double getAverage() {
		synchronized (ringBuffer) {
			if (ringBuffer.isEmpty()) {
				return 0;
			}

			int sum = 0;

			for (int sample : ringBuffer) {
				sum += sample;
			}

			return sum / (double) ringBuffer.size();
		}
	}

	/**
	 * Get the current samples in the buffer.
	 * 
	 * @return a array containing the samples
	 */
	public List<Integer> getSamples() {
		Integer[] samples;

		synchronized (ringBuffer) {
			samples = new Integer[ringBuffer.size()];
			ringBuffer.toArray(samples);
		}

		return Arrays.asList(samples);
	}

	/**
	 * Get the capacity of the {@link Sampler}
	 * 
	 * @return capacity of this {@link Sampler}
	 */
	public int getCapacity() {
		return this.capacity;
	}
}
