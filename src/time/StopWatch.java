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
package time;

import java.util.Calendar;

public class StopWatch {
	final String format = "%1$02d:%2$02d:%3$02d.%4$03d";
	boolean isRunning = false;
	long startTime, stopTime;
	
	final long CONST_H = 3600000;
	final long CONST_M = 60000;
	final long CONST_S = 1000;
	
	public boolean start(){
		startTime = Calendar.getInstance().getTimeInMillis();
		isRunning = true;
		return isRunning;
	}
	
	public boolean stop(){
		stopTime = Calendar.getInstance().getTimeInMillis();
		isRunning = false;
		return isRunning;
	}
	
	public String getTime(){
		return convertTime(getTimeMilli());
	}
	
	public long getTimeMilli(){
		return stopTime-startTime;
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
	public void reset(){
		isRunning = false;
		startTime = 0;
		stopTime = 0;
	}
	
	protected String convertTime(long time){
		
		if(time == 0){
			return "00:00:00.000";
		}
		
		if(time < 0){
			return "--:--:--.---";
		}
		
		long remainder, hours, minutes, seconds, milliSec;
		
		hours = time / CONST_H;
		remainder = time - (hours*CONST_H);
		
		minutes = remainder / CONST_M;
		remainder = remainder - (minutes*CONST_M);
		
		seconds = remainder / CONST_S;
		remainder = remainder - (seconds*CONST_S);
		
		milliSec = remainder;
		
		return String.format(format, hours,minutes,seconds,milliSec);
	}
}
