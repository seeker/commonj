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
package io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
/**
 * Class for downloading binary data from the Internet.
 */
public class GetBinary {
	long contentLenght = 0;
	int offset = 0;
	int failCount = 0;
	ByteBuffer classBuffer;
	Logger logger = Logger.getLogger(GetBinary.class.getName());
	
	public GetBinary(){
		classBuffer = ByteBuffer.allocate(15728640); //15mb
	}
	
	public GetBinary(int size){
		classBuffer = ByteBuffer.allocate(size);
	}
	
	public byte[] get(String url) throws IOException{
		return get(new URL(url));
	}

	public byte[] get(URL url) throws IOException{

		BufferedInputStream binary = null;

		HttpURLConnection thread = null;
		try {
			thread = (HttpURLConnection) url.openConnection();
			thread.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:2.0) Gecko/20100101 Firefox/4.0"); // pretend to be a firefox browser
			binary = new BufferedInputStream(thread.getInputStream());
			classBuffer.clear();		  
			//ByteBuffer bb = ByteBuffer.allocate(15728640); //15mb

			int count = 0;
			byte[] c= new byte[8192];			//transfer data from input (URL) to output (file) one byte at a time

			while ((count=binary.read(c)) != -1){
				classBuffer.put(c, 0, count);
			}
			classBuffer.flip();
			byte[] varBuffer = new byte[classBuffer.limit()];

			classBuffer.get(varBuffer);
			binary.close();

			return varBuffer;
		} catch (IOException e) {
			throw new IOException("unable to connect to " + url.toString());
		}finally{
			if(binary != null)
				binary.close();
			if(thread != null)
				thread.disconnect();
			// give some time to close
			try{Thread.sleep(20);}catch(InterruptedException ignore){}
		}
	}

	public Long getLenght(URL url)throws IOException, PageLoadException{
		HttpURLConnection thread = null;

		try{
			thread = (HttpURLConnection)url.openConnection();
			thread.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:2.0) Gecko/20100101 Firefox/4.0"); // pretend to be a firefox browser
			thread.setRequestMethod("HEAD");
			thread.setDoOutput(true);
			thread.connect();
			return Long.valueOf(thread.getHeaderField("Content-Length"));
		} catch (NumberFormatException nfe){
			if(thread.getResponseCode() != 200)
				throw new PageLoadException(Integer.toString(thread.getResponseCode()),thread.getResponseCode());
			throw new NumberFormatException("unable to parse "+url.toString());
		}catch(SocketTimeoutException ste){
			throw new SocketTimeoutException(ste.getMessage());
		} catch (IOException e) {
			throw new IOException("unable to connect to " + url.toString());
		} catch(ClassCastException cce){
			logger.warning(cce.getMessage()+", "+url.toString());
		}finally{
			if(thread != null)
				thread.disconnect();
			// give some time to close
			try{Thread.sleep(20);}catch(InterruptedException ignore){}
		}
		return contentLenght;
	}


	public Map<String,List<String>> getHeader(URL url) throws IOException{
		HttpURLConnection thread = null;
		try{
			thread = (HttpURLConnection) url.openConnection();
			thread.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:2.0) Gecko/20100101 Firefox/4.0"); // pretend to be a firefox browser
			return thread.getHeaderFields();	
		} catch (IOException e) {
			throw new IOException("unable to connect to " + url.toString());
		}finally{
			if(thread != null){
				thread.disconnect();
				// give some time to close
				try{Thread.sleep(20);}catch(InterruptedException ignore){}
			}
		}
	}

	public byte[] getRange(URL url, int start, long l) throws IOException, PageLoadException{
		BufferedInputStream binary = null;
		HttpURLConnection httpCon;
		try{
			httpCon = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			throw new IOException("unable to connect to " + url.toString());
		}

		try{
			httpCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:2.0) Gecko/20100101 Firefox/4.0"); // pretend to be a firefox browser
			httpCon.setRequestMethod("GET");
			httpCon.setDoOutput(true);
			httpCon.setRequestProperty("Range", "bytes=" + start + "-"+ l);
			httpCon.setReadTimeout(10000);

			httpCon.connect();
			binary = new BufferedInputStream(httpCon.getInputStream());
		}catch(SocketTimeoutException ste){
			if(httpCon != null){
				httpCon.disconnect();
				// give some time to close
				try{Thread.sleep(20);}catch(InterruptedException ignore){}
			}
			throw new SocketTimeoutException(ste.getMessage());
		}catch(IOException e){
			if(httpCon != null){
				httpCon.disconnect();
				// give some time to close
				try{Thread.sleep(20);}catch(InterruptedException ignore){}
			}
			throw new PageLoadException(Integer.toString(httpCon.getResponseCode()),httpCon.getResponseCode());
		}
		int count = 0;
		byte[] c= new byte[8192];			//transfer data from input (URL) to output (file) one byte at a time

		try{
			while ((count=binary.read(c)) != -1){
				classBuffer.put(c, 0, count);
			}
		}catch(SocketException se){
			logger.warning("SocketException, http response: "+httpCon.getResponseCode());
			if (failCount <= 20){
				try{Thread.sleep(5000);}catch(Exception ie){}
				this.offset = classBuffer.position();
				httpCon.disconnect();
				failCount++;
				return getRange(url,offset,contentLenght-1);
			}
			else{
				logger.warning("Buffer position at failure: "+classBuffer.position()+"  URL: "+url.toString());
				httpCon.disconnect();
			}
		}finally{
			if(binary != null)
				binary.close();
			if(httpCon != null){
				httpCon.disconnect();
				// give some time to close
				try{Thread.sleep(20);}catch(InterruptedException ignore){}
			}
		}
		if (failCount != 0)
			logger.info("GetBinary Successful -> "+classBuffer.position()+"/"+contentLenght+", "+failCount+" tries, "+url.toString());

		classBuffer.flip();
		byte[] varBuffer = new byte[classBuffer.limit()];
		classBuffer.get(varBuffer);
		classBuffer.clear();
		return varBuffer;
	}

	public byte[] getViaHttp(URL url)throws IOException, PageLoadException{
		this.contentLenght = getLenght(url);

		BufferedInputStream binary = null;
		HttpURLConnection httpCon = null;

		try{
			httpCon = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			throw new IOException("unable to connect to " + url.toString());
		}

		try{
			httpCon.setRequestMethod("GET");
			httpCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:2.0) Gecko/20100101 Firefox/4.0"); // pretend to be a firefox browser
			httpCon.setDoOutput(true);
			httpCon.setReadTimeout(10000);

			httpCon.connect();
			binary = new BufferedInputStream(httpCon.getInputStream());
		}catch(SocketTimeoutException ste){
			throw new SocketTimeoutException(ste.getMessage()); 
		}catch(IOException e){
			if(httpCon.getResponseCode() != 200)
				throw new PageLoadException(Integer.toString(httpCon.getResponseCode()),httpCon.getResponseCode());
		}catch(Exception ue){
			logger.severe("Unhandled Exception "+ ue.getMessage());
		}

		int count = 0;
		byte[] c= new byte[8192];			//transfer data from input (URL) to output (file) one byte at a time
		try{	          
			while ((count=binary.read(c)) != -1){
				classBuffer.put(c, 0, count);
			}
		}catch(SocketException se){
			try{Thread.sleep(5000);}catch(Exception ie){}
			this.offset = classBuffer.position();

			failCount++;
			logger.warning("GetBinary failed, reason: "+ se.getLocalizedMessage()+"  -> "+classBuffer.position()+"/"+contentLenght+"  "+url.toString());
			httpCon.disconnect();
			return getRange(url,offset,contentLenght-1);
		}catch(NullPointerException npe){
			logger.severe("NullPointerException in GetBinary.getViaHttp");
			return null;
		}finally{
			if(binary != null)
				binary.close();
			if(httpCon != null){
				httpCon.disconnect();
				// give some time to close
				try{Thread.sleep(20);}catch(InterruptedException ignore){}
			}
		}

		classBuffer.flip();
		byte[] varBuffer = new byte[classBuffer.limit()];
		classBuffer.get(varBuffer);
		classBuffer.clear();
		return varBuffer;
	}
}
