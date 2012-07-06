package com.baidu.pcs;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

// 
// get the space info
//
class BaiduPCSQuotaInfo extends BaiduPCSActionBase{

	//
	// get space info
	//
	PCSActionInfo.PCSQuotaResponse quotaInfo(){
		return quotaInfo(null);
	}
	
	//
	// get space info of specific folder
	//
	PCSActionInfo.PCSQuotaResponse quotaInfo(String folder){

		PCSActionInfo.PCSQuotaResponse ret = new PCSActionInfo.PCSQuotaResponse();

		// set url params
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Method, Value_Method));
		params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_AccessToken, getAccessToken()));

		if(null != folder && folder.length() > 0){
			params.add(new BasicNameValuePair(Key_Path, folder));
		}

		// build url
		String url = BaiduPCSActionBase.PCSRequestUrl + "/" + mbCommand + "?" +buildParams(params);

		if(null != url && url.length() > 0){
			HttpGet httpget = new HttpGet(url);
			BaiduPCSActionBase.PCSRawHTTPResponse response = sendHttpRequest(httpget);
			
			if(null != response){
				ret.message = response.message;
				if(null != response.response){
					HttpEntity resEntity = response.response.getEntity();
					String json;
					try {
						json = EntityUtils.toString(resEntity);
						JSONObject jo = new JSONObject(json);

						if(null != jo){
							if(jo.has(Key_ErrorCode)){ // get error, failed to upload piece
								ret.error_code = jo.getInt(Key_ErrorCode);

								if(jo.has(Key_ErrorMessage)){
									ret.message = jo.getString(Key_ErrorMessage);
								}
							}
							else if(jo.has(Key_Total)){
								ret.total = jo.getLong(Key_Total);
								ret.usded = jo.getLong(Key_Used);
								ret.error_code = 0;
							}
						}

					} catch (ParseException e) {
						ret.message = e.getMessage();
					} catch (IOException e) {
						ret.message = e.getMessage();
					} catch (JSONException e) {
						ret.message = e.getMessage();
					}					
				}
			}
		}
		else{
			ret.message = "Invalid Url";
		}
		return ret;
	}
	
	// the command
	private final static String mbCommand = "quota";
	
	// the value of method
	private final static String Value_Method = "info";
	
	// the key of total
	private final static String Key_Total = "quota";
	
	// the key of used
	private final static String Key_Used = "used";
}
