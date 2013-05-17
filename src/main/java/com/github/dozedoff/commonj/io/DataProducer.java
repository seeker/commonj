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
package com.github.dozedoff.commonj.io;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class DataProducer<I,O> {
	protected LinkedBlockingQueue<I> input = new LinkedBlockingQueue<>();
	protected LinkedBlockingQueue<O> output = new LinkedBlockingQueue<>();
	
	private LinkedList<DataLoader> loaders = new LinkedList<>();
	
	public DataProducer() {
		startLoader();
	}
	
	public DataProducer(int outputQueueMaxSize) {
		output = new LinkedBlockingQueue<>(outputQueueMaxSize);
		
		startLoader();
	}
	
	public DataProducer(int inputQueueMaxSize, int outputQueueMaxSize) {
		input = new LinkedBlockingQueue<>(inputQueueMaxSize);
		output = new LinkedBlockingQueue<>(outputQueueMaxSize);
		
		startLoader();
	}
	
	public void startLoader() {
		startLoader(1);
	}
	
	public void startLoader(int numberOfLoaders) {
		for(int i = 0; i < numberOfLoaders; i++){
			DataLoader loader = new DataLoader();
			loaders.add(loader);
			loader.setDaemon(true);
			loader.start();
		}
	}
	
	public void stopLoader() {
		stopLoader(1);
	}
	
	public void stopLoader(int numberOfLoaders) {
		for(int i = 0; i < numberOfLoaders; i++){
			if(loaders.size() > 1){
				DataLoader loader = loaders.removeLast();
				loader.interrupt();
			}
		}
	}
	
	public void clear() {
		input.clear();
		output.clear();
		outputQueueChanged();
	}
	
	public void addToLoad(@SuppressWarnings("unchecked") I... paths) {
		List<I> list = Arrays.asList(paths);
		input.addAll(list);
	}
	
	public void addToLoad(List<I> paths) {
		input.addAll(paths);
	}
	
	public O takeData() throws InterruptedException {
			O take = output.take();
			outputQueueChanged();
			return take;
	}
	
	public void drainTo(Collection<O> drainTo, int maxElements) throws InterruptedException {
			O next = output.take();
			drainTo.add(next);
			output.drainTo(drainTo, maxElements - 1);
			outputQueueChanged();
	}
	
	public boolean hasWork() {
		return (! input.isEmpty()) || (! output.isEmpty()); 
	}
	
	abstract protected void loaderDoWork() throws InterruptedException;
	
	protected void outputQueueChanged(){};
	
	class DataLoader extends Thread {
		
		public DataLoader() {
			this.setName("Data loader");
		}
		
		@Override
		public void run() {
			while(! isInterrupted()) {
					try {
						loaderDoWork();
						outputQueueChanged();
					} catch (InterruptedException e) {
						interrupt();
					}
			}
		}
	}
}
