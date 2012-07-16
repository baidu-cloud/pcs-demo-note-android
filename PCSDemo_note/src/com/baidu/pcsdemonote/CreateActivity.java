package com.baidu.pcsdemonote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.baidu.oauth2.BaiduOAuth;
import com.baidu.oauth2.BaiduOAuthViaDialog;
import com.baidu.pcs.BaiduPCSAPI;
import com.baidu.pcs.BaiduPCSStatusListener;
import com.baidu.pcs.PCSActionInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
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
public class CreateActivity extends Activity {
    /** Called when the activity is first created. */
	
	private TextView  title = null;
	
	private EditText content = null;
	
	private Handler uiThreadHandler = null;
	
	private String access_token = null ;
	
	private String fileTitle = null;
	
	private ImageButton editBack = null;
	private ImageButton save = null;
	
	private String output_content = null;
	
	private String sourceFile = null;
	
	private int save_Flag = 0;
	
	private final static String mbRootPath = "/apps/pcstest_oauth/";
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);
        
        Intent intent = getIntent();
        
        access_token = intent.getStringExtra("access_token");
        fileTitle = intent.getStringExtra("fileTitle");
        
        title = (TextView)findViewById(R.id.titleedit);
        content = (EditText)findViewById(R.id.contentedit);
        
        editBack = (ImageButton)findViewById(R.id.btnback);
        save = (ImageButton)findViewById(R.id.btnsave);
              
        uiThreadHandler = new Handler(); 
        
        title.setText(fileTitle);
        
        
        editBack.setOnClickListener(new Button.OnClickListener(){
        	
        	public void onClick(View v){        		
                 back();                 
        	}
        });
        
        save.setOnClickListener(new Button.OnClickListener(){
        	
        	public void onClick(View v){
        		
        		save();       		
        		save_Flag = 1;
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
  					
        	 upload();
      	                	                 		       	 		    
        	}catch (Exception e) {   
                //显示“文件保存失败”  
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
												
							final long bs = bytes;
							final long tl = total;

							uiThreadHandler.post(new Runnable(){
				    			public void run(){
				    				
				    				Toast.makeText(getApplicationContext(),tl+"/"+bs, Toast.LENGTH_SHORT).show(); 
				    				
				    				if(bs == tl){
				    					
				    				}
				    			}
				    		});						
						}
		    		});
		    		
		    		uiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				if(response.error_code == 0){
		    					
		    					Toast.makeText(getApplicationContext(),"上传成功", Toast.LENGTH_SHORT).show();
		    					
		    					File file = new File(sourceFile);
		    					
		    					file.delete();
		    					
		    					back();
		    					
		    				}else{
		    					
		    					Toast.makeText(getApplicationContext(),"错误代码："+response.error_code, Toast.LENGTH_SHORT).show(); 
		    				}
		    				
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
        		
        CreateActivity.this.startActivity(content_intent);   		  	         	
    }
    
    @Override
 	public boolean onCreateOptionsMenu(Menu menu) {
 		// TODO Auto-generated method stub
 		super.onCreateOptionsMenu(menu);
 	    menu.add(0, ITEM0, 0,"退出");
 	    menu.add(0, ITEM1, 0, "关于我们");
 	    
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
         exitAlert.setTitle("提示...").setMessage("你确定要离开客户端吗？");
         exitAlert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                
                     public void onClick(DialogInterface dialog, int which) {
                     	Exit.flag = 1;
                         Intent intent = new Intent(); 
                         intent.putExtra("flag", "exit");//添加参数，这是退出的依据
                         intent.setClass(getApplicationContext(), PCSDemoNoteActivity.class);//跳转到login界面，根据参数退出
                         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //注意本行的FLAG设置,clear所有Activity记录
                         startActivity(intent);//注意啊，在跳转的页面中进行检测和退出
                     }
                 });
         exitAlert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
              
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.cancel();
                     }
                 }).create();
         exitAlert.show();
     }

     
     public static final int ITEM0=Menu.FIRST;//系统值
     public static final int ITEM1=Menu.FIRST+1;

}
