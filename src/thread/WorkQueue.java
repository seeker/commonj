/*  Copyright (C) 2011  Nicholas Wright

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
package thread;

import gui.Stats;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
/**
 * This Class handles Job Queue and Worker thread pool.
 *
 */
public class WorkQueue{
	//Parallel running Threads(Executor) on System
	int corePoolSize = 5;
	//Maximum Threads allowed in Pool
	int maxPoolSize = 8;
	//Keep alive time for waiting threads for jobs(Runnable)
	long keepAliveTime = 60;
	//This is the one who manages and start the work
	ThreadPoolExecutor threadPool = null;
	// the size of the job queue
	int queueSize = 100;
	//Working queue for jobs (Runnable)
	ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(10);

	Logger logger = Logger.getLogger(WorkQueue.class.toString());
	
	/**
	 * Create an new WorkQueue with default settings
	 */
	public WorkQueue() {
		create();
	}
	/**
	 * 
	 * @param maxPool
	 */
	public WorkQueue(int maxPool) {
		this.maxPoolSize = maxPool;
		create();
	}

	public WorkQueue(int corePoolSize,int maxPool, int queueSize) {
		this.maxPoolSize = maxPool;
		this.queueSize = queueSize;
		this.corePoolSize = corePoolSize;
		create();
	}

	public WorkQueue(int maxPool, int queueSize) {
		this.maxPoolSize = maxPool;
		this.queueSize = queueSize;
		create();
	} 

	private void create(){
		//Working queue for jobs (Runnable)
		ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(queueSize);

		threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
				keepAliveTime, TimeUnit.SECONDS, workQueue);
		threadPool.allowCoreThreadTimeOut(true);
		
		Thread upadterDaemon = new UpdaterDaemon();
		upadterDaemon.setDaemon(true);
		upadterDaemon.setPriority(8);
		upadterDaemon.start();
	}
	/**
	 * Here we add our jobs to working queue
	 *
	 * @param task a Runnable task
	 */
	public void execute(Runnable task) {
		threadPool.execute(task);
	}
	/**
	 * Shutdown the Threadpool if it's finished
	 */
	public void shutDown() {
		threadPool.shutdown();
	}
	
	/**
	 * Sets the poolsize.
	 * @param poolSize size of the new pool
	 */
	public synchronized void setPoolSize(int poolSize){
		threadPool.setMaximumPoolSize(poolSize);
		threadPool.setCorePoolSize(poolSize);
	}
	
	public int getPoolSize(){
		return threadPool.getPoolSize();
	}
	/**
	 * Returns the number of tasks currently in the queue.
	 * @return number of queued tasks
	 */
	public int getQueueSize(){
		return threadPool.getQueue().size();
	}
	
	/**
	 * Clears the tasks queue, already executing tasks are not affected.
	 */
	public synchronized void clearQueue(){
		threadPool.getQueue().clear();
		logger.info("Clearing page queue...");
	}
	/**
	 * Returns the number of currently executing tasks.
	 * @return number of executing tasks
	 */
	public int getActiveTasks(){
		return threadPool.getActiveCount();
	}
	
	class UpdaterDaemon extends Thread{
		private int oldQueue = 0, oldActive = 0, oldPool = 0;
		@Override
		public void run() {
			while(! isInterrupted()){
				if(oldQueue != getQueueSize() || oldActive != getActiveTasks() || oldPool != getPoolSize()){
					oldQueue = getQueueSize();
					oldActive = getActiveTasks();
					oldPool = getPoolSize();
					
					Stats.setPageQueueState("PageQueue: "+getQueueSize()+" - "+getActiveTasks()+" / "+getPoolSize());
				}
				
				try{sleep(500);}catch (InterruptedException ie){interrupt();}
			}
		}
	}
}