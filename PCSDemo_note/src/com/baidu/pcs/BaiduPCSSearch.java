package com.baidu.pcs;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;

public class BaiduPCSSearch extends BaiduPCSActionBase{

	//
	// search file
	//
	protected PCSActionInfo.PCSListInfoResponse search(String path, String key, boolean recursive){

		PCSActionInfo.PCSListInfoResponse ret = new PCSActionInfo.PCSListInfoResponse();

		if(null != path && path.length() > 0 && null != key && key.length() > 0){
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Method, Value_Method));
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_AccessToken, getAccessToken()));
			params.add(new BasicNameValuePair(Key_KeyWord, key));
			params.add(new BasicNameValuePair(Key_Recursive, recursive ? "1" : "0"));

			// build url
			String url = BaiduPCSActionBase.PCSRequestUrl + "/" + mbCommand + "?" +buildParams(params);

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
	private final static String Value_Method = "search";
	
	// key world
	private final static String Key_KeyWord = "wd";
	
	// key : recursive
	private final static String Key_Recursive = "re";
}
