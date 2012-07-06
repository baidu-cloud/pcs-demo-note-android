package com.baidu.pcs;

import java.util.List;

public class BaiduPCSMove extends BaiduPCSFromToAction{

	//
	// Constructor
	//
	BaiduPCSMove(){
		super(Value_Action);
		// TODO Auto-generated constructor stub
	}
	
	//
	// move file
	//
	protected PCSActionInfo.PCSFileFromToResponse move(String from, String to){
		return super.execute(from, to);
	}
	
	//
	// move file
	//
	protected PCSActionInfo.PCSFileFromToResponse move(List<PCSActionInfo.PCSFileFromToInfo> info){
		return super.execute(info);
	}

	// action data
	private final static String Value_Action = "move";
}
