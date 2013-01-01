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
package com.github.dozedoff.commonj.net;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for downloading files from the Internet.
 */
public abstract class FileLoader {
	private static final Logger logger = LoggerFactory.getLogger(FileLoader.class);

	protected LinkedBlockingQueue<DownloadItem> downloadList = new LinkedBlockingQueue<DownloadItem>();
	private LinkedList<DownloadWorker> workers = new LinkedList<>();

	/**Delay between downloads. This is used to limit the number of connections**/
	protected int downloadSleep = 1000;
	protected int fileQueueWorkers;

	private GetBinary getBinary = new GetBinary();

	private File workingDir;

	public FileLoader(File workingDir, int fileQueueWorkers) {
		this.workingDir = workingDir;
		this.fileQueueWorkers = fileQueueWorkers;
		setUp(fileQueueWorkers);
	}

	/**
	 * Run before a file is added to the list.
	 * @param url URL that was added
	 * @param fileName relative path to working directory
	 * @return if true operation will continue
	 */
	protected boolean beforeFileAdd(URL url,String fileName){return true;} // code to run before adding a file to the list

	/**
	 * Run after a file was added to the list.
	 * @param url URL that was added
	 * @param fileName relative path to working directory
	 */
	protected void afterFileAdd(URL url,String fileName){} // code to run after adding a file to the list

	public void add(URL url,String fileName){
		if(! beforeFileAdd(url, fileName))
			return;

		DownloadItem toadd = new DownloadItem(url, fileName);

		if(downloadList.contains(toadd))
			return;

		downloadList.add(toadd);

		afterFileAdd(url, fileName);
	}

	/**
	 * Set the delay between file downloads. Used to limit the number of connections.
	 * @param sleep time between downloads in milliseconds
	 */
	public void setDownloadSleep(int sleep){
		this.downloadSleep = sleep;
	}

	public void clearQueue(){
		downloadList.clear();
		logger.info("Download queue cleared");
		afterClearQueue();
	}

	/**
	 * Called after the queue has been cleared.
	 */
	protected void afterClearQueue(){}

	/**
	 * Download a file, how the data is used is handled in the method afterFileDownload
	 * 
	 * @param url URL to save
	 * @param savePath relative save path
	 */
	private void loadFile(URL url, File savePath){
		File fullPath = new File(workingDir, savePath.toString());

		try{Thread.sleep(downloadSleep);}catch(InterruptedException ie){}

		byte[] data = null;
		try{
			data = getBinary.getViaHttp(url);
			afterFileDownload(data,fullPath,url);
		}catch(PageLoadException ple){
			onPageLoadException(ple);
		}catch(IOException ioe){
			onIOException(ioe);
		}
	}

	/**
	 * Called when a server could be contacted, but an error code was returned.
	 * @param ple the PageLoadException that was thrown
	 */
	protected void onPageLoadException(PageLoadException ple){
		logger.warn("Unable to load {} , response is {}", ple.getUrl(), ple.getResponseCode());
	}

	/**
	 * Called when a page / File could not be loaded due to an IO error.
	 * @param ioe the IOException that was thrown
	 */
	protected void onIOException(IOException ioe){
		logger.warn("Unable to load page {}", ioe.getMessage());
	}

	/**
	 * Called when the file was successfully downloaded.
	 * @param data the downloaded file
	 * @param fullpath the absolute filepath
	 * @param url the url of the file
	 */
	abstract protected void afterFileDownload(byte[] data, File fullpath, URL url);

	private void setUp(int fileWorkers){
		logger.debug("Setting up FileLoader with {} workers", fileWorkers);
		logger.debug("Creating worker threads");
		for(int i=0; i <fileWorkers; i++){
			workers.add(new DownloadWorker());
		}

		logger.debug("Starting worker threads");
		for(DownloadWorker t : workers){
			t.start();
		}
		
		logger.debug("FileLoader setup complete");
	}

	public void shutdown(){
		logger.info("Shutting down FileLoader...");
		clearQueue();

		logger.debug("Stopping worker threads...");
		for(DownloadWorker t : workers){
			t.kill();
			t.interrupt();
		}

		logger.debug("Waiting for worker threads to die...");
		for(DownloadWorker t : workers){
			try {t.join();} catch (InterruptedException e) {t.interrupt();}
		}
		
		logger.debug("FileLoader shutdown complete");
	}

	/**
	 * Called after a worker has processed an item from the list.
	 * @param di the DownloadItem that was processed
	 */
	protected void afterProcessItem(DownloadItem di){}

	class DownloadWorker extends Thread{
		private boolean stopped = false;
		public DownloadWorker() {
			super("Download Worker");

			Thread.currentThread().setPriority(2);
		}
		
		public void kill() {
			this.stopped = true;
		}

		@Override
		public void run() {
			DownloadItem di = null;
			while(!stopped){
				try{
					di = downloadList.take();

					loadFile(di.getImageUrl(), new File(di.getImageName()));
					afterProcessItem(di);

				}catch(InterruptedException ie){
					interrupt(); //otherwise it will reset it's own interrupt flag
					logger.debug("FileLoader download worker was interrupted");
				}catch(Exception e){
					Object[] logParams = {e, di.getImageUrl(), di.getImageName()};
					logger.warn("Download Worker failed with {}, Parameters: URL: {} ImageName: {}", logParams);
				}
			}
			logger.debug("FileLoader Download worker has died gracefully");
		}
	}
}
