/*
 * The MIT License (MIT)
 * Copyright (c) 2022 Nicholas Wright
 * http://opensource.org/licenses/MIT
 */
package com.github.dozedoff.commonj.io;

import java.util.HashSet;

/**
 * Default resource pool.
 */

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Generic resource pool that provides instances of the specified type.
 * 
 * @author Nicholas Wright
 *
 * @param <T>
 *            type of the resource the pool will provide
 */
public abstract class ResourcePool<T> {
	protected final Set<T> ownedByPool = new HashSet<T>();
	protected final Queue<T> resources = new ConcurrentLinkedQueue<T>();
	private final Semaphore semaphore;

	/**
	 * Create a pool with a limit of resources it can contain.
	 * 
	 * @param maxResources
	 *            the maximum number of resources this pool can contain
	 */
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
		if (!semaphore.tryAcquire(maxWait, TimeUnit.MILLISECONDS)) {
			throw new ResourceCreationException(new TimeoutException("No Resource available within the required time"));
		}

		// Acquire a resource
		T resource = resources.poll();
		if (resource != null)
			return resource;

		// create a resource if none are available
		try {
			T createdResource = createResource();
			ownedByPool.add(createdResource);
			return createdResource;
		} catch (Exception e) {
			// in case of failure the Semaphore permit must be returned, or they will run out
			semaphore.release();
			throw new ResourceCreationException(e);
		}
	}

	/**
	 * Create a new resource instance.
	 * 
	 * @return the configured and initialised resource
	 * @throws Exception
	 *             if there is a exception creating the resource
	 */
	abstract protected T createResource() throws Exception;

	// FIXME should not be public
	// to allow checks and manipulation before return
	/**
	 * Check that the returned resource was created by the pool.
	 * 
	 * @param resource
	 *            to return to the pool
	 * @return the resource to return to the pool
	 */
	public T processBeforReturn(T resource) {
		if (!ownedByPool.contains(resource)) {
			throw new IllegalArgumentException("Object was not created by the pool!");
		}

		return resource;
	}

	/**
	 * Return a resource to the pool. The resource is checked that it was
	 * created by the pool.
	 * 
	 * @param resource
	 *            to return to the pool. Must have been created by the pool
	 */
	public void returnResource(T resource) {
		T processedResource = processBeforReturn(resource);
		resources.add(processedResource);
		semaphore.release();
	}
}
