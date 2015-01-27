/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import com.github.dozedoff.commonj.util.Sampler;

public class DataGraph extends JPanel {
	/**
	 * Shows data over time
	 */
	private static final long serialVersionUID = 1L;
	private int updateInterval = 1; // in Seconds
	private int noOfColums = 20;
	private int columWidth = 2;
	private Timer updater = null;
	private boolean autoscale;
	private double scaleFactor = 1;

	Sampler sampler;

	public DataGraph(int hight, int width, int cNum, int cWidth, int interval, boolean autoscale) {
		this.setSize(width, hight);
		this.noOfColums = cNum;
		this.columWidth = cWidth;
		this.updateInterval = interval;
		this.autoscale = autoscale;

		sampler = new Sampler(noOfColums);
	}

	public DataGraph(int hight, int width, int cNum, int cWidth, int interval) {
		this(hight, width, cNum, cWidth, interval, false);
	}

	public void add(int delta) {
		sampler.addDelta(delta);
	}

	public void start() {
		if (updater == null) {
			updater = new Timer("DataGraph updater");
			updater.scheduleAtFixedRate(new Updater(), updateInterval * 1000, updateInterval * 1000);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLUE);
		List<Integer> graphData = sampler.getSamples();
		int i = 0;

		for (int currval : graphData) {
			currval = (int) (currval / scaleFactor);
			g.fillRect(i * columWidth, this.getHeight() - currval, columWidth, currval);
			i++;
		}
	}

	private void calcScale(List<Integer> graphData) {
		int max = 0;

		for (int i : graphData) {
			max = Math.max(max, i);
		}

		if (max > this.getHeight()) {
			scaleFactor = max / (double) this.getHeight();
		} else {
			scaleFactor = 1;
		}
	}

	class Updater extends TimerTask {

		@Override
		public void run() {
			sampler.sample();
			calcScale(sampler.getSamples());
			repaint();
		}
	}
}