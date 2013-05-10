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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dozedoff.commonj.util.Pair;

public class DataProvider {
	private final static Logger logger = LoggerFactory.getLogger(DataProvider.class);
	private final DataLoader loader;
	
	private long upperLimit = 104857600;  	// 100 MB
	private long lowerLimit = 83886080;		// 80 MB
	
	AtomicLong queueSize = new AtomicLong();
	LinkedBlockingQueue<Path> filesToLoad = new LinkedBlockingQueue<>();
	LinkedBlockingQueue<Pair<Path, byte[]>> data = new LinkedBlockingQueue<>();
	
	public DataProvider(long lowerLimit, long upperLimit) {
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		loader = new DataLoader();
		loader.start();
	}
	
	public DataProvider() {
		loader = new DataLoader();
		loader.start();
	}
	
	public void clear() {
		filesToLoad.clear();
		data.clear();
	}
	
	public void addToLoad(Path... paths) {
		filesToLoad.addAll(filesToLoad);
	}
	
	public Pair<Path, byte[]> takeData() throws InterruptedException {
		synchronized (data) {
			return data.take();
		}
	}
	
	public void drainData(Collection<Pair<Path, byte[]>> drainTo, int maxElements) {
		synchronized (data) {
			data.drainTo(drainTo, maxElements);
			data.notify();
		}
	}
	
	class DataLoader extends Thread {
		public DataLoader() {
			this.setName("Data loader");
		}
		
		public boolean hasWork() {
			if(queueSize.get() > lowerLimit) {
				return false;
			} else if(filesToLoad.isEmpty()) {
				return false;
			}
			
			return true;
		}
		
		@Override
		public void run() {
			while(isInterrupted()) {
				while(! hasWork()) {
					try {
						wait();
					} catch (InterruptedException e) {interrupt();}
				}
				
				Path next = filesToLoad.peek();
				
				try {
					if(Files.size(next) + queueSize.get() > upperLimit) {
						try {
							wait();
							continue;
						} catch (InterruptedException e) {interrupt();}
					}
					
					try {
						next = filesToLoad.take();
						byte[] file = Files.readAllBytes(next);
						Pair<Path, byte[]> pair = new Pair<Path, byte[]>(next, file);
						data.add(pair);
					} catch (InterruptedException e) {interrupt();}
					
				} catch (IOException e) {
					logger.warn("Failed to load data for {} - {}", next, e.getMessage());
				}
			}
		}
	}
}
