/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.gui;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Graphics;
import java.util.Arrays;

import javax.swing.JComponent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import com.github.dozedoff.commonj.util.Sampler;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class BarGraphTest {
	public @Rule MockitoRule mockito = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

	private static final Integer test_values[] = { 1, 2, 3 };

	@Mock
	private JComponent component;

	@Mock
	private Graphics g;

	@Mock
	private Sampler sampler;

	@InjectMocks
	private BarGraph barGraph;

	@SuppressFBWarnings("PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS")
	@Before
	public void setUp() throws Exception {
		when(component.getHeight()).thenReturn(10);
		when(component.getWidth()).thenReturn(10);
		
		when(sampler.getCapacity()).thenReturn(10);
		when(sampler.getSamples()).thenReturn(Arrays.asList(test_values));
	}

	@Test
	public void testAutoScale_one_value() throws Exception {
		barGraph.setAutoScale(true);
		when(sampler.getSamples()).thenReturn(Arrays.asList(1));
		
		barGraph.update(g);

		verify(g).fillRect(anyInt(), anyInt(), anyInt(), eq(10));
	}

	@Test
	public void testAutoScale_one_value_grater_than_height() throws Exception {
		barGraph.setAutoScale(true);
		when(sampler.getSamples()).thenReturn(Arrays.asList(20));

		barGraph.update(g);

		verify(g).fillRect(anyInt(), anyInt(), anyInt(), eq(10));
	}

	@Test
	public void testAutoScale_two_value() throws Exception {
		barGraph.setAutoScale(true);
		when(sampler.getSamples()).thenReturn(Arrays.asList(1, 2));

		barGraph.update(g);

		verify(g, atLeastOnce()).fillRect(anyInt(), anyInt(), anyInt(), eq(5));
	}

	@Test
	public void testUpdate() throws Exception {
		barGraph.update(g);
		
		verify(g).fillRect(0, 9, 1, 1);
	}

	@Test
	public void testUpdate3() throws Exception {
		barGraph.update(g);
		barGraph.update(g);
		barGraph.update(g);

		verify(g, atLeastOnce()).fillRect(2, 7, 1, 3);
	}

	@Test
	public void testThinBars() {
		when(sampler.getCapacity()).thenReturn(10000);

		barGraph.update(g);

		verify(g, atLeastOnce()).fillRect(anyInt(), anyInt(), eq(1), anyInt());
	}
}
