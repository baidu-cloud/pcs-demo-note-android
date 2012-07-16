package com.baidu.pcsdemonote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import com.baidu.pcs.BaiduPCSAPI;
import com.baidu.pcs.BaiduPCSStatusListener;
import com.baidu.pcs.PCSActionInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Author: ganxun(ganxun@baidu.com)
 * Time:   2012.7.10
 * 
 */

@SuppressWarnings("unused")
public class EditActivity extends Activity {
    /** Called when the activity is first created. */
	
	private TextView title = null;
	
	private EditText content = null;
	
	private Handler uiThreadHandler = null;
	
	private String access_token = null ;
	
	private String fileTitle = null;
	
	private ImageButton editBack = null;
	private ImageButton save = null;
	
	private String output_content = null;
	
	private String sourceFile = null;
	
	private String target = null;
	
	private final static String mbRootPath = "/apps/pcstest_oauth/";
	
	private int save_Flag = 0;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        
        Intent intent = getIntent();
        
        access_token = intent.getStringExtra("access_token");
        fileTitle = intent.getStringExtra("fileTitle");
        
        title = (TextView)findViewById(R.id.edit_title);
        content = (EditText)findViewById(R.id.edit_content);
        
        editBack = (ImageButton)findViewById(R.id.btneditback);
        save = (ImageButton)findViewById(R.id.btneditsave);
        
        
        uiThreadHandler = new Handler(); 
        
        title.setText(fileTitle);
        
        download();
        
        editBack.setOnClickListener(new Button.OnClickListener(){
        	
        	public void onClick(View v){
        		
                 back();
                 
        	}
        });
        
        save.setOnClickListener(new Button.OnClickListener(){
        	
        	public void onClick(View v){
        		
        		save_Flag = 1;
        		
        		save();
        	}
        });
        
    }
    
    private void save() {
    	
    	try{
			
       	 sourceFile = this.getFilesDir()+"/"+fileTitle+".txt";
       		
       	 String saveFile = fileTitle+".txt";
       			        			 	 
       	 FileOutputStream outputStream= this.openFileOutput(saveFile, Context.MODE_PRIVATE);
       		 
       	 output_content=content.getText().toString();
       	          	 
       	 outputStream.write(output_content.getBytes());
       	       	 
       	 outputStream.close();
 					
       	 delete();
     	                	                 		       	 		    
       	}catch (Exception e) {   
               //��ʾ���ļ�����ʧ�ܡ�  
               Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();  
           }    	
    		 
    }
    
    private void upload(){
    	
    	if(null != access_token){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {
							    		
		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		api.setAccessToken(access_token);
		    	
					final PCSActionInfo.PCSFileInfoResponse response = api.uploadFile(sourceFile, mbRootPath+fileTitle+".txt", new BaiduPCSStatusListener(){

						@Override
						public void onProgress(long bytes, long total) {
							// TODO Auto-generated method stub
																
						}
		    		});
									
		    		uiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				if(response.error_code == 0){
		    					
//		    					Toast.makeText(getApplicationContext(),"�ϴ��ɹ�", Toast.LENGTH_SHORT).show(); 
		    					back();
		    					
		    				}else{
		    					
		    					Toast.makeText(getApplicationContext(),"������룺"+response.error_code, Toast.LENGTH_SHORT).show(); 
		    				}
		    				
		    			}
		    		});	
		    		
				}
			});
			
    		workThread.start();
    	}
    }
    
    //download file
    
    private void download(){
    	
    	if(null != access_token){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		api.setAccessToken(access_token);
		    		
		    		String source = mbRootPath + fileTitle+".txt";
		    		
		    		target = getApplicationContext().getFilesDir()+"/"+fileTitle+".txt";
		    		
		    		final PCSActionInfo.PCSSimplefiedResponse ret = api.downloadFile(source, target,  new BaiduPCSStatusListener(){

						@Override
						public void onProgress(long bytes, long total) {
							// TODO Auto-generated method stub
								
						}		    			
		    		});
		    		
		    		uiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				
		    				if(ret.error_code == 0){
			    				try{
			    					
				    				File file = new File(target);			    				
				    				FileInputStream inStream = new FileInputStream(file);
				    				
				    				int length = inStream.available();
				    				
				    				byte [] buffer = new byte[length];
				    				
				    				inStream.read(buffer);
				    				
				    				String res = EncodingUtils.getString(buffer, "UTF-8");
				    				
				    				content.setText(res);
				    				
				    				inStream.close();
				    				
			    				}catch (Exception e) {
									// TODO: handle exception
			    					
			    					Toast.makeText(getApplicationContext(), "��ȡ�ļ�ʧ�ܣ�", Toast.LENGTH_SHORT).show();
								}
		    				}else{
		    					
		    					Toast.makeText(getApplicationContext(), "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
		    				}	
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    
    
    // delete file
    
    private void delete(){
    	
    	if(null != access_token){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		api.setAccessToken(access_token);
	    		
		    		List<String> files = new ArrayList<String>();
		    		files.add(mbRootPath + fileTitle + ".txt");
		    		
		    		final PCSActionInfo.PCSSimplefiedResponse ret = api.deleteFiles(files);
		    		
		    		uiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				if(0 == ret.error_code){
		    					
//		    					Toast.makeText(getApplicationContext(), "ɾ���ɹ���", Toast.LENGTH_SHORT).show();
	        					upload();
		    				}else{
		    					Toast.makeText(getApplicationContext(), "ɾ��ʧ�ܣ�"+ret.message, Toast.LENGTH_SHORT).show();
		    				}
		    				
//		    				mbMessageCenter.setText("Delete files:  " + (0 == ret.error_code ? "success" : "failed  " + ret.message));
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    
    // back to show content list
    
    private void back(){
    	  		
    	Intent content_intent = new Intent();
    		
    	content_intent.putExtra("access_token", access_token);
    		
    	content_intent.setClass(getApplicationContext(), ContentActivity.class);
    		
    	EditActivity.this.startActivity(content_intent);   		  	
    }
    
    @Override
 	public boolean onCreateOptionsMenu(Menu menu) {
 		// TODO Auto-generated method stub
 		super.onCreateOptionsMenu(menu);
 	    menu.add(0, ITEM0, 0,"�˳�");
 	    menu.add(0, ITEM1, 0, "��������");
 	    
 	    return true;
 	}  
     
 	@Override
 	public boolean onOptionsItemSelected(MenuItem item) {
 		// TODO Auto-generated method stub
 		super.onOptionsItemSelected(item);
 		
 		 switch (item.getItemId()) {
 		     case ITEM0:
 		    	 isExit();
 		         break;
 		     case ITEM1:
                  
 		         break;
 		 }
 		 
 		return true;
 	}
 	
   public void  isExit(){
     	
         AlertDialog.Builder exitAlert = new AlertDialog.Builder(this);
         exitAlert.setTitle("��ʾ...").setMessage("��ȷ��Ҫ�뿪�ͻ�����");
         exitAlert.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                
                     public void onClick(DialogInterface dialog, int which) {
                     	Exit.flag = 1;
                         Intent intent = new Intent(); 
                         intent.putExtra("flag", "exit");//��Ӳ����������˳�������
                         intent.setClass(getApplicationContext(), PCSDemoNoteActivity.class);//��ת��login���棬���ݲ����˳�
                         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //ע�Ȿ�е�FLAG����,clear����Activity��¼
                         startActivity(intent);//ע�Ⱑ������ת��ҳ���н��м����˳�
                     }
                 });
         exitAlert.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
              
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.cancel();
                     }
                 }).create();
         exitAlert.show();
     }

     
     public static final int ITEM0=Menu.FIRST;//ϵͳֵ
     public static final int ITEM1=Menu.FIRST+1;

}
