/* The MIT License (MIT)
 * Copyright (c) 2014 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */

package com.github.dozedoff.commonj.io;

/**
 * Default resource pool.
 */

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public abstract class ResourcePool<T> {

	protected final Queue<T> resources = new ConcurrentLinkedQueue<T>();
	private final Semaphore semaphore;

	public ResourcePool(int maxResources) {
		semaphore = new Semaphore(maxResources, true);
	}

	/**
	 * Try to acquire a resource from the pool. Either a resource is returned or the request times out and throws a exception.
	 * 
	 * @param maxWait
	 *            The maximum time to wait for a resource, in milliseconds.
	 * @return the requested resource.
	 * @throws InterruptedException
	 *             if interrupted while waiting for a resource.
	 * @throws ResourceCreationException
	 *             if the resource could not be created.
	 */
	public T getResource(long maxWait) throws InterruptedException, ResourceCreationException {

		// try to get a resource. Do not wait longer than the timeout
		semaphore.tryAcquire(maxWait, TimeUnit.MILLISECONDS);

		// Acquire a resource
		T resource = resources.poll();
		if (resource != null)
			return resource;

		// create a resource if none are available
		try {
			return createResource();
		} catch (Exception e) {
			// in case of failure the Semaphore permit must be returned, or they will run out
			semaphore.release();
			throw new ResourceCreationException(e);
		}
	}

	abstract protected T createResource();

	// to allow checks and manipulation before return
	public T processBeforReturn(T resource) {
		return resource;
	}

	public void returnResource(T resource) {
		T processedResource = processBeforReturn(resource);
		resources.add(processedResource);
		semaphore.release();
	}
}
