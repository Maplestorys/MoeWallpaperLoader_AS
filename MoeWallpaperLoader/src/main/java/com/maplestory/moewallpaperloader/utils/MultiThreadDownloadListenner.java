package com.maplestory.moewallpaperloader.utils;

public abstract class MultiThreadDownloadListenner 
{
	/**
	 * 下载成功方法
	 */
	public abstract void onSuccess();
	
	/**
	 * 下载中
	 */
	public abstract void onProgress(long bytes, float percent);
}
