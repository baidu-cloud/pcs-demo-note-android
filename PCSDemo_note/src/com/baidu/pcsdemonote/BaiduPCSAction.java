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
	//����ʹ��PCS�ṩ��APIʵ�־���Ĺ���

    // ��ٶ����ӣ����access_token
    public void login(final Context context){
    	
    	if(null != PCSDemoInfo.access_token){
			Intent intent = new Intent();    				    						    				
			intent.setClass(context, ContentActivity.class); 				
			context.startActivity(intent); 
    	}else{
    		
    		PCSDemoInfo.mbOauth = new BaiduOAuthViaDialog(PCSDemoInfo.app_key);

        	try {
        		//�����ٶ�OAuth�Ի���
        		PCSDemoInfo.mbOauth.startDialogAuth(context, new String[]{"basic", "netdisk"}, new BaiduOAuthViaDialog.DialogListener(){

        			//��½�ɹ���Ĳ���
        			public void onComplete(Bundle values) {
        				//���access_token
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
    
    //�ϴ��ļ����ƶ�
    public void upload(final Context context){
    	
    	if(null != PCSDemoInfo.access_token){

    		Thread workThread = new Thread(new Runnable(){
				
    			public void run() {
									
		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		
		    		//ΪAPI����access_token
		    		api.setAccessToken(PCSDemoInfo.access_token);
		    		
		    	    //ʹ��pcs uploadFile API�ϴ��ļ�
					final PCSActionInfo.PCSFileInfoResponse response = api.uploadFile(PCSDemoInfo.sourceFile, PCSDemoInfo.mbRootPath+PCSDemoInfo.fileTitle+".txt", new BaiduPCSStatusListener(){

						@Override
						public void onProgress(long bytes, long total) {
							// TODO Auto-generated method stub					
						}
		    		});
		    		
					//��UI����������߳�
					PCSDemoInfo.uiThreadHandler.post(new Runnable(){
						
		    			public void run(){
		  
		    				if(response.error_code == 0){
		    					
		    					Toast.makeText(context,"�ϴ��ɹ�", Toast.LENGTH_SHORT).show();
		    					
		    					//���ļ���Ϊ�ϴ��Ļ����ļ����ϴ��ɹ���ɾ�����ش洢���ļ�
		    					File file = new File(PCSDemoInfo.sourceFile);
		    					file.delete();
		    					
	    					    //�����ļ��б���ʾ����
		    					back(context);
		    					
		    				}else{
		    					
		    					Toast.makeText(context,"������룺"+response.error_code, Toast.LENGTH_SHORT).show(); 
		    				}
		    				
		    			}
		    		});	
		    		
				}
			});
			 
    		workThread.start();
    	}
    }
    
  
    //�г��ƶ����ļ�����Ϣ
    public void list(final Context context){
    	
        if (null != PCSDemoInfo.access_token){
        	        	
    		Thread workThread = new Thread(new Runnable(){
    			
				public void run() {
					
		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		api.setAccessToken(PCSDemoInfo.access_token );
		    		
		    		//�ƶ˴洢�ļ���·��
		    		String path = PCSDemoInfo.mbRootPath;
		    		
		    		//ʹ�ðٶ��ṩ��list API
		    		final PCSActionInfo.PCSListInfoResponse ret = api.list(path, "name", "asc");
		    				    		
		    		PCSDemoInfo.uiThreadHandler.post(new Runnable(){
		    			
		    			public void run(){		    				
		    				//HashMpaΪ��ֵ�����͡���һ������Ϊ�����ڶ�������Ϊֵ 
		    				ArrayList<HashMap<String, String>> list =new ArrayList<HashMap<String,String>>();   
		    						    				
		    				//ret.listΪ�ƶ˷�����Ϣ
		    				if("[]" != ret.list.toString()){
		    					   			    	            
			    	            for(Iterator<PCSFileInfoResponse> i = ret.list.iterator(); i.hasNext();){
			    	            	
			    	            	HashMap<String, String> map =new HashMap<String, String>();
			    	            				    	            	
			    	            	PCSFileInfoResponse info = i.next();
			    	            	
			    	            	//����ļ������֣������������ϵľ���·����			    	            	
			    	         	    String path = info.path;			    	         	    
			    	         	    String fileName = path.substring(PCSDemoInfo.mbRootPath.length(),path.lastIndexOf("."));
			    	         	    
			    	         	    //����ϴ��޸ĵ�ʱ�䣨����Ϊ��λ��
			    	         	    Date date = new Date(info.mTime*1000);
			    	         	    
			    	         	    //�޸�ʱ��ĸ�ʽ
			    	         	    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
			    	         	    String dateString = formatter.format(date);
		  			 			    			    	            	
			    	            	map.put("file_name", fileName);			    	            	
			    	            	map.put("time", dateString);
			    	            	
			    	            	//listview��ÿһ����ʾ���ݵ�list		
			    	            	list.add(map); 	            	
			    	            	PCSDemoInfo.fileNameList.add(fileName);							    				    	             
			    	            }			    	               
			    	        }else{
			    	        	
			    	        	//���ƶ�û���ļ�������ɾ�������һ���ļ�ˢ�º󣬱������list
		    					list.clear();
		    					Toast.makeText(context, "�����ļ���Ϊ�գ�", Toast.LENGTH_SHORT).show();		    					
		    				}    
		    				//����һ��SimpleAdapter���͵ı������������   
			    	         SimpleAdapter listAdapter =new SimpleAdapter(context, list, R.layout.content, new String[]{"file_name","time"}, new int[]{R.id.file_name,R.id.time});   
			    	        
			    	         //������ʾListView 
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
		    		
		    		//���������ļ����ƶ˴洢��λ��
		    		PCSDemoInfo.sourceFile = PCSDemoInfo.mbRootPath + PCSDemoInfo.fileTitle+".txt";
		    		
		    		//���������ļ��ڱ��ش洢λ��
		    		PCSDemoInfo.target = context.getFilesDir()+"/"+PCSDemoInfo.fileTitle+".txt";
		    		
		    		//����PCS downloadFile API
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
			    					
			    					Toast.makeText(context, "��ȡ�ļ�ʧ�ܣ�", Toast.LENGTH_SHORT).show();
								}
		    				}else{
		    					
		    					Toast.makeText(context, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
		    				}	
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    
    
 
    //ɾ���ƶ��ϵ��ļ�
    public void delete(final Context context){
    	
    	if(null != PCSDemoInfo.access_token){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		//����access_token
		    		api.setAccessToken(PCSDemoInfo.access_token);
		    		
		    		//��ʵ������ɾ���ļ�����
		    		List<String> files = new ArrayList<String>();
		    		files.add(PCSDemoInfo.mbRootPath + PCSDemoInfo.fileTitle + ".txt");
		    		
		    		//�����ƶ�delete api
		    		final PCSActionInfo.PCSSimplefiedResponse ret = api.deleteFiles(files);
		    		
		    		PCSDemoInfo.uiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				if(0 == ret.error_code){
		    							    							
		    					if(PCSDemoInfo.statu == 2){
		    						//����������ʾ����ɾ������ʱ����ɾ����ˢ����ʾ�б�
		    						Toast.makeText(context, "ɾ���ɹ���", Toast.LENGTH_SHORT).show();
		    						
		    						list(context);
		    						
		    					}else{
		    						//���ڱ༭����ɾ������ʱ����ɾ���ƶ���ͬ�ļ������ļ����ϴ��༭����ļ�
		    						if(PCSDemoInfo.statu == 1){
		    							
		    							upload(context);
		    						} 						
		    					}		    					
		    				}else{
		    					Toast.makeText(context, "ɾ��ʧ�ܣ�"+ret.message, Toast.LENGTH_SHORT).show();
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
			//���뱾���ļ�����Ϊ�ϴ��Ļ�����
    		PCSDemoInfo.sourceFile = context.getFilesDir()+"/"+PCSDemoInfo.fileTitle+".txt";
       		
       	    String saveFile = PCSDemoInfo.fileTitle+".txt";
       			        			 	 
       	    FileOutputStream outputStream= context.openFileOutput(saveFile, Context.MODE_PRIVATE);
       	 
       	    if(!PCSDemoInfo.fileContent.equals("")){
       		    //���ı��༭���е�����д���ļ���
           	    outputStream.write(PCSDemoInfo.fileContent.getBytes());
     					           	           	 
       	    }else{
       		    //���ļ�����Ϊ��ʱ��������ļ�
	       		byte bytes = 0;
	       		outputStream.write(bytes);
       	    }
       	          	 
       	    outputStream.close();
       	    
       	    if(PCSDemoInfo.statu == 0){
       	    	//����Ǵ����ļ���ֱ���ϴ�����Ϊ�ڴ����ļ�ʱ�Ѿ������ļ����Ƿ���ںͿյļ��
       	    	upload(context);
       	    	
       	    }else{
       	    	//����Ǳ༭����ļ����棬����ɾ���ƶ��Ѵ��ڵ��ļ���Ȼ�����ϴ�
       	    	if(PCSDemoInfo.statu == 1){
       	    		
       	    		delete(context);       	    		
       	    	}
       	    }
   	                 		       	 		    
       	  }catch (Exception e) {   
               //��ʾ���ļ�����ʧ�ܡ�  
               Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();  
          }    	    		 
    }
    
    //�����ļ��б���ʾ����
    public void back(Context context){    	  		
    	Intent content = new Intent();
  	    content.setClass(context, ContentActivity.class);	
  	    context.startActivity(content);   		  	
    }
 
    //�����˳�����
    public void  isExit(final Context context){
    	
        AlertDialog.Builder exitAlert = new AlertDialog.Builder(context);
        exitAlert.setIcon(R.drawable.alert_dark).setTitle("��ʾ...").setMessage("��ȷ��Ҫ�뿪�ͻ�����");
        exitAlert.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
               
                    public void onClick(DialogInterface dialog, int which) {
                    	PCSDemoInfo.flag = 1;
                        Intent intent = new Intent(); 
                        intent.setClass(context, PCSDemoNoteActivity.class);//��ת��login���棬���ݲ����˳�
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //ע�Ȿ�е�FLAG����,clear����Activity��¼
                        context.startActivity(intent);//ע�Ⱑ������ת��ҳ���н��м����˳�
                    }
                });
        
        exitAlert.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
             
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        
        exitAlert.show();
    }
    
		
}
