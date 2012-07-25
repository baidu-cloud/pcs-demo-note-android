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

    // Get access_token 
    public void login(final Context context){
    	
    	if(null != PCSDemoInfo.access_token){
			Intent intent = new Intent();    				    						    				
			intent.setClass(context, ContentActivity.class); 				
			context.startActivity(intent); 
    	}else{
    		
    		PCSDemoInfo.mbOauth = new BaiduOAuthViaDialog(PCSDemoInfo.app_key);

        	try {
        		//Start OAUTH dialog
        		PCSDemoInfo.mbOauth.startDialogAuth(context, new String[]{"basic", "netdisk"}, new BaiduOAuthViaDialog.DialogListener(){

        			//Login successful 
        			public void onComplete(Bundle values) {
        				//Get access_token
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
    
    //Upload files to cloud
    public void upload(final Context context){
    	
    	if(null != PCSDemoInfo.access_token){

    		Thread workThread = new Thread(new Runnable(){
				
    			public void run() {
									
		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		
		    		//Set access_token for pcs api
		    		api.setAccessToken(PCSDemoInfo.access_token);
		    		
		    	    //Use pcs uploadFile API to uplaod files
					final PCSActionInfo.PCSFileInfoResponse response = api.uploadFile(PCSDemoInfo.sourceFile, PCSDemoInfo.mbRootPath+PCSDemoInfo.fileTitle+".txt", new BaiduPCSStatusListener(){

						@Override
						public void onProgress(long bytes, long total) {
							// TODO Auto-generated method stub					
						}
		    		});
		    		
					//The interface of the thread UI
					PCSDemoInfo.uiThreadHandler.post(new Runnable(){
						
		    			public void run(){
		  
		    				if(response.error_code == 0){
		    					
		    					Toast.makeText(context,"上传成功", Toast.LENGTH_SHORT).show();
		    					
		    					//Delete temp file
		    					File file = new File(PCSDemoInfo.sourceFile);
		    					file.delete();
		    					
	    					    //Bcak to the content activity
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
    
  
    //This function to display the list of contents
    public void list(final Context context){
    	
        if (null != PCSDemoInfo.access_token){
        	        	
    		Thread workThread = new Thread(new Runnable(){
    			
				public void run() {
					
		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		api.setAccessToken(PCSDemoInfo.access_token );
		    		
		    		//The path to  file storage on the cloud
		    		String path = PCSDemoInfo.mbRootPath;
		    		
		    		//Use list api
		    		final PCSActionInfo.PCSListInfoResponse ret = api.list(path, "name", "asc");
		    				    		
		    		PCSDemoInfo.uiThreadHandler.post(new Runnable(){
		    			
		    			public void run(){		    				
		    			
		    				ArrayList<HashMap<String, String>> list =new ArrayList<HashMap<String,String>>();   
		    						    				

		    				if("[]" != ret.list.toString()){
		    					   			    	            
			    	            for(Iterator<PCSFileInfoResponse> i = ret.list.iterator(); i.hasNext();){
			    	            	
			    	            	HashMap<String, String> map =new HashMap<String, String>();
			    	            				    	            	
			    	            	PCSFileInfoResponse info = i.next();
			    	            	
			    	            	//Get the file name 			    	            	
			    	         	    String path = info.path;			    	         	    
			    	         	    String fileName = path.substring(PCSDemoInfo.mbRootPath.length(),path.lastIndexOf("."));
			    	         	    
			    	         	    //Get the last modified time
			    	         	    Date date = new Date(info.mTime*1000);
			    	         	    
			    	         	    //Modify the format of the time
			    	         	    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
			    	         	    String dateString = formatter.format(date);
		  			 			    			    	            	
			    	            	map.put("file_name", fileName);			    	            	
			    	            	map.put("time", dateString);
			    	            	
			    	            	//Add item to content list	
			    	            	list.add(map); 	            	
			    	            	PCSDemoInfo.fileNameList.add(fileName);							    				    	             
			    	            }			    	               
			    	        }else{
			    	        	
			    	        	//Clear content list
		    					list.clear();
		    					Toast.makeText(context, "您的文件夹为空！", Toast.LENGTH_SHORT).show();		    					
		    				}    
		    				
			    	         SimpleAdapter listAdapter =new SimpleAdapter(context, list, R.layout.content, new String[]{"file_name","time"}, new int[]{R.id.file_name,R.id.time});   
			    	        
			    	         //Set listview to display content
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
		    		
		    		//Get the download file storage path on cloud
		    		PCSDemoInfo.sourceFile = PCSDemoInfo.mbRootPath + PCSDemoInfo.fileTitle+".txt";
		    		
		    		//Set the download file storage path
		    		PCSDemoInfo.target = context.getFilesDir()+"/"+PCSDemoInfo.fileTitle+".txt";
		    		
		    		//Call PCS downloadFile API
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
			    					//The local store download files
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
    
    
 
    //Delete the  file on the cloud 
    public void delete(final Context context){
    	
    	if(null != PCSDemoInfo.access_token){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		//Set access_token
		    		api.setAccessToken(PCSDemoInfo.access_token);
		    		
		    		List<String> files = new ArrayList<String>();
		    		files.add(PCSDemoInfo.mbRootPath + PCSDemoInfo.fileTitle + ".txt");
		    		
		    		//Call delete api
		    		final PCSActionInfo.PCSSimplefiedResponse ret = api.deleteFiles(files);
		    		
		    		PCSDemoInfo.uiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				if(0 == ret.error_code){
		    							    							
		    					if(PCSDemoInfo.statu == 2){
		    						//First remove the clouds files, and then refresh content list
		    						Toast.makeText(context, "删除成功！", Toast.LENGTH_SHORT).show();
		    						
		    						list(context);
		    						
		    					}else{
		    						//First remove the clouds files,and then upload the file 
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
    		PCSDemoInfo.sourceFile = context.getFilesDir()+"/"+PCSDemoInfo.fileTitle+".txt";
       		
       	    String saveFile = PCSDemoInfo.fileTitle+".txt";
       			        			 	 
       	    FileOutputStream outputStream= context.openFileOutput(saveFile, Context.MODE_PRIVATE);
       	 
       	    if(!PCSDemoInfo.fileContent.equals("")){
       		    //save file
           	    outputStream.write(PCSDemoInfo.fileContent.getBytes());
     					           	           	 
       	    }else{

	       		byte bytes = 0;
	       		outputStream.write(bytes);
       	    }
       	          	 
       	    outputStream.close();
       	    
       	    if(PCSDemoInfo.statu == 0){
       	    	//Upload the file to cloud 
       	    	upload(context);
       	    	
       	    }else{
       	    	//If it is edited file save, the first remove the clouds existing file, and then upload
       	    	if(PCSDemoInfo.statu == 1){
       	    		
       	    		delete(context);       	    		
       	    	}
       	    }
   	                 		       	 		    
       	  }catch (Exception e) {   
               Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();  
          }    	    		 
    }
    
    //Back to the content activity
    public void back(Context context){    	  		
    	Intent content = new Intent();
  	    content.setClass(context, ContentActivity.class);	
  	    context.startActivity(content);   		  	
    }
 
    //Finish the program
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
