/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
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

	// TODO DEPRECATED remove after 0.2.1
	@Deprecated
	public DataGraph(int hight, int width, int cNum, int cWidth, int interval, boolean autoscale) {
		this.setSize(width, hight);
		this.noOfColums = cNum;
		this.columWidth = cWidth;
		this.updateInterval = interval;
		this.autoscale = autoscale;

		sampler = new Sampler(noOfColums);
		this.graph = new BarGraph(this, sampler);
	}

	// TODO DEPRECATED remove after 0.2.1
	@Deprecated
	public DataGraph(int hight, int width, int cNum, int cWidth, int interval) {
		this(hight, width, cNum, cWidth, interval, false);
	}

	public DataGraph(Graph graph, int interval, TimeUnit timeUnit) {
		this.graph = graph;
		this.updateInterval = interval;
		this.timeUnit = timeUnit;
	}

	// TODO DEPRECATED remove after 0.2.1
	@Deprecated
	public void add(int delta) {
		sampler.addDelta(delta);
	}

	public void start() {
		if (updater != null) {
			return;
		}

		updater = new Ticker("DataGraph updater", updateInterval, TimeUnit.SECONDS) {
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