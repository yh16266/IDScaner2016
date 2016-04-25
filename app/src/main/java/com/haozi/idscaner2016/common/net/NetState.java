package com.haozi.idscaner2016.common.net;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

import com.haozi.idscaner2016.common.app.MyApp;
import com.haozi.idscaner2016.common.base.BaseObject;
import com.haozi.idscaner2016.common.utils.DXLog;
import com.haozi.idscaner2016.common.utils.DXUtil;
import com.haozi.idscaner2016.constants.IActionIntent;

/**
 * 类名：NetState 网络状态工具类
 * @author YH
 * 创建日期：2015年4月18日
 * [修改者，修改日期，修改内容]
 */
public class NetState extends BaseObject {
	
	private static NetState INSTANCE;
	
	public synchronized static NetState getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new NetState();
		}
		
		return INSTANCE;
	}
	
	public NetState() {
		checkNetStatus();
	}
	
	/**
	 * 检查网络状态，并发送广播
	 * */
	public void checkNetStatus() {
		/*----------------------------yh add 20170701--------------------------------*/
		if(isWifiOrMobile()){
			DXUtil.sendBroadcast(new Intent(IActionIntent.ACTION_NET_CONNECTION_WARING));
		}else{
			DXUtil.sendBroadcast(new Intent(IActionIntent.ACTION_NET_DISCONNECTION_WARING));
		}
		/*---------------------------------------------------------------------------*/
		DXLog.d(TAG, "checkNetStatus : isWifi=" + isWifi() + " : isMobile=" + isMobile());
	}
	
	/**
	 * 网络是否联通（wifi或者移动网络）
	 * */
	public boolean isWifiOrMobile() {
		return isWifi() || isMobile();
	}

	/**
	 * wifi是否链接成功
	 * */
	public boolean isWifi() {
		ConnectivityManager connManager = (ConnectivityManager) MyApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		boolean isWifi = State.CONNECTED == state;
		return isWifi;
	}

	/**
	 * 移动网络是否链接成功
	 * */
	public boolean isMobile() {
		ConnectivityManager connManager = (ConnectivityManager) MyApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState(); 
		boolean isMobile = State.CONNECTED == state;
		return isMobile;
	}
	
}
