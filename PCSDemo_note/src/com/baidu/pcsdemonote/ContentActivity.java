package com.baidu.pcsdemonote;

import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.baidu.oauth2.BaiduOAuth;
import com.baidu.oauth2.BaiduOAuthViaDialog;
import com.baidu.pcs.BaiduPCSAPI;
import com.baidu.pcs.PCSActionInfo;
import com.baidu.pcs.PCSActionInfo.PCSFileInfoResponse;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


/*
 * Author: ganxun(ganxun@baidu.com)
 * Time:   2012.7.10
 * 
 */

@SuppressWarnings("unused")
public class ContentActivity extends ListActivity {
    /** Called when the activity is first created. */
		
	
	private ImageButton create = null;
	private ImageButton refresh = null;
	
	BaiduPCSAction contentNote = new BaiduPCSAction();
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cotentshow);
                        
        create = (ImageButton)findViewById(R.id.btncreate);
        refresh = (ImageButton)findViewById(R.id.btnrefresh);
      
        PCSDemoInfo.uiThreadHandler = new Handler(); 
        
        PCSDemoInfo.statu = 2;
        
        //Content list
        contentNote.list(ContentActivity.this);
                
        create.setOnClickListener(new Button.OnClickListener(){
        	
        	public void onClick(View v){	
        		create();
        	}
        });
        
        refresh.setOnClickListener(new Button.OnClickListener(){
        	
        	public void onClick(View v){       		
        		refresh();
        	}
        });
        
    }
    
    //Set item response function
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
    	
    	super.onListItemClick(l, v, position, id);
    	
    	//Get filename form item
    	 PCSDemoInfo.fileTitle = l.getAdapter().getItem(position).toString();
    	
    	 PCSDemoInfo.fileTitle = PCSDemoInfo.fileTitle.substring(PCSDemoInfo.fileTitle.indexOf("=")+1, PCSDemoInfo.fileTitle.lastIndexOf(","));
    	 
		//Select operation(edit/delete/cancel)
    	AlertDialog.Builder onListItemClickAlert = new AlertDialog.Builder(ContentActivity.this);
    	onListItemClickAlert.setTitle("操作选择：");
    		
    	onListItemClickAlert.setPositiveButton("编辑", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				Intent edit_intent = new Intent();
			    edit_intent.setClass(getApplicationContext(),EditActivity.class);					
				ContentActivity.this.startActivity(edit_intent);
			}
		});
    	
    	onListItemClickAlert.setNeutralButton("删除", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				contentNote.delete(ContentActivity.this);		
			}
		});
    	
    	onListItemClickAlert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
    	
    	onListItemClickAlert.show();  
    	 
    }
        
     //Create note
    
    private void create(){
    	
    	//Judge whether filename is empty
    	AlertDialog.Builder alert = new AlertDialog.Builder(ContentActivity.this);
    	alert.setTitle(R.string.title);
    	
    	final EditText input = new EditText(ContentActivity.this);
    	
    	alert.setView(input);
    	
    	alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				PCSDemoInfo.fileTitle = input.getText().toString();
				
				PCSDemoInfo.fileFlag = 0;
				//Judge whether filename is empty			
                if(PCSDemoInfo.fileTitle.equals("")){                	
                	PCSDemoInfo.fileFlag = 1;                	
                }
            	//Judge whether filename is exist		
				for(Iterator<String> file = PCSDemoInfo.fileNameList.iterator();file.hasNext();){					
					if (file.next().equals(PCSDemoInfo.fileTitle)){						
						PCSDemoInfo.fileFlag = 2;
					}										
				}
				
				if(PCSDemoInfo.fileFlag == 1){
					Toast.makeText(getApplicationContext(), "文件名不能为空！", Toast.LENGTH_SHORT).show();
				}else{
					
					if(PCSDemoInfo.fileFlag == 2)
					{
						Toast.makeText(getApplicationContext(), "文件名已存在！", Toast.LENGTH_SHORT).show();
					
					}else{						
						//back to create activity
						Intent create_intent = new Intent();											
						create_intent.setClass(getApplicationContext(), CreateActivity.class);						
						ContentActivity.this.startActivity(create_intent);					
					}					
				}
			}
		});
    	
    	alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub				
			}
		});
    	
    	alert.show();      	
    }
    
    //Refresh  content list
    private void refresh(){    	
    	contentNote.list(ContentActivity.this);
    } 
    
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
		    	 contentNote.isExit(ContentActivity.this);
		         break;
		     case PCSDemoInfo.ITEM1:		    	 
		    	 Toast.makeText(getApplicationContext(), "自由开发者，呵呵！", Toast.LENGTH_SHORT).show();
		         break;
		 }
		 
		return true;
	}
	
}
