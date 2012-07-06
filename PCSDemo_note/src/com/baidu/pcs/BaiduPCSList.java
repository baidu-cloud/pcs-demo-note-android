package com.baidu.pcs;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
public class BaiduPCSList extends BaiduPCSActionBase{

	//
	// list folder
	//
	// @ by:  time   name   size
	// @ order : asc dsc
	//
	public PCSActionInfo.PCSListInfoResponse list(String path, String by, String order){
		PCSActionInfo.PCSListInfoResponse ret = new PCSActionInfo.PCSListInfoResponse();

		if(null != path && path.length() > 0){
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Method, Value_Method));
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_AccessToken, getAccessToken()));
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Path, path));

			if(null != by && by.length() > 0){
				params.add(new BasicNameValuePair(Key_By, by));
			}

			if(null != order && order.length() > 0){
				params.add(new BasicNameValuePair(Key_Order, order));
			}

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
	private final static String Value_Method = "list";
	
	// key : by
	private final static String Key_By = "by";
	
	// key : order
	private final static String Key_Order = "order";
}
