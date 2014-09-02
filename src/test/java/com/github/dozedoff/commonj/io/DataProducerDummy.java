/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.io;

public class DataProducerDummy extends DataProducer<String, Integer> {
	private int processed = 0;

	@Override
	protected void loaderDoWork() throws InterruptedException {
		String string;

		string = input.take();
		int next = Integer.parseInt(string);
		processed++;
		output.add(next);
	}

	public int getProcessed() {
		return processed;
	}
}
