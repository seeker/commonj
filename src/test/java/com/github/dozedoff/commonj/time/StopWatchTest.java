/*  Copyright (C) 2012  Nicholas Wright

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.dozedoff.commonj.time;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class StopWatchTest {
	StopWatch stopWatch;

	@Before
	public void setUp() throws Exception {
		stopWatch = new StopWatch();
	}

	@Test
	public void testStart() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(100);
		stopWatch.start();
		Thread.sleep(100);
		stopWatch.stop();

		assertThat(stopWatch.getTimeMilli(), allOf(greaterThan(190L), lessThan(210L)));
	}

	@Test
	public void testStop() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(100);
		stopWatch.stop();
		stopWatch.stop();

		assertThat(stopWatch.getTimeMilli(), allOf(greaterThan(90L), lessThan(110L)));
	}

	@Test
	public void testGetTime() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(100);
		stopWatch.stop();

		assertThat(stopWatch.getTime(), is("00:00:00.100"));
	}

	@Test
	public void testGetTimeWhileRunning() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(100);
		assertThat(stopWatch.getTime(), is("00:00:00.100"));
		stopWatch.stop();
	}

	@Test
	public void testGetTimeMilli() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(100);
		stopWatch.stop();

		assertThat(stopWatch.getTimeMilli(), allOf(greaterThan(90L), lessThan(110L)));
	}

	@Test
	public void testGetTimeMilliWhileRunning() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(100);
		assertThat(stopWatch.getTimeMilli(), allOf(greaterThan(90L), lessThan(110L)));
		stopWatch.stop();
	}

	@Test
	public void testIsRunning() {
		assertThat(stopWatch.isRunning(), is(false));

		stopWatch.start();
		assertThat(stopWatch.isRunning(), is(true));

		stopWatch.stop();
		assertThat(stopWatch.isRunning(), is(false));
	}

	@Test
	public void testReset() throws InterruptedException {
		stopWatch.start();
		Thread.sleep(100);
		stopWatch.stop();

		assertThat(stopWatch.getTimeMilli(), allOf(greaterThan(90L), lessThan(110L)));

		stopWatch.reset();

		assertThat(stopWatch.getTimeMilli(), is(0L));
	}

	@Test
	public void testInitialState() {
		assertThat(stopWatch.getTimeMilli(), is(0L));
	}

	@Test
	public void testConvertTimeZero() {
		assertThat(stopWatch.convertTime(0), is("00:00:00.000"));
	}

	@Test
	public void testConvertTimeInvalid() {
		assertThat(stopWatch.convertTime(-1), is("--:--:--.---"));
	}

	@Test
	public void testConvertTime() {
		assertThat(stopWatch.convertTime(100), is("00:00:00.100"));
	}
}
