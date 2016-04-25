package com.haozi.idscaner2016.common.utils;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import com.haozi.idscaner2016.common.app.MyApp;
import com.haozi.idscaner2016.common.base.BaseObject;
import com.haozi.idscaner2016.constants.IConstants;

/**
 * 类名：DXUtil
 * @author Yh
 * 涂鸦
 * 创建日期：20135年9月29日
 * [修改者，修改日期，修改内容]
 */
public class DXUtil extends BaseObject {
	
	protected static String TAG = "DXUtil";
	
	/**
	 * 发送广播
	 * @param it 广播
	 */
	public static void sendBroadcast(Intent it) {
		DXLog.d(TAG, "sendBroadcast: " + it.toString());
		MyApp.getInstance().sendBroadcast(it);
	}
	
	/**
	 * 获取当前运行activity名字
	 * */
	public static String getCurActivityClassName() {
		ActivityManager am = (ActivityManager) MyApp.getInstance().getSystemService(Activity.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runlist = am.getRunningTasks(1);
		if(runlist != null && runlist.size() > 0 && runlist.get(0) != null) {
			ComponentName cn = runlist.get(0).topActivity;
			return cn.getClassName();
		}
		
		return null;
	}
	
	/**
	 * 打开指定APK
	 * @param File
	 * */
	public static void openAPK(File file) {
		Intent install = new Intent(Intent.ACTION_VIEW);
		install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		install.setDataAndType(Uri.fromFile(file), IConstants.CONTENT_TYPE_APP);
	  	MyApp.getInstance().startActivity(install);
	}
	
}
