package com.baidu.pcs;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;

public class BaiduPCSListStream extends BaiduPCSActionBase{
	
	public  PCSActionInfo.PCSListInfoResponse liststream(String type, String start, String limit){
		PCSActionInfo.PCSListInfoResponse ret = new PCSActionInfo.PCSListInfoResponse();
		if(null != type && type.length() > 0){
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Method, Value_Method));
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_AccessToken, getAccessToken()));
			params.add(new BasicNameValuePair("type", type));
			if(null != start&&start.length() > 0){
				params.add(new BasicNameValuePair("start", start));
			}
			if(null != limit && limit.length() > 0){
				params.add(new BasicNameValuePair("limit", limit));
			}
			// build url
			String url = BaiduPCSActionBase.PCSRequestUrl + "/" + sbCommand + "?" +buildParams(params);
			HttpGet httpget = new HttpGet(url);
			BaiduPCSActionBase.PCSRawHTTPResponse response = sendHttpRequest(httpget);
			
			if(null != response){
				ret.message = response.message;
				
				if(null != response.response){
					ret = parseListResponse(response.response);
				}
			}
		}

		return ret;

	}
	
	// value of method
	private final static String Value_Method = "list";
	
	// type of list
	@SuppressWarnings("unused")
	private final static String Value_Type = "type";
	
	// start of the list
	@SuppressWarnings("unused")
	private final static String Value_Start = "start";
	
	// limit of the list
	@SuppressWarnings("unused")
	private final static String Value_Limit = "limit";
	
	

}
