package com.baidu.pcs;

public abstract class BaiduPCSStatusListener {

		/**
		 * Gets called when some bytes have been transferred since the last time it was called and the progress interval has
		 * passed.
		 * 
		 * @param bytes
		 *            the number of bytes transferred.
		 * @param total
		 *            the size of the file in bytes.
		 */
		public abstract void onProgress(long bytes, long total);

		/**
		 * Return how often transferred bytes should be reported to this listener, in milliseconds. It is not
		 * guaranteed that updates will happen at this exact interval, but that at least this amount of time will pass
		 * between updates. The default implementation always returns 500 milliseconds.
		 */
		public long progressInterval() {
			return 500;
		}

		/**
		 * Whether to continue the download process. It is not guaranteed that updates will stop in time. The default
		 * implementation always returns 500 milliseconds.
		 */
		public boolean toContinue() {
			return true;
		}
}
