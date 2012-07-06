package com.baidu.PCSDemoNote;

import java.util.ArrayList;
import java.util.List;

import com.baidu.oauth2.BaiduOAuth;
import com.baidu.oauth2.BaiduOAuthViaDialog;
import com.baidu.pcs.BaiduPCSAPI;
import com.baidu.pcs.BaiduPCSStatusListener;
import com.baidu.pcs.PCSActionInfo;


import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PCSDemoNoteActivity extends Activity {
    /** Called when the activity is first created. */
	
    private BaiduOAuth mbOauth = null;
    
    private String access_token = null;
    
    // the api key
    /*
     * mbApiKey should be your app_key, please instead of "your app_key"
     */
    private final static String mbApiKey = "L6g70tBRRIXLsY0Z3HwKqlRE"; //your app_key";
    
    // the default root folder
    /*
     * mbRootPath should be your app_path, please instead of "/apps/pcstest_oauth"
     */
    private final static String mbRootPath = "/apps/pcstest_oauth";
    
    // the handler
    private Handler mbUiThreadHandler = null;
    
    private Button login = null;
    private Button upload = null;
    private Button delete = null;
    private TextView mbMessageCenter = null;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        login = (Button)findViewById(R.id.btnlogin);
        upload = (Button)findViewById(R.id.btnupload);
        delete = (Button)findViewById(R.id.btndelete);
        mbMessageCenter = (TextView)findViewById(R.id.messagecenter);
                
        //login listener
        login.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				test_login();
				test_getQuota();
//				test_list();
			}
        	
        });
        
    }
       
    private void test_login(){
    	mbOauth = new BaiduOAuthViaDialog(mbApiKey);

    	try {
    		mbOauth.startDialogAuth(this, new String[]{"basic", "netdisk"}, new BaiduOAuthViaDialog.DialogListener(){

    			public void onComplete(Bundle values) {
    				Toast.makeText(PCSDemoNoteActivity.this, R.string.loginsuccess, Toast.LENGTH_SHORT).show();
    	//			mbMessageCenter.setText("Access Token: " + values.getString("access_token"));
    				Log.i("YYY", values.getString("access_token"));
    				upload.setEnabled(true);
    				delete.setEnabled(true);
    			}

    			// TODO: the error code need be redefined
    			public void onError(int error) {
    				
    				Toast.makeText(PCSDemoNoteActivity.this, "Login Error: " + error, Toast.LENGTH_SHORT).show();
    			}

    			public void onCancel() {
    				Toast.makeText(PCSDemoNoteActivity.this, R.string.logincancel, Toast.LENGTH_SHORT).show();
    			}

    			public void onException(String arg0) {
    				Toast.makeText(PCSDemoNoteActivity.this, "Login Exception: " + arg0, Toast.LENGTH_SHORT).show();

    			}
    		});
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    } 
    

    //
    // get quota
    //
    private void test_getQuota(){

    	if(null != mbOauth){
    		
    		mbMessageCenter.setText("Message Center");

    		Thread workThread = new Thread(new Runnable(){
				public void run() {
		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		api.setAccessToken(mbOauth.getAccessToken());
		    		final PCSActionInfo.PCSQuotaResponse info = api.quota();

		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
				    		if(null != info){
				    			if(0 == info.error_code){
				    				mbMessageCenter.setText("Quota success: total:" + info.total + "  used: " + info.usded);
				    			}
				    			else{
				    				mbMessageCenter.setText("Quota failed: " + info.message);
				    			}
				    		}
		    			}
		    		});
				}
			});
			 
    		workThread.start();
    	}
    }
    
    
    /**
     * Called by the system when the device configuration changes while your activity is running. 
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //don't reload the current page when the orientation is changed
        super.onConfigurationChanged(newConfig);
    }
    
}