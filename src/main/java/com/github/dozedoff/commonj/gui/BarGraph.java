package com.github.dozedoff.commonj.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JComponent;

import com.github.dozedoff.commonj.util.Sampler;

public class BarGraph implements Graph {
	private final JComponent component;
	private final Sampler sampler;
	private Color barColor;
	private double scaleFactor = 1;

	public BarGraph(JComponent component, Sampler sampler) {
		this(component, sampler, Color.BLUE);
	}

	public BarGraph(JComponent component, Sampler sampler, Color color) {
		this.component = component;
		this.sampler = sampler;
		this.barColor = color;
	}

	@Override
	public void update(Graphics g) {
		calcScale(sampler.getSamples());
		g.setColor(barColor);
		List<Integer> graphData = sampler.getSamples();
		int i = 0;

		int columWidth = columnWidth();
		for (int currval : graphData) {
			currval = (int) (currval / scaleFactor);
			g.fillRect(i * columWidth, this.component.getHeight() - currval, columWidth, currval);
			i++;
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
		int max = 0;

		for (int i : graphData) {
			max = Math.max(max, i);
		}

		if (max > this.component.getHeight()) {
			scaleFactor = max / (double) this.component.getHeight();
		} else {
			scaleFactor = 1;
		}
	}
}
