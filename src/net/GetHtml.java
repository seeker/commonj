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
package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Class for loading HTML data from the Internet.
 */
public class GetHtml {
	private int failCount;
	private static Logger logger = Logger.getLogger(GetHtml.class.getName());
	private int maxRetry = 3;

	public int getResponse(String url)throws Exception{
		return getResponse(new URL(url));
	}
	
	public int getMaxRetry() {
		return maxRetry;
	}

	public void setMaxRetry(int maxRetry) {
		this.maxRetry = maxRetry;
	}

	public int getResponse(URL url){
		/*
		URLConnection thread = url.openConnection(); 
		return thread.getHeaderField(0);
		 */
		int response = 0;
		HttpURLConnection httpCon = null;		
		try{
			httpCon = connect(url);
			response =  httpCon.getResponseCode();
		}catch(IOException io){
			logger.warning("Error while getting HTML response code: "+io.getMessage());
		}finally{
			if(httpCon != null){
				httpCon.disconnect();

				// give some time to close
				try{Thread.sleep(20);}catch(InterruptedException ignore){}
			}
		}

		return response;
	}


	public String get (String url) throws Exception{
		return get(new URL(url));
	}
	/**
	 * Diese Funktion liefert den HTML code der angegebenen Adresse
	 * als String.
	 * 
	 * @param url Adresse der WebPage
	 * @return WebPage als Text String
	 * @throws IOException 
	 * @throws PageLoadException 
	 */
	public String get (URL url) throws IOException, PageLoadException {
		return loadHtml(connect(url));
	}
	
	private String loadHtml(HttpURLConnection connection) throws PageLoadException, IOException{
		StringBuilder classString = new StringBuilder();
		BufferedReader in = null;
		String inputLine = "";

		try{
			if (connection.getResponseCode() != 200){
				connection.disconnect();
				try{Thread.sleep(20);}catch(InterruptedException ignore){}
				throw new PageLoadException(String.valueOf(connection.getResponseCode()),connection.getResponseCode());
			}
			in = new BufferedReader(
					new InputStreamReader(
							connection.getInputStream()));
			while ((inputLine = in.readLine()) != null)	
				classString.append(inputLine);


		}catch(SocketException se){
			try {Thread.sleep(5000);} catch (InterruptedException e) {}
			return reTry(connection.getURL(), connection, new SocketException());
		}catch(SocketTimeoutException te){
			return reTry(connection.getURL(), connection, new SocketTimeoutException());
		}finally{
			if(in != null)
				in.close();
			if(connection != null){
				connection.disconnect();
				// give some time to close
				try{Thread.sleep(20);}catch(InterruptedException ignore){}
			}
		}
		
		reset();
		return classString.toString();
	}
	
	private HttpURLConnection connect(URL url) throws IOException, ProtocolException {
		return connect(url, null);
	}

	private HttpURLConnection connect(URL url, HashMap<String, String> requestProperties) throws IOException, ProtocolException {
		HttpURLConnection httpCon;
		httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:2.0) Gecko/20100101 Firefox/4.0"); // pretend to be a firefox browser
		if(requestProperties != null){
			for(String key : requestProperties.keySet()){
				httpCon.setRequestProperty(key, requestProperties.get(key));
			}
		}
		httpCon.setRequestMethod("GET");
		httpCon.setDoOutput(true);
		httpCon.setReadTimeout(10000);

		httpCon.connect();
		return httpCon;
	}

	private String reTry(URL url, HttpURLConnection httpCon, IOException ex) throws IOException, PageLoadException  {
		if (failCount < maxRetry){
			failCount++;
			httpCon.disconnect();
			try{Thread.sleep(20);}catch(InterruptedException ignore){}
			return get(url);
		}else{
			if(httpCon != null){
				httpCon.disconnect();
				try{Thread.sleep(20);}catch(InterruptedException ignore){}
			}
			throw ex;
		}
	}
	
	private void reset(){
		failCount = 0;
	}
}
