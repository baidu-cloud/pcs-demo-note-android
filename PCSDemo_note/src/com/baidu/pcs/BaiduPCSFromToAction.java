package com.baidu.pcs;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public class BaiduPCSFromToAction extends BaiduPCSActionBase{

	BaiduPCSFromToAction(String action){
		mbAction = action;
	}
	
	//
	// move file
	//
	protected final PCSActionInfo.PCSFileFromToResponse execute(String from, String to){
		List<PCSActionInfo.PCSFileFromToInfo> info = new ArrayList<PCSActionInfo.PCSFileFromToInfo>();
		PCSActionInfo.PCSFileFromToInfo data = new PCSActionInfo.PCSFileFromToInfo();
		data.from = from;
		data.to = to;
		
		info.add(data);
		
		return execute(info);
	}
	
	//
	// move file
	//
	protected final PCSActionInfo.PCSFileFromToResponse execute(List<PCSActionInfo.PCSFileFromToInfo> info){
		PCSActionInfo.PCSFileFromToResponse ret = new PCSActionInfo.PCSFileFromToResponse();

		if(null != info && info.size() > 0){
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Method, mbAction));
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_AccessToken, getAccessToken()));

			// build url
			String url = BaiduPCSActionBase.PCSRequestUrl + "/" + mbCommand + "?" +buildParams(params);

			List<NameValuePair> pairs = buildBodyParamsWithFromTo(info);

			HttpPost post = new HttpPost(url);
			try {

				post.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
				BaiduPCSActionBase.PCSRawHTTPResponse response = sendHttpRequest(post);
				
				if(null != response)
				{
					ret.message = response.message;
					
					if(null != response.response){
						ret = parseFileFromToExtraInfo(response.response);
					}
				}

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				ret.message = e.getMessage();
			}


		}

		return ret;
	}
	
	// value of method
	private String mbAction = null;
}
