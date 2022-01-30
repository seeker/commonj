/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.gui;

import java.awt.Graphics;

/**
 * Interface for components that display graphs.
 * 
 * @author Nicholas Wright
 *
 */
public interface Graph {
	/**
	 * Update the displayed graph.
	 * 
	 * @param g
	 *            the graphics object from the component
	 */
	public void update(Graphics g);
}
