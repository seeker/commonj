package com.github.dozedoff.commonj.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.EvictingQueue;

public class Sampler {
	private EvictingQueue<Integer> ringBuffer;
	private AtomicInteger deltaSum = new AtomicInteger();
	private int capacity;

	public Sampler(int size) {
		ringBuffer = EvictingQueue.create(size);
		capacity = size;
	}

	public void addDelta(int delta) {
		deltaSum.addAndGet(delta);
	}

	public void sample() {
		synchronized (ringBuffer) {
			ringBuffer.add(deltaSum.getAndSet(0));
		}
	}

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

	public List<Integer> getSamples() {
		Integer[] samples;

		synchronized (ringBuffer) {
			samples = new Integer[ringBuffer.size()];
			ringBuffer.toArray(samples);
		}

		return Arrays.asList(samples);
	}

	public int getCapacity() {
		return this.capacity;
	}
}
