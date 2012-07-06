package com.baidu.pcs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

//
// upload file
//
class BaiduPCSUploader extends BaiduPCSActionBase{
	//
	//upload file
	//
	public PCSActionInfo.PCSFileInfoResponse uploadFile(String source, String target, BaiduPCSStatusListener listener){
		UploadTask task = new UploadTask();
		task.source = source;
		task.target = target;
		task.listener = listener;
		
		return startUploadingByTask(task);
	}
	
	//
	// upload file piece by piece
	//
	private PCSActionInfo.PCSFileInfoResponse startUploadingByTask(UploadTask task){

		PCSActionInfo.PCSFileInfoResponse ret = new PCSActionInfo.PCSFileInfoResponse();

		if(null != task.source && null != task.target && task.source.length() > 0 && task.target.length() > 0){
			File file = new File(task.source);

			if(null != file && file.length() > 0){
				long length = file.length();

				if((long)MaxPieceSize > length){
					ret = uploadFileInSinglePiece(file, task.target);
				}
				else{
					ret = uploadFileInMutiplePieces(file, task);
				}
			}
		}

		return ret;
	}
	
	//
	// upload file as single piece
	//
	private PCSActionInfo.PCSFileInfoResponse uploadFileInSinglePiece(File file, String target){
		
		PCSActionInfo.PCSFileInfoResponse ret = new PCSActionInfo.PCSFileInfoResponse();
		
		try {
			RandomAccessFile accFile = new RandomAccessFile(file, "r");
			byte[] bytes = new byte[(int)file.length()];
			accFile.readFully(bytes);
			
			int index = target.lastIndexOf("/");
			String suffix = target.substring(index + 1, target.length()); 
			
			ret = uploadPiece(bytes, target, suffix);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			ret.message = e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ret.message = e.getMessage();
		}
		
		return ret;
	}
	
	//
	// upload file as mutiple pieces
	//
	private PCSActionInfo.PCSFileInfoResponse uploadFileInMutiplePieces(File file, UploadTask task){

		PCSActionInfo.PCSFileInfoResponse ret = new PCSActionInfo.PCSFileInfoResponse();
		List<PCSActionInfo.PCSFileInfoResponse> results = new ArrayList<PCSActionInfo.PCSFileInfoResponse>();

		if(null != file && null != task){

			long length = file.length();

			boolean error = false;

			try {
				RandomAccessFile accFile = new RandomAccessFile(file, "r");
				long doneLength = 0;

				long beginMS = 0;
				
				if(null != task.listener){
					task.listener.onProgress(0, length);
				}

				int maxSize = MaxPieceSize;
				int bigger = 1;


				while((long)maxSize * (long)Max_Pieces < length){
					maxSize = Max_Pieces * (++bigger);
				}

				int index = task.target.lastIndexOf("/");
				String prefix = task.target.substring(0, index + 1);
				String suffix = task.target.substring(index + 1, task.target.length()); 

				while(doneLength < length){

					int toLength = maxSize;
					if(maxSize + doneLength > length){
						toLength = (int)(length - doneLength);
					}

					byte[] bytes = new byte[toLength];

					accFile.seek(doneLength);

					accFile.readFully(bytes);

				
					long t = System.currentTimeMillis();

					String fileName = "tmp" + t + suffix;
					String targetFile = prefix + fileName;

					PCSActionInfo.PCSFileInfoResponse result = uploadPiece(bytes, targetFile, fileName);

					if(null != result && 0 == result.error_code){
						results.add(result);

						if(null != task.listener){
							long endMS = System.currentTimeMillis();
							
							if(endMS - beginMS >= task.listener.progressInterval()){
								task.listener.onProgress(doneLength, length);
								beginMS = endMS;
							}

							if(false == task.listener.toContinue()){

								// it is required to stop
								error = true;
								ret.message = "User stopped uploading";
								break;
							}
						}
					}
					else{
						ret.error_code = result.error_code;
						ret.message = result.message;
						error = true;
						break;
					}

					doneLength += toLength;
				}

				if(false == error){
					
					List<String> md5s = new ArrayList<String>();
					
					for(int i = 0; i < results.size(); ++i){
						
						PCSActionInfo.PCSFileInfoResponse tmp = results.get(i);
						
						if(null != tmp){
							md5s.add(tmp.md5);
						}
					}
					
					ret = createSuperFile(md5s, task.target);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				ret.message = e.getMessage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ret.message = e.getMessage();
			}
		}
		
		// delete the tmp files no matter what
		List<String> tmpFiles = new ArrayList<String>();
		for(int i = 0; i < results.size(); ++i){
			
			PCSActionInfo.PCSFileInfoResponse tmp = results.get(i);
			
			if(null != tmp){
				tmpFiles.add(tmp.path);
			}
		}
		
		deleteTmpUploadedFiles(tmpFiles);
		
		return ret;
	}
	
	//
	// delete the tmp files
	//
	private void deleteTmpUploadedFiles(List<String> files){
		BaiduPCSDeleter deleter = new BaiduPCSDeleter();
		deleter.setAccessToken(getAccessToken());
		
		deleter.deleteFiles(files);
	}

	//
	// upload piece
	//
	private PCSActionInfo.PCSFileInfoResponse uploadPiece(byte[] bytes, String target, String file){

		PCSActionInfo.PCSFileInfoResponse ret = new PCSActionInfo.PCSFileInfoResponse();

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Method, Value_Method_Upload));
		params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_AccessToken, getAccessToken()));
		params.add(new BasicNameValuePair(Key_Path, target));

		String url = BaiduPCSActionBase.PCSRequestUrl + "/" + mbCommand + "?" + buildParams(params);

		if(null != url && url.length() > 0){

			HttpPost post = new HttpPost(url);

			MultipartEntity entity = new MultipartEntity();
			ContentBody bsData = new ByteArrayBody(bytes, file);
			entity.addPart("uploadedfile", bsData);

			post.setEntity(entity);

			BaiduPCSActionBase.PCSRawHTTPResponse response = sendHttpRequest(post);

			if(null != response){
				ret.message = response.message;
				if(null != response.response){
					ret = parseFileInfo(response.response);
				}
			}
		}

		return ret;
	}
	
	//
	// create super file
	//
	private PCSActionInfo.PCSFileInfoResponse createSuperFile(List<String> md5s, String target){

		PCSActionInfo.PCSFileInfoResponse ret = new PCSActionInfo.PCSFileInfoResponse();

		if(null != md5s && md5s.size() > 0 && null != target && target.length() > 0){
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_Method, Value_Method_CreateSuperFile));
			params.add(new BasicNameValuePair(BaiduPCSActionBase.Key_AccessToken, getAccessToken()));
			params.add(new BasicNameValuePair(Key_Path, target));

			String url = BaiduPCSActionBase.PCSRequestUrl + "/" + mbCommand + "?" + buildParams(params);

			List<NameValuePair> bodyParams = new ArrayList<NameValuePair>();

			if (md5s != null) {
				JSONArray json = new JSONArray(md5s);
				Map<String, JSONArray> map = new HashMap<String, JSONArray>();
				map.put(Key_BlockList, json);

				JSONObject md5list = new JSONObject(map);

				bodyParams.add(new BasicNameValuePair(Key_Param, md5list.toString()));
			}

			HttpPost post = new HttpPost(url);
			try {
				post.setEntity(new UrlEncodedFormEntity(bodyParams, "utf-8"));

				for(int i = 0; i < Max_Retries; ++i){
					
					BaiduPCSActionBase.PCSRawHTTPResponse response = sendHttpRequest(post);

					if(null != response){
						ret.message = response.message;
						ret = parseFileInfo(response.response);
						
						if(null != ret && 0 == ret.error_code){
							break;
						}
						else{
							try {
								Thread.sleep(1000 * (i + 1));
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				ret.message = e.getMessage();
			}
		}

		return ret;
	}
	
	// 
	// the task to upload file
	//
	private class UploadTask{
		public String source = null;
		public String target = null;
		public BaiduPCSStatusListener listener = null;
	}

	// the max piece size
	private final static int MaxPieceSize = 1024 * 500;
	
	// the value of method
	private final static String Value_Method_Upload = "upload";
	
	// the value of method -- createsuperfile
	private final static String Value_Method_CreateSuperFile = "createsuperfile";
	
	// key of block list
	private final static String Key_BlockList = "block_list";
	
	// max pieces
	private final static int Max_Pieces = 1024;
}
