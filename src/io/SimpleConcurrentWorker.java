/*  Copyright (C) 2012  Nicholas Wright
	
	part of 'AidUtil', a collection of maintenance tools for 'Aid'.

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

import java.util.AbstractQueue;

public abstract class SimpleConcurrentWorker<E,T> extends Thread{
	private AbstractQueue<E> input;
	private AbstractQueue<T> output;
	
	public SimpleConcurrentWorker() {}
	
	public SimpleConcurrentWorker(String threadName){
		super(threadName);
	}
	
	@Override
	public final void run() {
		work(this.input, this.output);
	}
	
	protected abstract void work(AbstractQueue<E> input, AbstractQueue<T> output);
}
