package com.baidu.pcsdemonote;

import java.util.ArrayList;

import android.os.Handler;
import android.view.Menu;

import com.baidu.oauth2.BaiduOAuth;

public class PCSDemoInfo {
	
	//�жϳ����Ƿ�����ı�־
	public static int flag = 0;
	
	//�ж��ļ����Ƿ�Ϊ�գ��Ƿ��Ѵ��ڵı�־
	public static int fileFlag = 0;
	
	public static BaiduOAuth mbOauth = null;
	
	//����access_token
	public static String access_token = null; 
	
	//��Ӧ�����ƶ��ϴ洢���ļ���
	public final static String mbRootPath = "/apps/�ƶ˼��±�/";
	
	//UI�����߳�
	public static Handler uiThreadHandler = null;
	
	//�洢�ƶ����ļ���
	public static ArrayList<String> fileNameList = new ArrayList<String>();

  
    //mbApikeyΪ����������ΪӦ�÷����app_key,ʹ���Լ�Ӧ�õ�app_key����
	public final static String app_key = "A364CFOoZtNnrsRlusRbHK5r"; 
	
	public static String fileName = null;
	
	public static String fileTitle = null;
	
	//�ϴ����ƶ��ļ����λ��
	public static String sourceFile = null;
	
	//�ƶ����ش��λ��
	public static String target = null;
	
	public static String fileContent = null;
	
	
	//��ǰ��״̬�Ǵ�����0�����༭��1����������ʾ��2��
	public static int statu = 2;
	
	//�˵�����ֵ
    public static final int ITEM0=Menu.FIRST;//ϵͳֵ
    public static final int ITEM1=Menu.FIRST+1;

}
