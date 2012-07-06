package com.baidu.pcs;

import java.util.ArrayList;
import java.util.List;

//
// the HTTP request in this class is handled synchronously
// if user uses this class in UI thread, the UI thread will definitely be blocked, in order to avoid blocking UI, we suggest use this class in a work thread
//
public class BaiduPCSAPI {
	
	//
	// get space info, quota
	//
	public PCSActionInfo.PCSQuotaResponse quota(){
		return quota(null);
	}
	
	//
	// get space info, quota
	//
	public PCSActionInfo.PCSQuotaResponse quota(String path){
		
		BaiduPCSQuotaInfo info = new BaiduPCSQuotaInfo();
		info.setAccessToken(mbAccessToken);
		PCSActionInfo.PCSQuotaResponse space = info.quotaInfo(path);
		
		return space;
	}
	
	//
	// upload file
	//
	public PCSActionInfo.PCSFileInfoResponse uploadFile(String source, String target, BaiduPCSStatusListener listener){
		BaiduPCSUploader uploader = new BaiduPCSUploader();
		uploader.setAccessToken(mbAccessToken);
		return uploader.uploadFile(source, target, listener);
	}
	
	//
	// delete file
	//
	public PCSActionInfo.PCSSimplefiedResponse deleteFile(String file){
		ArrayList<String> files = new ArrayList<String>();
		files.add(file);
		BaiduPCSDeleter deleter = new BaiduPCSDeleter();
		return deleter.deleteFiles(files);
	}
	
	//
	// delete files
	//
	public PCSActionInfo.PCSSimplefiedResponse deleteFiles(List<String> files){
		BaiduPCSDeleter deleter = new BaiduPCSDeleter();
		deleter.setAccessToken(mbAccessToken);
		return deleter.deleteFiles(files);
	}
	
	//
	//download file
	//
	public PCSActionInfo.PCSSimplefiedResponse downFile(String source, String target){
		return downloadFile(source, target, null);
	}
	
	//
	//download file
	//
	public PCSActionInfo.PCSSimplefiedResponse downloadFile(String source, String target, BaiduPCSStatusListener listener){
		BaiduPCSDownloader downloader = new BaiduPCSDownloader();
		downloader.setAccessToken(mbAccessToken);
		return downloader.downloadFile(source, target, listener);
	}
	//
	// make dir, create a folder
	//
	public PCSActionInfo.PCSFileInfoResponse makeDir(String path){
		BaiduPCSFolderMaker folderMaker = new BaiduPCSFolderMaker();
		folderMaker.setAccessToken(mbAccessToken);
		return folderMaker.makeDir(path);
	}
	
	//
	// meta info
	//
	public PCSActionInfo.PCSFileInfoResponse meta(String file){
		BaiduPCSMeta meta = new BaiduPCSMeta();
		meta.setAccessToken(mbAccessToken);
		return meta.meta(file);		
	}
	
	//
	// list info
	// @ by:  time   name   size
	// @ order : asc  dsc
	//
	public PCSActionInfo.PCSListInfoResponse list(String path, String by, String order){
		BaiduPCSList list = new BaiduPCSList();
		list.setAccessToken(mbAccessToken);
		return list.list(path, by, order);		
	}
	
	//
	// move
	//
	public PCSActionInfo.PCSFileFromToResponse move(String from, String to){

		List<PCSActionInfo.PCSFileFromToInfo> info = new ArrayList<PCSActionInfo.PCSFileFromToInfo>();
		PCSActionInfo.PCSFileFromToInfo data = new PCSActionInfo.PCSFileFromToInfo();
		data.from = from;
		data.to = to;
		
		info.add(data);
		
		return move(info);		
	}
	
	//
	// move
	//
	public PCSActionInfo.PCSFileFromToResponse move(List<PCSActionInfo.PCSFileFromToInfo> info){
		BaiduPCSMove move = new BaiduPCSMove();
		move.setAccessToken(mbAccessToken);
		return move.move(info);		
	}
	
	//
	// copy
	//
	public PCSActionInfo.PCSFileFromToResponse copy(String from, String to){

		List<PCSActionInfo.PCSFileFromToInfo> info = new ArrayList<PCSActionInfo.PCSFileFromToInfo>();
		PCSActionInfo.PCSFileFromToInfo data = new PCSActionInfo.PCSFileFromToInfo();
		data.from = from;
		data.to = to;
		
		info.add(data);
		
		return copy(info);		
	}
	
	//
	// copy
	//
	public PCSActionInfo.PCSFileFromToResponse copy(List<PCSActionInfo.PCSFileFromToInfo> info){
		BaiduPCSCopy copy = new BaiduPCSCopy();
		copy.setAccessToken(mbAccessToken);
		return copy.copy(info);		
	}
	
	//
	// copy
	//
	public PCSActionInfo.PCSListInfoResponse search(String path, String key, boolean recursive){
		BaiduPCSSearch search = new BaiduPCSSearch();
		search.setAccessToken(mbAccessToken);
		return search.search(path, key, recursive);		
	}
	//
	//stream list
	//
	public PCSActionInfo.PCSListInfoResponse streamlist(String type, String start, String limit){
		BaiduPCSListStream liststream = new BaiduPCSListStream();
		liststream.setAccessToken(mbAccessToken);
		return liststream.liststream(type, start, limit);
	}
	//
	// set the access token
	//
	public void setAccessToken(String token){
		mbAccessToken = token;
	}
	
	//
	// get the access token
	//
	public String accessToken(){
		return mbAccessToken;
	}

	// record the access token
	private String mbAccessToken = null;
}
