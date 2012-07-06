package com.baidu.pcs;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

//
// make folder
//
public class BaiduPCSFolderMaker extends BaiduPCSActionBase{

	//
	// make folder
	//
	public PCSActionInfo.PCSFileInfoResponse makeDir(String path){
		PCSActionInfo.PCSFileInfoResponse ret = new PCSActionInfo.PCSFileInfoResponse();
		
		if(null != path && path.length() > 0){
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Method, Value_Method));
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_AccessToken, getAccessToken()));
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Path, path));
			
			// build url
			String url = BaiduPCSActionBase.PCSRequestUrl + "/" + mbCommand + "?" +buildParams(params);
			
			HttpPost httppost = new HttpPost(url);
			BaiduPCSActionBase.PCSRawHTTPResponse response = sendHttpRequest(httppost);
			
			if(null != response){
				ret.message = response.message;
				
				if(null != response.response){
					ret = parseFileInfo(response.response);			
					if(null != ret){
						ret.isDir = true;
						ret.size = 0;
					}
				}
			}
		}
		
		return ret;
	}
	
	// value of method
	private final static String Value_Method = "mkdir";
}
