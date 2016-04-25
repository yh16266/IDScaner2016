package com.haozi.idscaner2016.common.utils;

import java.util.Set;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.haozi.idscaner2016.common.base.BaseObject;

/**
 * 类名：DXLog
 * 
 * @author Yh 创建日期：2015年9月29日 [修改者，修改日期，修改内容]
 */
public class DXLog extends BaseObject {

	public static final boolean isDebug = true;

	public static void d(String tag, String msg, boolean isWriteFile) {
		if (isDebug) {
			Log.d(tag, msg);
			
			if(isWriteFile) {
				FileUtil.writeToLogFile(msg);
			}
		}
	}

	public static void d(String tag, String msg) {
		d(tag, msg, false);
	}

	public static void e(String tag, String msg, boolean isWriteFile) {
		if (isDebug) {
			Log.e(tag, msg);
			
			if(isWriteFile) {
				FileUtil.writeToLogFile(msg);
			}
		}
	}

	public static void e(String tag, String msg) {
		e(tag, msg, false);
	}

	/**
	 * Show detail information of cursor.
	 * @param tag Log tag.
	 * @param prefix Prefix to add before cursor information.
	 * @param csr
	 */
	public static void showCursor(String tag, String prefix, Cursor csr) {
		StringBuilder builder = new StringBuilder(prefix);
		builder.append("\r\n");
		int columnCount = csr.getColumnCount();
		while (csr.moveToNext()) {
			for (int i = 0; i < columnCount; i++) {
				builder.append(csr.getColumnName(i)).append("=")
						.append(csr.getString(i)).append("  ");
			}
			builder.append("\r\n");
		}
		d(tag, builder.toString());
	}

	/**
	 * Show detail information of intent.
	 * @param tag  Log tag.
	 * @param prefix Prefix to add before intent information.
	 * @param intent
	 */
	public static void showIntent(String tag, String prefix, Intent intent) {
		StringBuilder builder = new StringBuilder(prefix);
		builder.append("\r\n");
		builder.append("Action: ").append(intent.getAction());
		Bundle extra = intent.getExtras();
		Set<String> keys = extra.keySet();
		for (String key : keys) {
			builder.append(key).append("=").append(extra.get(key)).append("  ");
		}
		d(tag, builder.toString());
	}
}
