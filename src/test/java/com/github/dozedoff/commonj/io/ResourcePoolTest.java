package com.github.dozedoff.commonj.io;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class ResourcePoolTest {
	private ResourcePool<Integer> pool;
	private static final int MAX_RESOURCE = 3;
	private static final int MAX_WAIT_MS = 1000;

	@Before
	public void setUp() throws Exception {
		pool = new ResourcePool<Integer>(MAX_RESOURCE) {

			@Override
			protected Integer createResource() {
				return new Integer(-1);
			}
		};
	}

	@Test
	public void testGetResourceNew() throws Exception {
		Integer resource = pool.getResource(MAX_WAIT_MS);

		assertThat(resource, is(-1));
	}

	@Test
	public void testGetResourceReused() throws Exception {
		Integer resource = pool.getResource(MAX_WAIT_MS);
		resource = new Integer(1);

		pool.returnResource(resource);

		resource = pool.getResource(MAX_WAIT_MS);

		assertThat(resource, is(1));
	}

	@Test
	public void testGetResourceMax() throws Exception {
		LinkedList<Integer> res = new LinkedList<Integer>();

		for (int i = 0; i < MAX_RESOURCE; i++) {
			res.add(pool.getResource(MAX_WAIT_MS));
		}
	}

	@Test
	public void testReturnResourceMax() throws Exception {
		LinkedList<Integer> res = new LinkedList<Integer>();

		for (int i = 0; i < MAX_RESOURCE; i++) {
			res.add(pool.getResource(MAX_WAIT_MS));
		}

		for (Integer resource : res) {
			pool.returnResource(resource);
		}
	}

	@Test(expected = ResourceCreationException.class)
	public void testGetResourceMoreThanMax() throws Exception {
		LinkedList<Integer> res = new LinkedList<Integer>();

		final int max = MAX_RESOURCE + 1;
		for (int i = 0; i < max; i++) {
			res.add(pool.getResource(MAX_WAIT_MS));
		}
	}

	@Test(expected = ResourceCreationException.class)
	public void testResourceCreationException() throws Exception {
		pool = new ResourcePool<Integer>(MAX_RESOURCE) {

			@Override
			protected Integer createResource() throws Exception {
				throw new Exception();
			}
		};

		Integer foo = pool.getResource(MAX_WAIT_MS);
		assertThat(foo, is(not(null)));
	}
}
