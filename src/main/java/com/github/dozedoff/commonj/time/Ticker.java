package com.github.dozedoff.commonj.time;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public abstract class Ticker {
	private Timer timer;

	public Ticker(long time, TimeUnit timeUnit) {
		createTimer("Timer", time, timeUnit);
	}

	public Ticker(String tickerName, long time, TimeUnit timeUnit) {
		createTimer(tickerName, time, timeUnit);
	}

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

	public void cancel() {
		timer.cancel();
	}
}
