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
	private ImageButton editBack = null;
	private ImageButton save = null;
	
	
	BaiduPCSAction createNote = new BaiduPCSAction(); 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create);
        
        //Set status
        PCSDemoInfo.statu = 0;
                
        title = (TextView)findViewById(R.id.titleedit);
        content = (EditText)findViewById(R.id.contentedit);
        
        editBack = (ImageButton)findViewById(R.id.btnback);
        save = (ImageButton)findViewById(R.id.btnsave);
              
        PCSDemoInfo.uiThreadHandler = new Handler(); 
        
        //Set tilte
        title.setText(PCSDemoInfo.fileTitle);
        
        editBack.setOnClickListener(new Button.OnClickListener(){
        	
        	public void onClick(View v){ 
        		
                 createNote.back(CreateActivity.this);                 
        	}
        });
        
        save.setOnClickListener(new Button.OnClickListener(){
        	
        	public void onClick(View v){
        		
        		PCSDemoInfo.fileContent = content.getText().toString();
        		
        		createNote.save(CreateActivity.this); 
        	}
        });
        
    }
    
    
    //Create menu
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
	    menu.add(0, PCSDemoInfo.ITEM0, 0,"退出");
	    menu.add(0, PCSDemoInfo.ITEM1, 0, "关于我们");
	    
	    return true;
	}  
    
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		
		 switch (item.getItemId()) {
		     case PCSDemoInfo.ITEM0:
		    	 createNote.isExit(CreateActivity.this);
		         break;
		     case PCSDemoInfo.ITEM1:		    	 
		    	 Toast.makeText(getApplicationContext(), "我是自由开发者，呵呵！", Toast.LENGTH_SHORT).show();
		         break;
		 }
		 
		return true;
	}

}
