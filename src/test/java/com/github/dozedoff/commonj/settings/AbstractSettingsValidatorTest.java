/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.settings;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class AbstractSettingsValidatorTest {

	private AbstractSettingsValidator asv;
	private static final String DUMMY_PARAMETER = "foo";
	private static final int UPPER_BOUND = 42;
	private static final int LOWER_BOUND = -42;

	@Before
	public void setUp() throws Exception {
		asv = new AbstractSettingsValidator() {

			@Override
			public boolean validate(AbstractSettings settings) {
				return false;
			}
		};
	}

	@Test
	public void testCheckGreaterZeroGreater() throws Exception {
		asv.checkGreaterZero(DUMMY_PARAMETER, 42);
		assertThat(asv.getOkState(), is(true));
	}

	@Test
	public void testCheckGreaterZeroZero() throws Exception {
		asv.checkGreaterZero(DUMMY_PARAMETER, 0);
		assertThat(asv.getOkState(), is(false));
	}

	@Test
	public void testCheckGreaterZeroLess() throws Exception {
		asv.checkGreaterZero(DUMMY_PARAMETER, -1);
		assertThat(asv.getOkState(), is(false));
	}

	@Test
	public void testCheckRangeMatchLowerBound() throws Exception {
		asv.checkRange(DUMMY_PARAMETER, -42, LOWER_BOUND, UPPER_BOUND);
		assertThat(asv.getOkState(), is(true));
	}

	@Test
	public void testCheckRangeInRange() throws Exception {
		asv.checkRange(DUMMY_PARAMETER, 5, LOWER_BOUND, UPPER_BOUND);
		assertThat(asv.getOkState(), is(true));
	}

	@Test
	public void testCheckRangeMatchUpperBound() throws Exception {
		asv.checkRange(DUMMY_PARAMETER, 42, LOWER_BOUND, UPPER_BOUND);
		assertThat(asv.getOkState(), is(true));
	}

	@Test
	public void testCheckRangeNegativeRange() throws Exception {
		asv.checkRange(DUMMY_PARAMETER, -42, -50, -30);
		assertThat(asv.getOkState(), is(true));
	}

	@Test
	public void testCheckRangeBothBoundsSame() throws Exception {
		asv.checkRange(DUMMY_PARAMETER, 42, 42, 42);
		assertThat(asv.getOkState(), is(true));
	}

	@Test
	public void testCheckRangeOutOfLowerBound() throws Exception {
		asv.checkRange(DUMMY_PARAMETER, -100, LOWER_BOUND, UPPER_BOUND);
		assertThat(asv.getOkState(), is(false));
	}

	@Test
	public void testCheckRangeOutOfUpperBound() throws Exception {
		asv.checkRange(DUMMY_PARAMETER, 100, LOWER_BOUND, UPPER_BOUND);
		assertThat(asv.getOkState(), is(false));
	}

	@Test
	public void testGetOkState() throws Exception {
		assertThat(asv.getOkState(), is(true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReversedBounds() throws Exception {
		asv.checkRange(DUMMY_PARAMETER, 1, UPPER_BOUND, LOWER_BOUND);
	}

	public void testBoundsAreEqual() throws Exception {
		asv.checkRange(DUMMY_PARAMETER, UPPER_BOUND, UPPER_BOUND, UPPER_BOUND);
		assertThat(asv.getOkState(), is(true));
	}
}
