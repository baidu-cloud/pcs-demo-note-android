package com.baidu.pcsdemonote;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.baidu.pcsdemonote.BaiduPCSAction;

/*
 * Author: ganxun(ganxun@baidu.com)
 * Time:   2012.7.10
 * 
 */

public class PCSDemoNoteActivity extends Activity {
	
	private Button login = null;
    
    /*
     * mbApikey为开发者中心为应用分配的app_key,使用自己应用的app_key代替
     * mbApiKey should be your app_key, please instead of "your app_key"
     */
	
    BaiduPCSAction loginNote = new BaiduPCSAction();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        login = (Button)findViewById(R.id.btnlogin);
        
        login.setOnClickListener(new Button.OnClickListener(){
        	
            public void onClick(View v) {
            	
            	loginNote.login(PCSDemoNoteActivity.this);
    
            }
        });
    }
    
    
    /*
     * 在启动界面判断是否需要退出程序
     * @see android.app.Activity#onStart()
     */
    
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
				
		if(PCSDemoInfo.flag == 1)
		{
			
			this.finish();
			//kill process
			android.os.Process.killProcess(android.os.Process.myPid()); 
						
		}else{
			super.onResume();
		}
	}
    
}

