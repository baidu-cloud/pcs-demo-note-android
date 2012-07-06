package com.baidu.pcs;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BaiduPCSMeta extends BaiduPCSActionBase{

	//
	// get file meta info
	//
	public PCSActionInfo.PCSFileInfoResponse meta(String file){
		PCSActionInfo.PCSFileInfoResponse ret = new PCSActionInfo.PCSFileInfoResponse();

		if(null != file && file.length() > 0){
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Method, Value_Method));
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_AccessToken, getAccessToken()));
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Path, file));

			// build url
			String url = BaiduPCSActionBase.PCSRequestUrl + "/" + mbCommand + "?" +buildParams(params);

			HttpGet httpget = new HttpGet(url);
			BaiduPCSActionBase.PCSRawHTTPResponse response = sendHttpRequest(httpget);
			
			if(null != response){
				
				ret.message = response.message;
				
				if(null != response.response){

					try {
						HttpEntity resEntity = response.response.getEntity();
						String json = EntityUtils.toString(resEntity);

						JSONObject jo = new JSONObject(json);

						if(null != jo){
							if(jo.has(Key_ErrorCode)){ // get error, failed to upload piece
								ret.error_code = jo.getInt(Key_ErrorCode);

								if(jo.has(Key_ErrorMessage)){
									ret.message = jo.getString(Key_ErrorMessage);
								}
							}else{
								if(jo.has(Key_Files_List)){
									JSONArray list = jo.getJSONArray(Key_Files_List);

									if(null != list && list.length() > 0){
										JSONObject o = list.getJSONObject(0);
										ret = parseFileInfoByJSONObject(o);
									}
								}
							}
						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						ret.message = e.getMessage();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						ret.message = e.getMessage();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						ret.message = e.getMessage();
					}
				}

			}
		}


		return ret;
	}
	
	// value of method
	private final static String Value_Method = "meta";
	
	// key : list
	private final static String Key_Files_List = "list";
}
