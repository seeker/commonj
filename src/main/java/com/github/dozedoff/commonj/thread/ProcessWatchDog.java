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
package com.github.dozedoff.commonj.thread;

import java.util.logging.Logger;

public class ProcessWatchDog extends Thread{
	String processDescription;
	Process process;
	long timeout;
	final static Logger logger = Logger.getLogger(ProcessWatchDog.class.getName());
	
	public ProcessWatchDog(String processDescription, Process process, long timeout) {
		this.process = process;
		this.timeout = timeout;
		this.processDescription = processDescription;
	}

	@Override
	public void run() {
		try {
			sleep(timeout);
		} catch (InterruptedException e) {
			return; // all is well
		}
		
		logger.severe("Process timed out. Description: " + processDescription);
		process.destroy();
	}
}
