package com.baidu.pcsdemonote;

import com.baidu.oauth2.BaiduOAuth;

import com.baidu.oauth2.BaiduOAuthViaDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/*
 * Author: ganxun(ganxun@baidu.com)
 * Time:   2012.7.10
 * 
 */


public class PCSDemoNoteActivity extends Activity {

	/** Called when the activity is first created. */
	
	private Button login = null;
	
    private BaiduOAuth mbOauth = null;
    
    // the api key
    /*
     * mbApiKey should be your app_key, please instead of "your app_key"
     */
    private final static String mbApiKey = "L6g70tBRRIXLsY0Z3HwKqlRE"; //your app_key";
	
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        login = (Button)findViewById(R.id.btnlogin);
        
        login.setOnClickListener(new Button.OnClickListener(){
        	
            public void onClick(View v) {
            	
            	login();
            }
        });
    }
    
    //
    // login
    //
    private void login(){
    	mbOauth = new BaiduOAuthViaDialog(mbApiKey);

    	try {
    		mbOauth.startDialogAuth(this, new String[]{"basic", "netdisk"}, new BaiduOAuthViaDialog.DialogListener(){

    			public void onComplete(Bundle values) {
    				
//    				mbMessageCenter.setText("Access Token: " + values.getString("access_token"));
    				Log.i("YYY", values.getString("access_token"));
    				
    				Intent intent = new Intent();
    				
    				intent.putExtra("access_token", values.getString("access_token"));
    				
    				intent.setClass(getApplicationContext(), ContentActivity.class);
    				
    				PCSDemoNoteActivity.this.startActivity(intent);
    				
    			}

    			// TODO: the error code need be redefined
    			@SuppressWarnings("unused")
				public void onError(int error) {
//    				mbMessageCenter.setText("Login Error: " + error);
    				
    				Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
    			}

    			public void onCancel() {
//    				mbMessageCenter.setText("Cancel login");
    				
    				Toast.makeText(getApplicationContext(), R.string.back, Toast.LENGTH_SHORT).show();
    				//commandTest.this.addErr("access token error :\n" + "User  cancel the request");
    			}

    			public void onException(String arg0) {
//    				mbMessageCenter.setText("Login Exception: " + arg0);
    				
    				Toast.makeText(getApplicationContext(), arg0, Toast.LENGTH_SHORT).show();
    			}
    		});
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }
    
}

