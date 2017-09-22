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

public class DataGraph extends JPanel {
	/**
	 * Shows data over time
	 */
	private static final long serialVersionUID = 1L;
	private int updateInterval = 1;
	private TimeUnit timeUnit;
	private int noOfColums = 20;
	private int columWidth = 2;
	private transient Ticker updater;
	private transient Sampler sampler;
	private boolean autoscale;
	private transient Graph graph;

	public DataGraph(Graph graph, int interval, TimeUnit timeUnit) {
		this.graph = graph;
		this.updateInterval = interval;
		this.timeUnit = timeUnit;
	}

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