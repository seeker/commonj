/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.time;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Class that to call a given method at a regular interval.
 * 
 * @author Nicholas Wright
 *
 */
public abstract class Ticker {
	private Timer timer;

	/**
	 * Create a new {@link Ticker} that will execute it's {@link Ticker#tickEvent()} in the provided interval. The
	 * {@link Ticker}s internal timer thread will be named 'Timer'.
	 * 
	 * @param time
	 *            for the interval
	 * @param timeUnit
	 *            for the interval
	 */
	public Ticker(long time, TimeUnit timeUnit) {
		createTimer("Timer", time, timeUnit);
	}

	/**
	 * Create a new {@link Ticker} that will execute it's {@link Ticker#tickEvent()} in the provided interval.
	 * 
	 * @param tickerName
	 *            name for the {@link Ticker}s internal timer thread
	 * @param time
	 *            for the interval
	 * @param timeUnit
	 *            for the interval
	 */
	public Ticker(String tickerName, long time, TimeUnit timeUnit) {
		createTimer(tickerName, time, timeUnit);
	}

	/**
	 * This Method is executed on each interval. Override this method to provide code to be executed at the intervals.
	 */
	abstract public void tickEvent();

	private void createTimer(String name, long time, TimeUnit timeUnit) {
		timer = new Timer(name);
		timer.scheduleAtFixedRate(createTickTask(), 0, convertToMiliseconds(time, timeUnit));
	}

	private long convertToMiliseconds(long time, TimeUnit timeUnit) {
		return TimeUnit.MILLISECONDS.convert(time, timeUnit);
	}

	private TimerTask createTickTask() {
		return new TimerTask() {
			@Override
			public void run() {
				tickEvent();
			}
		};
	}

	/**
	 * Cancel the execution of the {@link Ticker}. Calls the underlying {@link Timer}s {@link Timer#cancel()} method.
	 */
	public void cancel() {
		timer.cancel();
	}
}
