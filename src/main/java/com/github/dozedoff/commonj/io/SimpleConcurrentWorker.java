/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.io;

import java.util.AbstractQueue;

public abstract class SimpleConcurrentWorker<E, T> extends Thread {
	private AbstractQueue<E> input;
	private AbstractQueue<T> output;

	public SimpleConcurrentWorker() {
	}

	public SimpleConcurrentWorker(String name, AbstractQueue<E> input, AbstractQueue<T> output) {
		super(name);
		this.input = input;
		this.output = output;
	}

	@Override
	public final void run() {
		work(this.input, this.output);
	}

	protected abstract void work(AbstractQueue<E> input, AbstractQueue<T> output);
}
