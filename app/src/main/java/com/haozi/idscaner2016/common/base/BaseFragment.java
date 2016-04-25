package com.haozi.idscaner2016.common.base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.haozi.idscaner2016.client.control.DXProgressDialog;

public abstract class BaseFragment extends Fragment implements OnClickListener, Callback{

	protected LayoutInflater inflater;
	/**广播接收器*/
	protected BroadcastReceiver br;
	/**主处理UI器 */
	protected Handler mMainHandler;
	/**进度条 */
	private ProgressDialog progressDialog;
	/**日志TAG*/
	protected String TAG = getClass().getSimpleName();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public LayoutInflater getInflater() {
		return inflater;
	}

	/**
	 * 广播接收处理
	 * */
	protected void brReceive(Context context, Intent intent) {
		
	}
	
	/**
	 * 注册广播接收器
	 * */
	protected void registerReceiver(IntentFilter itFilter) {
		br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				brReceive(context, intent);
			}
		};
		getActivity().registerReceiver(br, itFilter);
	}
	
	/**
	 * 取消广播注册
	 * */
	protected void unregisterReceiver() {
		if(br != null) {
			getActivity().unregisterReceiver(br);
			br = null;
		}
	}

	/**
	 * 注册UI处理器
	 * */
	protected void registerHandler() {
		if(mMainHandler == null){
			mMainHandler = new Handler(this);
		}
	}
	
	/**
	 * 处理UIMsg请求
	 * */
	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}
	
	/**
	 * 发送空内容广播
	 * */
	protected void sendEmptyMessage(int what){
		if(mMainHandler != null){
			mMainHandler.sendEmptyMessage(what);
		}
	}
	
	/**
	 * 销毁当前Fragment
	 * */
	@Override
	public void onDestroy() {
		//取消广播注册
		unregisterReceiver();
		//关闭进度条
		dismissProgress();
		//销毁
		super.onDestroy();
	}

	/**
	 * 打开进度条
	 * */
	protected void showProgress(int messageRes){
		String message = getResources().getString(messageRes);
		if(progressDialog == null){
			progressDialog = DXProgressDialog.show(getActivity(), message);
		}else{
			progressDialog.setMessage(message);
			progressDialog.show();
		}
	}
	
	/**
	 * 关闭进度条
	 * */
	protected void dismissProgress(){
		if(progressDialog != null && progressDialog.isShowing() == true){
			progressDialog.dismiss();
		}
	}
}
