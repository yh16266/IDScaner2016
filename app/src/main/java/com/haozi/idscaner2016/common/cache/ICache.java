package com.haozi.idscaner2016.common.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.haozi.idscaner2016.common.base.BaseObject;
import com.haozi.idscaner2016.common.utils.SystemUtil;

public class ICache extends BaseObject {
	
	/**所有Activity列表 */
	public static final ArrayList<Activity> alllActivityList = new ArrayList<Activity>();

	/**下载任务列表*/
	public static final ArrayList<Long> downloadFileTask = new ArrayList<Long>();
	/**图片缓存*/
	private static LruCache<Object, Bitmap> bitmapCache;
	/**交易数据缓存*/
	private static Map<String,Double> tradeData = new HashMap<String, Double>();
	
	public static final String HEAD_ICON_APK_RESOURCE_THUMBNAIL = "apk_res_th";
	
	/**
	 * 初始化缓存
	 * */
	public static void initCache() {
		//通过屏幕分辨率和版本设置初始化缓存大小
	    int cacheSize = 0;
	    if(!SystemUtil.checkVersionGreaterThan10() || SystemUtil.is480X320()) {
	    	cacheSize = 3 * 1024 * 1024;
	    } else {
	    	cacheSize = 6 * 1024 * 1024;
	    }
	    //初始化缓存
	    bitmapCache = new LruCache<Object, Bitmap>(cacheSize) {
			@Override
			protected void entryRemoved(boolean evicted, Object key, Bitmap oldValue, Bitmap newValue) {
				super.entryRemoved(evicted, key, oldValue, newValue);
					oldValue = null;
			}
			@Override
			protected int sizeOf(Object key, Bitmap value) {
				int lsize = value != null ? (value.getRowBytes() * value.getHeight()) : 0;
				return lsize; 
			} 
	    }; 
	}
	
	/**
	 * 获取程序图片lru缓存
	 * */
	public static LruCache<Object, Bitmap> getBitmapCache() {
		return bitmapCache;
	}
	
	/**
	 * 清空所有缓存
	 * */
	public static final void clearAll(){
		alllActivityList.clear();
		bitmapCache.evictAll();
		downloadFileTask.clear();
		tradeData.clear();
	}
	
}
