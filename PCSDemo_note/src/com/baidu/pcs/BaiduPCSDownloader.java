package com.baidu.pcs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;


//
// download file
//
public class BaiduPCSDownloader extends BaiduPCSActionBase{
	
	//
	// download file
	//
	public PCSActionInfo.PCSSimplefiedResponse downloadFile(String source, String target, BaiduPCSStatusListener listener){
		PCSActionInfo.PCSSimplefiedResponse ret = new PCSActionInfo.PCSSimplefiedResponse();
		if(null != source && null != target && source.length() > 0 && target.length() > 0){
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Method, Value_Method));
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_AccessToken, getAccessToken()));
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Path, source));

			// build url
			String url = BaiduPCSActionBase.PCSRequestUrl + "/" + mbCommand + "?" +buildParams(params);
			HttpGet httpget = new HttpGet(url);

			try {
				BaiduPCSActionBase.PCSRawHTTPResponse response = sendHttpRequest(httpget);

				if(null != response)
				{
					ret.message = response.message;

					if(null != response.response){

						long startMS = 0;

						long size = 0;
						long doneLength = 0;

						// in order to get content length
						Header[] requestHeaders = response.response.getHeaders(ContentLength);
						if (requestHeaders.length > 0) {
							size = Long.valueOf(requestHeaders[0].getValue());
						}

						// check the status
						if(HttpStatus.SC_OK == response.response.getStatusLine().getStatusCode()){

							boolean success = true;

							// open a file
							RandomAccessFile out = new RandomAccessFile(target, AccessReadWrite);
							InputStream inputStream = response.response.getEntity().getContent();
							MappedByteBuffer mappedStream = out.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, size);

							// a buffer to read file
							byte[] buffer = new byte[MaxPieceSize];

							int count = 0;
							while ((count = inputStream.read(buffer, 0, buffer.length)) > 0) {
								mappedStream.put(buffer, 0, count);
								doneLength += count;

								// update the listener
								if(null != listener){
									long interval = listener.progressInterval();

									long endMS = System.currentTimeMillis();

									// update progress
									if(endMS - startMS > interval){
										listener.onProgress(doneLength, size);
									}

									// if user wants to stop downloading
									if(false == listener.toContinue()){
										success = false;
										break;
									}
								}
							}

							if(success){
								mappedStream.force();
								out.getFD().sync();
								ret.error_code = 0;
								out.close();
							}
							else{
								out.close();
								ret.error_code = -1;
								ret.message = "User stops downloading";
							}
						}
						else{
							ret.error_code = response.response.getStatusLine().getStatusCode();
						}
					}
				}

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				ret.message = e1.getMessage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ret.message = e.getMessage();
			}

		}

		return ret;
	}
	
	// access to read and write
	private final static String AccessReadWrite = "rw";
	
	// Content-Length 
	private final static String ContentLength = "Content-Length";
	
	// MAX doenload piece
	private final static int MaxPieceSize = 1024 * 50;
	
	// value of method
	private final static String Value_Method = "download";
}
