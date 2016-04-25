/**
 * Project Name:
 * File Name:MyApp.java
 * Package com.zfxy.jinebao.common.app
 * Date:2015-09-25
 * Copyright (c) 2015, zfxy All Rights Reserved.
 *
 */

package com.haozi.idscaner2016.common.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.haozi.idscaner2016.client.data.sqlite.UserTable;
import com.haozi.idscaner2016.common.cache.ICache;
import com.haozi.idscaner2016.common.service.MainService;
import com.haozi.idscaner2016.common.utils.DXLog;
import com.haozi.idscaner2016.common.utils.FileUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 类名：MyApp
 * @author yinhao
 * @功能 APP的主页面
 * @创建日期 2015年10月8日 上午9:55:43
 * @备注 [修改者，修改日期，修改内容]
 */
public class MyApp extends Application {

	protected String TAG = getClass().getSimpleName();
	/** APP引用 */
	private static MyApp mInstance;
	/**屏幕密度*/
	public static float density;
	/** 屏幕宽*/
	public static float screenW;
	/**屏幕高*/
	public static float screenH;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		init();
		
	}

	/**
	 * 初始化
	 */
	private void init() {

		FileUtil.checkSDCard(true);

		density = getResources().getDisplayMetrics().density;
		screenW = getResources().getDisplayMetrics().widthPixels;
		screenH = getResources().getDisplayMetrics().heightPixels;

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			/**
			 * 得到错误日志
			 * @param ex
			 * @return
			 */
			private String getCrashInfoToFile(Throwable ex) {   
				StringWriter info = new StringWriter();   
		        PrintWriter pwter = new PrintWriter(info);   
		        ex.printStackTrace(pwter);   
		        Throwable cause = ex.getCause();   
		        while (cause != null) {   
		            cause.printStackTrace(pwter);   
		            cause = cause.getCause();   
		        }   
		        String result = info.toString();
		        pwter.close();
		        
		        return result; 
		    }
			
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				// TODO Auto-generated method stub
				if (ex != null) {
					ex.printStackTrace();
					DXLog.e(TAG, getCrashInfoToFile(ex), true);
		        }
	            android.os.Process.killProcess(android.os.Process.myPid());  
	            System.exit(1);  
			}
		});

		ICache.initCache();
		// app主服务
		startService(new Intent(this, MainService.class));

		UserTable.getInstance().initUserData();
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static MyApp getInstance() {
		return mInstance;
	}
	
	/**
	 * 退出应用程序
	 */
	public void exitApp() {
		try {
			finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.killBackgroundProcesses(this.getPackageName());
			ICache.clearAll();
			System.exit(0);
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for(int i = 0, size = ICache.alllActivityList.size(); i < size; i++) {
			if (null != ICache.alllActivityList.get(i)) {
				ICache.alllActivityList.get(i).finish();
			}
		}
		ICache.alllActivityList.clear();
	}
}
