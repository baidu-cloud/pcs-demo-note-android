package com.baidu.pcs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class BaiduPCSActionBase {
	//
	// set the access token
	//
	public void setAccessToken(String token){
		mbAccessToken = token;
	}
	
	//
	// get the access token
	//
	public String getAccessToken(){
		return mbAccessToken;
	}

	//
	// send HTTP Request
	//
	protected PCSRawHTTPResponse sendHttpRequest(HttpRequestBase request){
		// response
		PCSRawHTTPResponse ret = new PCSRawHTTPResponse();

		if(null != request){
			
			// create client
			HttpClient client = HttpClientFactory.makeHttpClient();

			if(null != client){

				for (int retries = 0; ret.response == null && retries < Max_Retries; ++retries) {
					/*
					 * The try/catch is a workaround for a bug in the HttpClient libraries. It should be returning null
					 * instead when an error occurs. Fixed in HttpClient 4.1, but we're stuck with this for now. See:
					 * http://code.google.com/p/android/issues/detail?id=5255
					 */
					try {
						ret.response = client.execute(request);
					} catch (NullPointerException e) {
						ret.message = e.getMessage();
					} catch (ClientProtocolException e) {
						ret.message = e.getMessage();
					} catch (IOException e) {
						ret.message = e.getMessage();
					}
					
					if(null == ret.response){
						try {
							Thread.sleep(1000 * (retries + 1));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							ret.message = e.getMessage();
						}
					}
				}
			}
		}

		return ret;
	}
	
	//
	// build url
	//
	protected String buildParams(List<NameValuePair> urlParams){
		
		String ret = null;
		
		if(null != urlParams && urlParams.size() > 0){
			
			try {
				HttpEntity paramsEntity = new UrlEncodedFormEntity(urlParams, "utf8");
				ret = EntityUtils.toString(paramsEntity);
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	/*
	 * parse the response for both piece upload and create super file
	 */
	protected PCSActionInfo.PCSFileInfoResponse parseFileInfo(HttpResponse response){
		
		PCSActionInfo.PCSFileInfoResponse ret = new PCSActionInfo.PCSFileInfoResponse();
		
		try {
			HttpEntity resEntity = response.getEntity();
			String json = EntityUtils.toString(resEntity);
			
			ret = parseFileInfoByJson(json);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			ret.message = e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ret.message = e.getMessage();
		}
		
		return ret;
	}
	
	/*
	 * parse json
	 */
	protected PCSActionInfo.PCSFileInfoResponse parseFileInfoByJson(String json){
		PCSActionInfo.PCSFileInfoResponse ret = new PCSActionInfo.PCSFileInfoResponse();
		if(null != json && json.length() > 0){
			try {
				JSONObject jo = new JSONObject(json);
				ret = parseFileInfoByJSONObject(jo);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				ret.message = e.getMessage();
			}
		}

		return ret;
	}
	
	/*
	 * parse file info based on JSONObject
	 */
	 protected PCSActionInfo.PCSFileInfoResponse parseFileInfoByJSONObject(JSONObject jo){
		 PCSActionInfo.PCSFileInfoResponse ret = new PCSActionInfo.PCSFileInfoResponse();
			try {
				if(null != jo){
					if(jo.has(Key_ErrorCode)){ // get error, failed to upload piece
						ret.error_code = jo.getInt(Key_ErrorCode);

						if(jo.has(Key_ErrorMessage)){
							ret.message = jo.getString(Key_ErrorMessage);
						}
					}
					else{ // success, we need to parse the parameters
						ret.error_code = 0;

						if(jo.has(Key_MD5)){
							ret.md5 = jo.getString(Key_MD5);
						}
						
						if(jo.has(Key_BlockList)){
							ret.md5 = jo.getString(Key_BlockList);
						}

						if(jo.has(Key_Path)){
							ret.path = jo.getString(Key_Path);
						}

						if(jo.has(Key_FSID)){
							ret.fs_id = jo.getInt(Key_FSID);
						}

						if(jo.has(Key_Size)){
							ret.size = jo.getInt(Key_Size);
						}

						if(jo.has(Key_CTime)){
							ret.cTime = jo.getLong(Key_CTime);
						}

						if(jo.has(Key_MTime)){
							ret.mTime = jo.getLong(Key_MTime);
						}

						if(jo.has(Key_IsDir)){
							int isdir = jo.getInt(Key_IsDir);
							ret.isDir = (0 == isdir ? false : true);
						}
						
						if(jo.has(Key_HasSubFolder)){
							int subFolder = jo.getInt(Key_HasSubFolder);
							ret.hasSubFolder = (0 == subFolder ? false : true);
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				ret.message = e.getMessage();
			}
		 return ret;
	 }
	
	/*
	 * parse simplefied response
	 */
	protected PCSActionInfo.PCSSimplefiedResponse parseSimplefiedResponse(HttpResponse response){

		PCSActionInfo.PCSSimplefiedResponse ret = new PCSActionInfo.PCSSimplefiedResponse();
		
		if(null != response){
			try {
				String json = EntityUtils.toString(response.getEntity());
				JSONObject jo = new JSONObject(json);

				if(null != jo){

					if(jo.has(Key_ErrorCode)){ // get error, failed to upload piece
						ret.error_code = jo.getInt(Key_ErrorCode);

						if(jo.has(Key_ErrorMessage)){
							ret.message = jo.getString(Key_ErrorMessage);
						}
					}
					else{
						ret.error_code = 0;
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
		
		return ret;
	}
	
	/*
	 * parse  extra info for file moving or copying
	 */
	protected PCSActionInfo.PCSFileFromToResponse parseFileFromToExtraInfo(HttpResponse response){

		PCSActionInfo.PCSFileFromToResponse ret = new PCSActionInfo.PCSFileFromToResponse();

		if(null != response){
			try {

				HttpEntity resEntity = response.getEntity();
				String json = EntityUtils.toString(resEntity);

				JSONObject root = new JSONObject(json);

				if(null != root){

					if(root.has(Key_ErrorCode)){
						ret.error_code = root.getInt(Key_ErrorCode);

						if(root.has(Key_ErrorMessage)){
							ret.message = root.getString(Key_ErrorMessage);
						}
					}
					else{
						ret.error_code = 0;
					}

					if(root.has(Key_Extra)){
						JSONObject o = root.getJSONObject(Key_Extra);

						if(null != o){
							JSONArray array = o.getJSONArray(Key_Files_List);
							if(null != array && array.length() > 0)
							{
								ret.list = new ArrayList<PCSActionInfo.PCSFileFromToInfo>();

								for(int i = 0; i < array.length(); ++i){
									JSONObject jo = array.getJSONObject(i);

									PCSActionInfo.PCSFileFromToInfo info = new PCSActionInfo.PCSFileFromToInfo();

									if(null != jo){
										if(jo.has(Key_From)){
											info.from = jo.getString(Key_From);
										}

										if(jo.has(Key_to)){
											info.to = jo.getString(Key_to);
										}

										ret.list.add(info);
									}

								}
							}
						}
					}
				}


			} catch (ParseException e) {
				// TODO Auto-generated catch block
				ret.message = e.getMessage();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				ret.message = e.getMessage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ret.message = e.getMessage();
			}
		}

		return ret;
	}
	
	/*
	 * parse list response
	 */
	protected PCSActionInfo.PCSListInfoResponse parseListResponse(HttpResponse response){
		PCSActionInfo.PCSListInfoResponse ret = new PCSActionInfo.PCSListInfoResponse();
		
		if(null != response){
			try {
				HttpEntity resEntity = response.getEntity();
				String json = EntityUtils.toString(resEntity);

				JSONObject jo = new JSONObject(json);

				if(null != jo){
					if(jo.has(Key_ErrorCode)){ // get error, failed to upload piece
						ret.error_code = jo.getInt(Key_ErrorCode);

						if(jo.has(Key_ErrorMessage)){
							ret.message = jo.getString(Key_ErrorMessage);
						}
					}else{
						ret.error_code = 0;
						if(jo.has(Key_Files_List)){
							JSONArray list = jo.getJSONArray(Key_Files_List);
							ret.list = new ArrayList<PCSActionInfo.PCSFileInfoResponse>();
							
							for(int i = 0; i < list.length(); ++i){
								PCSActionInfo.PCSFileInfoResponse info = parseFileInfoByJSONObject(list.getJSONObject(i));
								ret.list.add(info);
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
		
		return ret;
	}
	
	//
	// build body params
	//
	protected List<NameValuePair> buildBodyParamsWithFromTo(List<PCSActionInfo.PCSFileFromToInfo> info){
		List<NameValuePair> bodyParams = new ArrayList<NameValuePair>();
		
		JSONArray array = new JSONArray();
		
		for(int i = 0; i < info.size(); ++i){
			Map<String, String> map = new HashMap<String, String>();
			PCSActionInfo.PCSFileFromToInfo tmp = info.get(i);
			map.put(Key_From, tmp.from);
			map.put(Key_to, tmp.to);
			
			JSONObject node = new JSONObject(map);
			array.put(node);
		}

		Map<String, JSONArray> list = new HashMap<String, JSONArray>();
		list.put(Key_Files_List, array);
		JSONObject nodelist = new JSONObject(list);
		
		bodyParams.add(new BasicNameValuePair(Key_Param, nodelist.toString()));
		
		return bodyParams;
	}
	
	/*
	 * the data structure of quota, including the space info
	 */
	protected static class PCSRawHTTPResponse {		
		// the response
		public HttpResponse response = null;
		
		// status message if failed
		public String message = null;
	}
	
	// the request url
	final static String PCSRequestUrl = "https://pcs.baidu.com/rest/2.0/pcs";
	
	// the key of method
	final static String Key_Method = "method";
	
	// the key of access token
	final static String Key_AccessToken = "access_token";
	
	// key of error code
	final static String Key_ErrorCode = "error_code";
	
	// key of error message
	final static String Key_ErrorMessage = "error_msg";
	
	// the key of path
	final static String Key_Path = "path";
	
	// key of param
	final static String Key_Param = "param";
	
	// the value of command
	final static String mbCommand = "file";
	
	// the value of command
	final static String sbCommand = "stream";
	
	// key of md5
	final static String Key_MD5 = "md5";
	
	// key block list
	final static String Key_BlockList = "block_list";
	
	// key : from
	final static String Key_From = "from";
	
	// key : to
	final static String Key_to = "to";
	
	
	// max retries times
	final static int Max_Retries = 6;
	
	// key : list
	final static String Key_Files_List = "list";
	
	// key of fs_id
	private final static String Key_FSID = "fs_id";
	
	// key of size
	private final static String Key_Size = "size";
	
	// key of c time
	private final static String Key_CTime = "ctime";
	
	// key of mtime
	private final static String Key_MTime = "mtime";
	
	// key : isdir
	private final static String Key_IsDir = "isdir";
	
	// key : extra
	private final static String Key_Extra = "extra";
	
	// key : has sub folder
	private final static String Key_HasSubFolder = "ifhassubdir";

	
	// the access token
	private String mbAccessToken = null;
}
