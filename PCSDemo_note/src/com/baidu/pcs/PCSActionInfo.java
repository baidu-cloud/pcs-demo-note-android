package com.baidu.pcs;

import java.util.List;

/*
 * this class defines the response from PCS HTTP server
 */
public final class PCSActionInfo {
	
	/*
	 * normal response
	 */
	public static class PCSSimplefiedResponse {
		//0 means success
		public int error_code = -1;
		
		// status message if failed
		public String message = null;
	}

	/*
	 * the data structure of quota, including the space info
	 */
	public static class PCSQuotaResponse {
		
		//0 means success
		public int error_code = -1;
		
		// The total byte
		public long total = 0L;

		// The used bytes
		public long usded = 0L;
		
		// status message if failed
		public String message = null;
	}
	
	/*
	 * Server-returned data for Request line£ºGET /Create
	 */
	public static class PCSFileInfoResponse {

		// if upload successfully, 0 means success
		public int error_code = -1;
		
		// status message if failed
		public String message = null;

		// final path in PCS
		public String path = null;
		
		// modified time
		public long mTime = 0L;
		
		// created time
		public long cTime = 0L;
		
		// md5 value
		public String md5 = null;
		
		// the fs id
		public long fs_id = 0L;
		
		// size
		public int size = -1;
		
		// is dir
		public boolean isDir = false;
		
		// if it has sub folder
		public boolean hasSubFolder = false;
	}
	
	/*
	 * list info
	 */
	public static class PCSListInfoResponse {

		// if upload successfully, 0 means success
		public int error_code = -1;
		
		// status message if failed
		public String message = null;

		// list info
		public List<PCSFileInfoResponse> list = null;
	}
	
	/*
	 * list info
	 */
	public static class PCSFileFromToResponse {

		// if upload successfully, 0 means success
		public int error_code = -1;
		
		// status message if failed
		public String message = null;

		// list info
		public List<PCSFileFromToInfo> list = null;
	}
	
	/*
	 * copy file, move file info
	 */
	public static class PCSFileFromToInfo {
		
		// the source file
		public String from = null;

		// the target location
		public String to = null;
	}
	
}
