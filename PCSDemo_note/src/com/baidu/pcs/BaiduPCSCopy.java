package com.baidu.pcs;

import java.util.List;

public class BaiduPCSCopy extends BaiduPCSFromToAction{

	//
	// Constructor
	//
	BaiduPCSCopy() {
		super(Value_Action);
		// TODO Auto-generated constructor stub
	}
	
	//
	// copy file
	//
	protected PCSActionInfo.PCSFileFromToResponse copy(String from, String to){
		return super.execute(from, to);
	}
	
	//
	//
	//
	protected PCSActionInfo.PCSFileFromToResponse copy(List<PCSActionInfo.PCSFileFromToInfo> info){
		return super.execute(info);
	}
	
	// action data
	private final static String Value_Action = "copy";
}
