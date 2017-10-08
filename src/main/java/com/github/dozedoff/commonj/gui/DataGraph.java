/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.gui;

import java.awt.Graphics;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import com.github.dozedoff.commonj.time.Ticker;
import com.github.dozedoff.commonj.util.Sampler;

/**
 * Component for displaying {@link Graph}s. Repaints itself and the
 * {@link Graph} at regular intervals.
 * 
 * @author Nicholas Wright
 *
 */
public class DataGraph extends JPanel {
	private static final long serialVersionUID = 1L;
	private int updateInterval = 1;
	private TimeUnit timeUnit;
	private int noOfColums = 20;
	private int columWidth = 2;
	private transient Ticker updater;
	private transient Sampler sampler;
	private boolean autoscale;
	private transient Graph graph;

	/**
	 * Create a new {@link DataGraph} for the given {@link Graph}. The
	 * {@link DataGraph} will update the {@link Graph} at the specified
	 * interval.
	 * 
	 * @param graph
	 *            to update
	 * @param interval
	 *            value of the interval
	 * @param timeUnit
	 *            unit of the interval
	 */
	public DataGraph(Graph graph, int interval, TimeUnit timeUnit) {
		this.graph = graph;
		this.updateInterval = interval;
		this.timeUnit = timeUnit;
	}

	/**
	 * Start the {@link DataGraph}, refreshing the displayed data at the set
	 * interval.
	 */
	public void start() {
		if (updater != null) {
			return;
		}

		updater = new Ticker("DataGraph updater", updateInterval, timeUnit) {
			@Override
			public void tickEvent() {
				updateGui();
			}
		};
	}

	/**
	 * Update the {@link DataGraph} and the underlying {@link Graph}.
	 * 
	 * @param g
	 *            {@inheritDoc}
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		graph.update(g);
	}

	private void updateGui() {
		sampler.sample();
		repaint();
	}
}