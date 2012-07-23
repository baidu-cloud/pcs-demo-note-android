package com.baidu.pcsdemonote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.http.util.EncodingUtils;
import com.baidu.oauth2.BaiduOAuthViaDialog;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.baidu.pcs.BaiduPCSAPI;
import com.baidu.pcs.BaiduPCSStatusListener;
import com.baidu.pcs.PCSActionInfo;
import com.baidu.pcs.PCSActionInfo.PCSFileInfoResponse;
import com.baidu.pcsdemonote.BaiduPCSAction;


public class BaiduPCSAction {
	//该类使用PCS提供的API实现具体的功能

    // 与百度连接，获得access_token
    public void login(final Context context){
    	
    	if(null != PCSDemoInfo.access_token){
			Intent intent = new Intent();    				    						    				
			intent.setClass(context, ContentActivity.class); 				
			context.startActivity(intent); 
    	}else{
    		
    		PCSDemoInfo.mbOauth = new BaiduOAuthViaDialog(PCSDemoInfo.app_key);

        	try {
        		//启动百度OAuth对话框
        		PCSDemoInfo.mbOauth.startDialogAuth(context, new String[]{"basic", "netdisk"}, new BaiduOAuthViaDialog.DialogListener(){

        			//登陆成功后的操作
        			public void onComplete(Bundle values) {
        				//获得access_token
        				PCSDemoInfo.access_token = values.getString("access_token");
        				
        				Intent intent = new Intent();    				    						    				
        				intent.setClass(context, ContentActivity.class); 				
        				context.startActivity(intent);    				
        			}

        			// TODO: the error code need be redefined
        			@SuppressWarnings("unused")
    				public void onError(int error) {   				
        				Toast.makeText(context, R.string.fail, Toast.LENGTH_SHORT).show();
        			}

        			public void onCancel() {   				
        				Toast.makeText(context, R.string.back, Toast.LENGTH_SHORT).show();
        			}

        			public void onException(String arg0) {
        				Toast.makeText(context, arg0, Toast.LENGTH_SHORT).show();
        			}
        		});
        	} catch (Exception e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
    		
    	}
    	
    	
    }
    
    //上传文件到云端
    public void upload(final Context context){
    	
    	if(null != PCSDemoInfo.access_token){

    		Thread workThread = new Thread(new Runnable(){
				
    			public void run() {
									
		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		
		    		//为API设置access_token
		    		api.setAccessToken(PCSDemoInfo.access_token);
		    		
		    	    //使用pcs uploadFile API上传文件
					final PCSActionInfo.PCSFileInfoResponse response = api.uploadFile(PCSDemoInfo.sourceFile, PCSDemoInfo.mbRootPath+PCSDemoInfo.fileTitle+".txt", new BaiduPCSStatusListener(){

						@Override
						public void onProgress(long bytes, long total) {
							// TODO Auto-generated method stub					
						}
		    		});
		    		
					//对UI界面操作的线程
					PCSDemoInfo.uiThreadHandler.post(new Runnable(){
						
		    			public void run(){
		  
		    				if(response.error_code == 0){
		    					
		    					Toast.makeText(context,"上传成功", Toast.LENGTH_SHORT).show();
		    					
		    					//该文件作为上传的缓冲文件，上传成功后删除本地存储的文件
		    					File file = new File(PCSDemoInfo.sourceFile);
		    					file.delete();
		    					
	    					    //返回文件列表显示界面
		    					back(context);
		    					
		    				}else{
		    					
		    					Toast.makeText(context,"错误代码："+response.error_code, Toast.LENGTH_SHORT).show(); 
		    				}
		    				
		    			}
		    		});	
		    		
				}
			});
			 
    		workThread.start();
    	}
    }
    
  
    //列出云端上文件的信息
    public void list(final Context context){
    	
        if (null != PCSDemoInfo.access_token){
        	        	
    		Thread workThread = new Thread(new Runnable(){
    			
				public void run() {
					
		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		api.setAccessToken(PCSDemoInfo.access_token );
		    		
		    		//云端存储文件的路径
		    		String path = PCSDemoInfo.mbRootPath;
		    		
		    		//使用百度提供的list API
		    		final PCSActionInfo.PCSListInfoResponse ret = api.list(path, "name", "asc");
		    				    		
		    		PCSDemoInfo.uiThreadHandler.post(new Runnable(){
		    			
		    			public void run(){		    				
		    				//HashMpa为键值对类型。第一个参数为建，第二个参数为值 
		    				ArrayList<HashMap<String, String>> list =new ArrayList<HashMap<String,String>>();   
		    						    				
		    				//ret.list为云端反馈信息
		    				if("[]" != ret.list.toString()){
		    					   			    	            
			    	            for(Iterator<PCSFileInfoResponse> i = ret.list.iterator(); i.hasNext();){
			    	            	
			    	            	HashMap<String, String> map =new HashMap<String, String>();
			    	            				    	            	
			    	            	PCSFileInfoResponse info = i.next();
			    	            	
			    	            	//获得文件的名字（名字是网盘上的绝对路径）			    	            	
			    	         	    String path = info.path;			    	         	    
			    	         	    String fileName = path.substring(PCSDemoInfo.mbRootPath.length(),path.lastIndexOf("."));
			    	         	    
			    	         	    //获得上次修改的时间（以秒为单位）
			    	         	    Date date = new Date(info.mTime*1000);
			    	         	    
			    	         	    //修改时间的格式
			    	         	    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
			    	         	    String dateString = formatter.format(date);
		  			 			    			    	            	
			    	            	map.put("file_name", fileName);			    	            	
			    	            	map.put("time", dateString);
			    	            	
			    	            	//listview上每一行显示内容的list		
			    	            	list.add(map); 	            	
			    	            	PCSDemoInfo.fileNameList.add(fileName);							    				    	             
			    	            }			    	               
			    	        }else{
			    	        	
			    	        	//当云端没有文件，或者删除了最后一个文件刷新后，必须清空list
		    					list.clear();
		    					Toast.makeText(context, "您的文件夹为空！", Toast.LENGTH_SHORT).show();		    					
		    				}    
		    				//生成一个SimpleAdapter类型的变量来填充数据   
			    	         SimpleAdapter listAdapter =new SimpleAdapter(context, list, R.layout.content, new String[]{"file_name","time"}, new int[]{R.id.file_name,R.id.time});   
			    	        
			    	         //设置显示ListView 
			    	         ((ListActivity)context).setListAdapter(listAdapter);
		    	         
			    	         Toast.makeText(context, R.string.refresh, Toast.LENGTH_SHORT).show();

		    			}
		    		});	
		    		
				}
			});
			 
    		workThread.start();

        } 
    }
    
    
    public void download(final Context context){
    	
    	if(null != PCSDemoInfo.access_token){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		api.setAccessToken(PCSDemoInfo.access_token);
		    		
		    		//设置下载文件在云端存储的位置
		    		PCSDemoInfo.sourceFile = PCSDemoInfo.mbRootPath + PCSDemoInfo.fileTitle+".txt";
		    		
		    		//设置下载文件在本地存储位置
		    		PCSDemoInfo.target = context.getFilesDir()+"/"+PCSDemoInfo.fileTitle+".txt";
		    		
		    		//调用PCS downloadFile API
		    		final PCSActionInfo.PCSSimplefiedResponse ret = api.downloadFile(PCSDemoInfo.sourceFile, PCSDemoInfo.target,  new BaiduPCSStatusListener(){

						@Override
						public void onProgress(long bytes, long total) {
							// TODO Auto-generated method stub
								
						}		    			
		    		});
		    		
		    		PCSDemoInfo.uiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				
		    				if(ret.error_code == 0){
			    				try{
			    					
				    				File file = new File(PCSDemoInfo.target);			    				
				    				FileInputStream inStream = new FileInputStream(file);
				    				
				    				int length = inStream.available();
				    				
				    				byte [] buffer = new byte[length];
				    				
				    				inStream.read(buffer);
				    				
				    				PCSDemoInfo.fileContent = EncodingUtils.getString(buffer, "UTF-8");
				    				inStream.close();
				    				
			    				}catch (Exception e) {
									// TODO: handle exception
			    					
			    					Toast.makeText(context, "读取文件失败！", Toast.LENGTH_SHORT).show();
								}
		    				}else{
		    					
		    					Toast.makeText(context, "下载失败！", Toast.LENGTH_SHORT).show();
		    				}	
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    
    
 
    //删除云端上的文件
    public void delete(final Context context){
    	
    	if(null != PCSDemoInfo.access_token){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		//设置access_token
		    		api.setAccessToken(PCSDemoInfo.access_token);
		    		
		    		//可实现批量删除文件操作
		    		List<String> files = new ArrayList<String>();
		    		files.add(PCSDemoInfo.mbRootPath + PCSDemoInfo.fileTitle + ".txt");
		    		
		    		//调用云端delete api
		    		final PCSActionInfo.PCSSimplefiedResponse ret = api.deleteFiles(files);
		    		
		    		PCSDemoInfo.uiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				if(0 == ret.error_code){
		    							    							
		    					if(PCSDemoInfo.statu == 2){
		    						//当在内容显示界面删除操作时，先删除后刷新显示列表
		    						Toast.makeText(context, "删除成功！", Toast.LENGTH_SHORT).show();
		    						
		    						list(context);
		    						
		    					}else{
		    						//当在编辑界面删除操作时，先删除云端相同文件名的文件后上传编辑后的文件
		    						if(PCSDemoInfo.statu == 1){
		    							
		    							upload(context);
		    						} 						
		    					}		    					
		    				}else{
		    					Toast.makeText(context, "删除失败！"+ret.message, Toast.LENGTH_SHORT).show();
		    				}
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    
    public void save(Context context) {
    	
    	try{
			//存入本地文件夹作为上传的缓冲区
    		PCSDemoInfo.sourceFile = context.getFilesDir()+"/"+PCSDemoInfo.fileTitle+".txt";
       		
       	    String saveFile = PCSDemoInfo.fileTitle+".txt";
       			        			 	 
       	    FileOutputStream outputStream= context.openFileOutput(saveFile, Context.MODE_PRIVATE);
       	 
       	    if(!PCSDemoInfo.fileContent.equals("")){
       		    //将文本编辑框中的内容写入文件中
           	    outputStream.write(PCSDemoInfo.fileContent.getBytes());
     					           	           	 
       	    }else{
       		    //当文件内容为空时，保存空文件
	       		byte bytes = 0;
	       		outputStream.write(bytes);
       	    }
       	          	 
       	    outputStream.close();
       	    
       	    if(PCSDemoInfo.statu == 0){
       	    	//如果是创建文件则直接上传，因为在创建文件时已经做了文件名是否存在和空的检测
       	    	upload(context);
       	    	
       	    }else{
       	    	//如果是编辑后的文件保存，则先删除云端已存在的文件，然后再上传
       	    	if(PCSDemoInfo.statu == 1){
       	    		
       	    		delete(context);       	    		
       	    	}
       	    }
   	                 		       	 		    
       	  }catch (Exception e) {   
               //显示“文件保存失败”  
               Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();  
          }    	    		 
    }
    
    //返回文件列表显示界面
    public void back(Context context){    	  		
    	Intent content = new Intent();
  	    content.setClass(context, ContentActivity.class);	
  	    context.startActivity(content);   		  	
    }
 
    //程序退出功能
    public void  isExit(final Context context){
    	
        AlertDialog.Builder exitAlert = new AlertDialog.Builder(context);
        exitAlert.setIcon(R.drawable.alert_dark).setTitle("提示...").setMessage("你确定要离开客户端吗？");
        exitAlert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
               
                    public void onClick(DialogInterface dialog, int which) {
                    	PCSDemoInfo.flag = 1;
                        Intent intent = new Intent(); 
                        intent.setClass(context, PCSDemoNoteActivity.class);//跳转到login界面，根据参数退出
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置,clear所有Activity记录
                        context.startActivity(intent);//注意啊，在跳转的页面中进行检测和退出
                    }
                });
        
        exitAlert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
             
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        
        exitAlert.show();
    }
    
		
}
