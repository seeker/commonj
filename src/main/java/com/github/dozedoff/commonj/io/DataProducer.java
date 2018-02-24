/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.io;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

// TODO mark this class as deprecated 

public abstract class DataProducer<I, O> {
	protected LinkedBlockingQueue<I> input = new LinkedBlockingQueue<>();
	protected LinkedBlockingQueue<O> output = new LinkedBlockingQueue<>();

	private LinkedList<DataLoader> loaders = new LinkedList<>();
	private int threadPriority = Thread.NORM_PRIORITY;

	public DataProducer() {
		startLoader();
	}

	public DataProducer(int outputQueueMaxSize) {
		output = new LinkedBlockingQueue<>(outputQueueMaxSize);

		startLoader();
	}

	public DataProducer(int inputQueueMaxSize, int outputQueueMaxSize) {
		input = new LinkedBlockingQueue<>(inputQueueMaxSize);
		output = new LinkedBlockingQueue<>(outputQueueMaxSize);

		startLoader();
	}

	public final void startLoader() {
		startLoader(1);
	}

	public void setThreadPriority(int priority) {
		threadPriority = priority;

		for (Thread t : loaders) {
			t.setPriority(threadPriority);
		}
	}

	public void startLoader(int numberOfLoaders) {
		for (int i = 0; i < numberOfLoaders; i++) {
			DataLoader loader = new DataLoader();
			loaders.add(loader);
			loader.setDaemon(true);
			loader.setPriority(threadPriority);
			loader.start();
		}
	}

	public void stopLoader() {
		stopLoader(1);
	}

	public void stopLoader(int numberOfLoaders) {
		for (int i = 0; i < numberOfLoaders; i++) {
			if (loaders.size() > 1) {
				DataLoader loader = loaders.removeLast();
				loader.interrupt();
			}
		}
	}

	public void clear() {
		input.clear();
		output.clear();
		outputQueueChanged();
	}

	public void addToLoad(@SuppressWarnings("unchecked") I... paths) {
		List<I> list = Arrays.asList(paths);
		input.addAll(list);
	}

	public void offer(I input) {
		this.input.offer(input);
	}

	public void addToLoad(List<I> paths) {
		input.addAll(paths);
	}

	public O takeData() throws InterruptedException {
		O take = output.take();
		outputQueueChanged();
		return take;
	}

	public void drainTo(Collection<O> drainTo, int maxElements) throws InterruptedException {
		output.drainTo(drainTo, maxElements);
		outputQueueChanged();
	}

	public int availableWork() {
		return output.size();
	}

	public boolean hasWork() {
		return (!input.isEmpty()) || (!output.isEmpty());
	}

	abstract protected void loaderDoWork() throws InterruptedException;

	protected void outputQueueChanged() {
	};

	class DataLoader extends Thread {

		public DataLoader() {
			this.setName("Data loader");
		}

		@Override
		public void run() {
			while (!isInterrupted()) {
				try {
					loaderDoWork();
					outputQueueChanged();
				} catch (InterruptedException e) {
					interrupt();
				}
			}
		}
	}
}
