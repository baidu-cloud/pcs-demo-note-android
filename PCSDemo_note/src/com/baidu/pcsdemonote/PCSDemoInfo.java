package com.baidu.pcsdemonote;

import java.util.ArrayList;

import android.os.Handler;
import android.view.Menu;

import com.baidu.oauth2.BaiduOAuth;

public class PCSDemoInfo {
	
	//判断程序是否结束的标志
	public static int flag = 0;
	
	//判断文件名是否为空，是否已存在的标志
	public static int fileFlag = 0;
	
	public static BaiduOAuth mbOauth = null;
	
	//保存access_token
	public static String access_token = null; 
	
	//该应用在云端上存储的文件夹
	public final static String mbRootPath = "/apps/云端记事本/";
	
	//UI操作线程
	public static Handler uiThreadHandler = null;
	
	//存储云端上文件名
	public static ArrayList<String> fileNameList = new ArrayList<String>();

  
    //mbApikey为开发者中心为应用分配的app_key,使用自己应用的app_key代替
	public final static String app_key = "A364CFOoZtNnrsRlusRbHK5r"; 
	
	public static String fileName = null;
	
	public static String fileTitle = null;
	
	//上传到云端文件存放位置
	public static String sourceFile = null;
	
	//云端下载存放位置
	public static String target = null;
	
	public static String fileContent = null;
	
	
	//当前的状态是创建（0）、编辑（1）、内容显示（2）
	public static int statu = 2;
	
	//菜单排序值
    public static final int ITEM0=Menu.FIRST;//系统值
    public static final int ITEM1=Menu.FIRST+1;

}
