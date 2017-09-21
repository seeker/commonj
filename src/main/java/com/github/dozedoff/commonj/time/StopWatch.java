/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.time;

import java.util.Calendar;

public class StopWatch {
	final String format = "%1$02d:%2$02d:%3$02d.%4$03d";
	boolean isRunning = false;
	long startTime, stopTime;
	private final Calendar calendar;

	final long CONST_H = 3600000;
	final long CONST_M = 60000;
	final long CONST_S = 1000;

	/**
	 * Use {@link StopWatch#StopWatch(Calendar)} instead.
	 */
	@Deprecated
	public StopWatch() {
		calendar = Calendar.getInstance();
	}

	public StopWatch(Calendar calendar) {
		this.calendar = calendar;
	}

	public boolean start() {
		if (!isRunning) {
			startTime = getCurrentTime();
			isRunning = true;
		}

		return isRunning;
	}

	public boolean stop() {
		stopTime = getCurrentTime();
		isRunning = false;
		return isRunning;
	}

	public String getTime() {
		return convertTime(getTimeMilli());
	}

	public long getTimeMilli() {
		if (isRunning) {
			return getCurrentTime() - startTime;
		} else {
			return stopTime - startTime;
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void reset() {
		isRunning = false;
		startTime = 0;
		stopTime = 0;
	}

	private long getCurrentTime() {
		return this.calendar.getTimeInMillis();
	}

	protected String convertTime(long time) {

		if (time == 0) {
			return "00:00:00.000";
		}

		if (time < 0) {
			return "--:--:--.---";
		}

		long remainder, hours, minutes, seconds, milliSec;

		hours = time / CONST_H;
		remainder = time - (hours * CONST_H);

		minutes = remainder / CONST_M;
		remainder = remainder - (minutes * CONST_M);

		seconds = remainder / CONST_S;
		remainder = remainder - (seconds * CONST_S);

		milliSec = remainder;

		return String.format(format, hours, minutes, seconds, milliSec);
	}
}
