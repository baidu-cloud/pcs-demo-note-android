package com.baidu.pcsdemonote;

import java.util.ArrayList;

import android.os.Handler;
import android.view.Menu;

import com.baidu.oauth2.BaiduOAuth;

public class PCSDemoInfo {
	
	//Judge whether the program is finish
	public static int flag = 0;
	
	//Judge whether the file name is empty or exits
	
	public static BaiduOAuth mbOauth = null;
	
	//save access_token
	public static String access_token = null; 
	
	//The file in the cloud of the storage of folders
	public final static String mbRootPath = "/apps/ÔÆ¶Ë¼ÇÊÂ±¾/";
	
	//UI thread
	public static Handler uiThreadHandler = null;
	
	//The file in the clouds of the storage of name
	public static ArrayList<String> fileNameList = new ArrayList<String>();

  
    /*
     * mbApiKey should be your app_key, please instead of "your app_key"
     */
	public final static String app_key = "A364CFOoZtNnrsRlusRbHK5r"; 
	
	public static String fileName = null;
	
	public static String fileTitle = null;
	
	public static int fileFlag = 0;
	
	//the path to the file storage on cloud
	public static String sourceFile = null;
	
	//the path to the file storage on local
	public static String target = null;
	
	public static String fileContent = null;
	
	
	//activity status :create£¨0£©¡¢edit£¨1£©¡¢content list£¨2£©
	public static int statu = 2;
	
	//
    public static final int ITEM0=Menu.FIRST;//System value
    public static final int ITEM1=Menu.FIRST+1;

}
