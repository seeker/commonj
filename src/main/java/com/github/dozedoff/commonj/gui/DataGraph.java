/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JPanel;

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

	LinkedList<Integer> graphData = new LinkedList<Integer>();
	AtomicInteger currentCount = new AtomicInteger(0);

	public DataGraph(int hight, int width, int cNum, int cWidth, int interval, boolean autoscale) {
		this.setSize(width, hight);
		this.noOfColums = cNum;
		this.columWidth = cWidth;
		this.updateInterval = interval;
		this.autoscale = autoscale;

		initGraphData();
	}

	public DataGraph(int hight, int width, int cNum, int cWidth, int interval) {

		this.setSize(width, hight);
		this.noOfColums = cNum;
		this.columWidth = cWidth;
		this.updateInterval = interval;

		initGraphData();
	}

	private void initGraphData() {
		synchronized (graphData) {
			for (int i = 0; i < noOfColums; i++) {
				graphData.add(0);
			}
		}
	}

	public void add(int delta) {
		currentCount.addAndGet(delta);
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

		synchronized (graphData) {
			int i = 0;
			for (int currval : graphData) {
				currval = (int) (currval / scaleFactor);
				g.fillRect(i * columWidth, this.getHeight() - currval, columWidth, currval);
				i++;
			}
		}
	}

	private void calcScale() {
		int max = 0;

		for (int i : graphData) {
			max = Math.max(max, i);
		}

		if (max > this.getHeight()) {
			scaleFactor = max / this.getHeight();
		} else {
			scaleFactor = 1;
		}
	}

	class Updater extends TimerTask {

		@Override
		public void run() {
			synchronized (graphData) {
				graphData.removeLast();
				graphData.addFirst(currentCount.getAndSet(0));
			}
			calcScale();
			repaint();
		}
	}
}