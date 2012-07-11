package com.baidu.pcsdemonote;

import java.util.ArrayList;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
	
	private TextView quota = null;
	private final static String mbRootPath = "/apps/pcstest_oauth";
	
	private Handler uiThreadHandler = null;
	
	private String access_token = null ;
	
	private String fileTitle = null;
	private String name = null;
	
	private Button create = null;
	private Button refresh = null;
	
	private int flag = 0 ;
	
	private ArrayList<String> fileNameList = new ArrayList<String>();

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cotentshow);
        
        Intent intent = getIntent();
        
        access_token = intent.getStringExtra("access_token");
        
        quota = (TextView)findViewById(R.id.text);
        
        create = (Button)findViewById(R.id.btncreate);
        refresh = (Button)findViewById(R.id.btnrefresh);
        
//        token.setText(ret.message); 
        
        uiThreadHandler = new Handler(); 
        
        list();
        
        
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
    
    private void list(){
    	
        if (null !=access_token){
        	        	
    		Thread workThread = new Thread(new Runnable(){
				public void run() {
					
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}

		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		api.setAccessToken(access_token );
		    		String path = mbRootPath;

		    		final PCSActionInfo.PCSListInfoResponse ret = api.list(path, "name", "asc");
		    				    		
		    		uiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				
		    				if("[]" != ret.list.toString()){
		    					
			    	            ArrayList<HashMap<String, String>> list =new ArrayList<HashMap<String,String>>();   
			    	            //HashMpa为键值对类型。第一个参数为建，第二个参数为值   
			    	            
			    	            for(Iterator<PCSFileInfoResponse> i = ret.list.iterator(); i.hasNext();){
			    	            	
			    	            	HashMap<String, String> map =new HashMap<String, String>();
			    	            	
			    	            	String path = i.next().path;
			    	            	
			    	            	String fileName = path.substring(mbRootPath.length()+1,path.lastIndexOf("."));
		    	            	
			    	            	map.put("file_name", fileName);
			    	            	
			    	            	fileNameList.add(fileName);
							    	   
				    	            list.add(map);  
			    	            }
			    	               
			    	            //生成一个SimpleAdapter类型的变量来填充数据   
			    	            SimpleAdapter listAdapter =new SimpleAdapter(ContentActivity.this, list, R.layout.content, new String[]{"file_name"}, new int[]{R.id.file_name});   
			    	            //设置显示ListView   
			    	            setListAdapter(listAdapter); 
			    	            
			    	            Toast.makeText(getApplicationContext(), R.string.refresh, Toast.LENGTH_SHORT).show();
		    				}		    						    				        	  	  				    				
		    			}
		    		});	
		    		
				}
			});
			 
    		workThread.start();

        } 
    }
    
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
    	
    	super.onListItemClick(l, v, position, id);
    	
    	name = l.getAdapter().getItem(position).toString();
    	
    	name = name.substring(name.indexOf("=")+1, name.lastIndexOf("}"));
    	 
		
    	AlertDialog.Builder onListItemClickAlert = new AlertDialog.Builder(ContentActivity.this);
    	onListItemClickAlert.setTitle("操作选择：");
    	
    	
    	onListItemClickAlert.setPositiveButton("编辑", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				Intent edit_intent = new Intent();
				
				edit_intent.putExtra("access_token", access_token);
				edit_intent.putExtra("fileTitle", name);
					
				edit_intent.setClass(getApplicationContext(),EditActivity.class);
					
				ContentActivity.this.startActivity(edit_intent);
			}
		});
    	
    	onListItemClickAlert.setNeutralButton("删除", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				delete();
				
			}
		});
    	
    	onListItemClickAlert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
    	
    	onListItemClickAlert.show();  
    	 
    }
    
   //create new file 
    
    private void create(){
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(ContentActivity.this);
    	alert.setTitle(R.string.title);
    	
    	final EditText input = new EditText(ContentActivity.this);
    	alert.setView(input);
    	
    	alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				fileTitle = input.getText().toString();
				
				flag = 0;
				
				for(Iterator<String> file = fileNameList.iterator();file.hasNext();){
					
					if (file.next().equals(fileTitle)){
						
						flag = 1;
					}
										
				}
				
				if(flag == 1)
				{
					Toast.makeText(getApplicationContext(), "文件已存在！", Toast.LENGTH_SHORT).show();
				
				}else{					
					Intent create_intent = new Intent();
					
					create_intent.putExtra("access_token", access_token);
					create_intent.putExtra("fileTitle", fileTitle);
					
					create_intent.setClass(getApplicationContext(), CreateActivity.class);
					
					ContentActivity.this.startActivity(create_intent);					
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
    
// delete file
    
    private void delete(){
    	
    	if(null != access_token){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		api.setAccessToken(access_token);
	    		
		    		List<String> files = new ArrayList<String>();
		    		files.add(mbRootPath +"/" + name + ".txt");
		    		
		    		final PCSActionInfo.PCSSimplefiedResponse ret = api.deleteFiles(files);
		    		
		    		uiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				if(0 == ret.error_code){
		    					
		    					Toast.makeText(getApplicationContext(), "删除成功！", Toast.LENGTH_SHORT).show();
		    					refresh();
		    					
		    				}else{
		    					Toast.makeText(getApplicationContext(), "删除失败！"+ret.message, Toast.LENGTH_SHORT).show();
		    				}
		    				
//		    				mbMessageCenter.setText("Delete files:  " + (0 == ret.error_code ? "success" : "failed  " + ret.message));
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
    }
    
    //upload local file
    private void refresh(){    	
    	list();
    }  
}
