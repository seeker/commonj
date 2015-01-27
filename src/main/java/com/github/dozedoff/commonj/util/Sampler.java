package com.github.dozedoff.commonj.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.EvictingQueue;

public class Sampler {
	private EvictingQueue<Integer> ringBuffer;
	private AtomicInteger deltaSum = new AtomicInteger();

	public Sampler(int size) {
		ringBuffer = EvictingQueue.create(size);
	}

	public void addDelta(int delta) {
		deltaSum.addAndGet(delta);
	}

	public void sample() {
		ringBuffer.add(deltaSum.getAndSet(0));
	}

	public double getAverage() {
		if (ringBuffer.isEmpty()) {
			return 0;
		}

		int sum = 0;

		for (int sample : ringBuffer) {
			sum += sample;
		}

		return sum / (double) ringBuffer.size();
	}

	public List<Integer> getSamples() {
		Integer[] samples = new Integer[ringBuffer.size()];
		ringBuffer.toArray(samples);
		return Arrays.asList(samples);
	}
}
