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
	private ImageButton editBack = null;
	private ImageButton save = null;
	
	private String output_content = null;
		
	private int save_Flag = 0;
	
	BaiduPCSAction editNote = new BaiduPCSAction(); 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        
        title = (TextView)findViewById(R.id.edit_title);
        content = (EditText)findViewById(R.id.edit_content);        
        editBack = (ImageButton)findViewById(R.id.btneditback);
        save = (ImageButton)findViewById(R.id.btneditsave);
        
        PCSDemoInfo.statu = 1;
        
        PCSDemoInfo.uiThreadHandler = new Handler(); 
        
        title.setText(PCSDemoInfo.fileTitle);
        
        editNote.download(EditActivity.this);
    	
        content.setText(PCSDemoInfo.fileContent);
 
        editBack.setOnClickListener(new Button.OnClickListener(){
        	
        	public void onClick(View v){
        		
        		editNote.back(EditActivity.this);       
        	}
        });
        
        save.setOnClickListener(new Button.OnClickListener(){
        	
        	public void onClick(View v){
        		
        		PCSDemoInfo.fileContent = content.getText().toString();
        		
        		editNote.save(EditActivity.this);
        	}
        });       
    }
     
    // back to show content list
    
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
		    	 editNote.isExit(EditActivity.this);
		         break;
		     case PCSDemoInfo.ITEM1:		    	 
		    	 Toast.makeText(getApplicationContext(), "自由开发者，呵呵！", Toast.LENGTH_SHORT).show();
		         break;
		 }
		 
		return true;
	}

}
