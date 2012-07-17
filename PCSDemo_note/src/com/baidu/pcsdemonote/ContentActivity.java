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
	
	private final static String mbRootPath = "/apps/云端记事本";
	
	private Handler uiThreadHandler = null;
	
	private String access_token = null ;
	
	private String fileTitle = null;
	private String name = null;
	
	private ImageButton create = null;
	private ImageButton refresh = null;
	
	private int flag = 0 ;
	
	
	private ArrayList<String> fileNameList = new ArrayList<String>();

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cotentshow);
        
        Intent intent = getIntent();
        
        access_token = intent.getStringExtra("access_token");
                
        create = (ImageButton)findViewById(R.id.btncreate);
        refresh = (ImageButton)findViewById(R.id.btnrefresh);
        
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
		    		BaiduPCSAPI api = new BaiduPCSAPI();
		    		api.setAccessToken(access_token );
		    		String path = mbRootPath;

		    		final PCSActionInfo.PCSListInfoResponse ret = api.list(path, "name", "asc");
		    				    		
		    		uiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				
		    				ArrayList<HashMap<String, String>> list =new ArrayList<HashMap<String,String>>();   
		    	            //HashMpa为键值对类型。第一个参数为建，第二个参数为值   
		    				if("[]" != ret.list.toString()){
		    					   			    	            
			    	            for(Iterator<PCSFileInfoResponse> i = ret.list.iterator(); i.hasNext();){
			    	            	
			    	            	HashMap<String, String> map =new HashMap<String, String>();
			    	            	
			    	            	PCSFileInfoResponse info = i.next();
			    	            				    	            	
			    	         	    String path = info.path;			    	         	    
			    	         	 			    	         	    
			    	         	    Date date = new Date(info.mTime*1000);
			    	         	    
			    	         	    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
			    	         	    String dateString = formatter.format(date);
			    	         	    
//			    	         	    String time = date.getYear()+"/"+date.getMonth()+"/"+date.getDate()+"/"+date.getHours()+":"+date.getMinutes();
			    	         	    
			  			 			    	          			    	            	
			    	            	String fileName = path.substring(mbRootPath.length()+1,path.lastIndexOf("."));		    	            	
			    	            	map.put("file_name", fileName);			    	            	
			    	            	map.put("time", dateString);
			    	            	
			    	            	list.add(map); 	            	
			    	            	fileNameList.add(fileName);							    				    	             
			    	            }			    	               
			    	        }else{
		    					list.clear();
		    					Toast.makeText(getApplicationContext(), "您的文件夹为空！", Toast.LENGTH_SHORT).show();		    					
		    				}    
		    				//生成一个SimpleAdapter类型的变量来填充数据   
			    	         SimpleAdapter listAdapter =new SimpleAdapter(ContentActivity.this, list, R.layout.content, new String[]{"file_name","time"}, new int[]{R.id.file_name,R.id.time});   
			    	        //设置显示ListView   
			    	         setListAdapter(listAdapter); 			    	            
			    	         Toast.makeText(getApplicationContext(), R.string.refresh, Toast.LENGTH_SHORT).show();

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
    	
    	name = name.substring(name.indexOf("=")+1, name.lastIndexOf(","));
    	 
		
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
				
                if(fileTitle.equals("")){
                	
                	flag = 1;
                	
                }
            				
				for(Iterator<String> file = fileNameList.iterator();file.hasNext();){
					
					if (file.next().equals(fileTitle)){
						
						flag = 2;
					}
										
				}
				
				if(flag == 1){
					Toast.makeText(getApplicationContext(), "文件名不能为空！", Toast.LENGTH_SHORT).show();
				}else{
					
					if(flag == 2)
					{
						Toast.makeText(getApplicationContext(), "文件名已存在！", Toast.LENGTH_SHORT).show();
					
					}else{					
						Intent create_intent = new Intent();
						
						create_intent.putExtra("access_token", access_token);
						create_intent.putExtra("fileTitle", fileTitle);
						
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
        exitAlert.setIcon(R.drawable.alert_dark).setTitle("提示...").setMessage("你确定要离开客户端吗？");
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
