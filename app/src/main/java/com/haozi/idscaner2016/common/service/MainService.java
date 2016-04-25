package com.haozi.idscaner2016.common.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.haozi.idscaner2016.common.base.BaseService;
import com.haozi.idscaner2016.common.net.NetState;
import com.haozi.idscaner2016.common.utils.DXLog;

/**
 * 类名：MainService
 * @author Yang.Gao
 * 涂鸦
 * 创建日期：2013年12月9日
 * [修改者，修改日期，修改内容]
 */
public class MainService extends BaseService {
	
	/**广播接收器*/
	private BroadcastReceiver br = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String strAction = intent.getAction();
			DXLog.d(TAG, "onReceive strAction=" + strAction);
			if(ConnectivityManager.CONNECTIVITY_ACTION.equals(strAction)) {
				NetState.getInstance().checkNetStatus();
				//掉线处理
				if(NetState.getInstance().isWifiOrMobile()) {
					
				}
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		//清空
		IntentFilter filter = new IntentFilter();   
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		//网络状态监听
		registerReceiver(br, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(br);
	}
	
}
