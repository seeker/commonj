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
package io;

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

	public ResourcePool(int maxResources){
		semaphore =  new Semaphore(maxResources, true);
	}

	/**
	 * Try to acquire a resource from the pool. Either a resource is returned or the request times out and throws a exception.
	 * @param maxWait The maximum time to wait for a resource, in milliseconds.
	 * @return the requested resource.
	 * @throws InterruptedException	if interrupted while waiting for a resource.
	 * @throws ResourceCreationException if the resource could not be created.
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
	public T processBeforReturn(T resource){
		return resource;
	}

	public void returnResource(T resource) {
		T processedResource = processBeforReturn(resource);
		resources.add(processedResource);
		semaphore.release();
	}
}
