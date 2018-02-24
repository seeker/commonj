/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JComponent;

import com.github.dozedoff.commonj.util.Sampler;

/**
 * Plot data as a bar graph.
 * 
 * @author Nicholas Wright
 *
 */
public class BarGraph implements Graph {
	private final JComponent component;
	private transient final Sampler sampler;
	private Color barColor;
	private double scaleFactor = 1;
	private boolean autoScale = false;

	/**
	 * Create a new {@link BarGraph} on the provided component.
	 * 
	 * @param component
	 *            to draw the graph on
	 * @param sampler
	 *            class containing the data to display
	 */
	public BarGraph(JComponent component, Sampler sampler) {
		this(component, sampler, Color.BLUE);
	}

	/**
	 * Create a new {@link BarGraph} on the provided component.
	 * 
	 * @param component
	 *            to draw the graph on
	 * @param sampler
	 *            class containing the data to display
	 * @param color
	 *            Colour of the bars
	 */
	public BarGraph(JComponent component, Sampler sampler, Color color) {
		this.component = component;
		this.sampler = sampler;
		this.barColor = color;
	}

	/**
	 * Set if the bars height should be scaled relative to the highest bar. If
	 * not set, the height of the graph is used, 1px = 1
	 * 
	 * @param autoScale
	 *            true to enable auto-scaling, false to disable
	 */
	public void setAutoScale(boolean autoScale) {
		this.autoScale = autoScale;
	}

	/**
	 * Update the {@link BarGraph}, drawing bars for the current values.
	 * 
	 * @param g
	 *            {@link Graphics} object used to draw onto the component
	 */
	@Override
	public void update(Graphics g) {
		List<Integer> graphData = sampler.getSamples();
		calcScale(graphData);
		g.setColor(barColor);

		int columnOffset = 0;

		int columnWidth = columnWidth();
		for (int barValue : graphData) {
			barValue = (int) (barValue / scaleFactor);
			g.fillRect(columnOffset * columnWidth, this.component.getHeight() - barValue, columnWidth, barValue);
			columnOffset++;
		}
	}

	private int columnWidth() {
		int barWidth = this.component.getWidth() / sampler.getCapacity();

		if (barWidth == 0) {
			return 1;
		}

		return barWidth;
	}

	private void calcScale(List<Integer> graphData) {
		if (!autoScale) {
			scaleFactor = 1;
			return;
		}
		
		int max = 0;

		for (int i : graphData) {
			max = Math.max(max, i);
		}

		if (max > component.getHeight()) {
			scaleFactor = (double) max / (double) this.component.getHeight();
		} else {
			scaleFactor = Math.pow((component.getHeight() / (double) max), -1);
		}
	}
}
