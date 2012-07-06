package com.baidu.pcs;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

//
// delete file
//
public class BaiduPCSDeleter extends BaiduPCSActionBase{

	//
	// delete single file
	//
	public PCSActionInfo.PCSSimplefiedResponse deleteFile(String file){
		ArrayList<String> files = new ArrayList<String>();
		files.add(file);
		return deleteFiles(files);
	}

	//
	// delete files
	//
	public PCSActionInfo.PCSSimplefiedResponse deleteFiles(List<String> files){

		PCSActionInfo.PCSSimplefiedResponse ret = new PCSActionInfo.PCSSimplefiedResponse();

		if(null != files && files.size() > 0){
			// set url params
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Method, Value_Method));
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_AccessToken, getAccessToken()));

			// build url
			String url = BaiduPCSActionBase.PCSRequestUrl + "/" + mbCommand + "?" +buildParams(params);

			List<NameValuePair> pairs = buildBodyParams(files);

			HttpPost post = new HttpPost(url);

			try {
				post.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));

				BaiduPCSActionBase.PCSRawHTTPResponse response = sendHttpRequest(post);

				if(null != response){
					ret.message = response.message;

					if(null != response.response){
						ret = parseSimplefiedResponse(response.response);
					}
				}

			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				ret.message = e1.getMessage();
			}

		}


		return ret;
	}
	
	//
	// build body params
	//
	private List<NameValuePair> buildBodyParams(List<String> files){
		List<NameValuePair> bodyParams = new ArrayList<NameValuePair>();
		
		JSONArray array = new JSONArray();
		
		for(int i = 0; i < files.size(); ++i){
			Map<String, String> map = new HashMap<String, String>();
			map.put(Key_Files_Path, files.get(i));
			
			JSONObject node = new JSONObject(map);
			array.put(node);
		}

		Map<String, JSONArray> list = new HashMap<String, JSONArray>();
		list.put(Key_Files_List, array);
		JSONObject nodelist = new JSONObject(list);
		
		bodyParams.add(new BasicNameValuePair(Key_Param, nodelist.toString()));
		
		return bodyParams;
	}
	
	// Value of method: delete
	private final static String Value_Method = "delete";
	
	// key : path
	private final static String Key_Files_Path = "path";
}
