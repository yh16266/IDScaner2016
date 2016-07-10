/*
 * 文件名：WellcomeActivity.java
 * 包含类名列表
 * 版本信息：V1.0
 * 创建日期：2014年12月2日
 * 版权声明
 */
package com.haozi.idscaner2016.client.ui.wellcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.bean.client.Accounts;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.client.data.AccountHelper;
import com.haozi.idscaner2016.client.data.sqlite.UserTable;
import com.haozi.idscaner2016.client.ui.home.CodeScanActivity;
import com.haozi.idscaner2016.client.ui.home.HomeNewActivity;
import com.haozi.idscaner2016.client.ui.home.UserSettingActivity;
import com.haozi.idscaner2016.client.utils.ViewUtils;
import com.haozi.idscaner2016.common.base.BaseCompatActivity;
import com.haozi.idscaner2016.common.utils.StringUtil;

/**
 * 类名：WellcomeActivity
 * @author yinhao
 * @实现的主要功能: TODO
 * @创建日期：2014年12月2日上午10:01:15
 * @其他 [修改者：，修改日期：，修改内容：]
 */
public class WellcomeActivity extends BaseCompatActivity {
	
	private static final int HDLER_EXIT_WELLCOME = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//注册处理器
		registerHandler();
		//启动版本更新
		//sendBroadcast(new Intent(IActionIntent.ACTION_START_CHECK_NEW_VERSION));
		setContentView(R.layout.wellcome_activity);
	}

	protected void initView(){
		findViewById(R.id.btn_login).setOnClickListener(this);
		findViewById(R.id.txv_unlogin).setOnClickListener(this);
		String lastAccount = AccountHelper.getLastAccount();
		if(!StringUtil.isEmpty(lastAccount)){
			ViewUtils.setEditTextTxt(this,R.id.edt_username,lastAccount);
		}
		//ViewUtils.setEditTextTxt(this,R.id.edt_password,"admin2016");
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()){
			case R.id.btn_login:
				login();
				break;
			case R.id.txv_unlogin:
				gotoMain();
				break;
		}
	}

	private void login(){
		AccountHelper.LoginOff();
		String username = ViewUtils.getEditString(this,R.id.edt_username);
		if(StringUtil.isEmpty(username)){
			DXToast.show("请输入用户名");
			return;
		}
		String password = ViewUtils.getEditString(this,R.id.edt_password);
		if(StringUtil.isEmpty(password)){
			DXToast.show("请输入密码");
			return;
		}
		AccountHelper.setLastAccount(username);
		Accounts rst = UserTable.getInstance().login(username,password);
		if(rst == null){
			DXToast.show("账号或者密码错误，登陆失败");
		}else{
			DXToast.show("登陆成功");
			AccountHelper.getInstance().LoginOn(rst);
			gotoMain();
		}
	}

	/**
	 * 跳转到主页面
	 * */
	private void gotoMain() {
		Intent intent = new Intent();
		intent.setClass(this, HomeNewActivity.class);
//		intent.setClass(this, UserSettingActivity.class);
		startActivity(intent);
		finish();
	}
	
	/**
	 * handler消息处理
	 * */
	@Override
	public boolean handleMessage(Message msg) {
		if(!isFinishing()) {
			switch(msg.what) {
			case HDLER_EXIT_WELLCOME:
				gotoMain();
				break;
			}
		}
		return super.handleMessage(msg);
	}
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
	}
}
