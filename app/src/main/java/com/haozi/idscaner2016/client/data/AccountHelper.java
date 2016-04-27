/**
 * 
 */
package com.haozi.idscaner2016.client.data;

import com.haozi.idscaner2016.client.bean.client.Accounts;
import com.haozi.idscaner2016.client.data.AppPersistedHelper.KEYS;
import com.haozi.idscaner2016.common.cache.ICache;
import com.haozi.idscaner2016.common.utils.StringUtil;

/**
 * 类名：UTokenAcountHelper
 * @author YH
 * 创建日期：2015年4月22日
 * [修改者，修改日期，修改内容]
 */
public class AccountHelper extends BasePersistedHelper{
	
	private static AccountHelper instance;
	
	public AccountHelper(String userId,String name) {
		super(name);
	}
	
	public synchronized static AccountHelper getInstance() {
		//获取用户ID
		long myID = AppPersistedHelper.getInstance().getNowUserId();
		myID = myID < 0 ? myID : 0;
		//设置保存设置的名称
		final String tempSpName =  myID + ".setting";
		//创建实例
		if(instance == null || !StringUtil.equals(instance.getSpName(), tempSpName)) {
			instance = new AccountHelper(String.valueOf(myID), tempSpName);
		}
        return instance;
    }

	public String getSpName() {
		return NAME;
	}

	public void setSpName(String spName) {
		NAME = spName;
	}
	
	/**
	 * 获取当前用户信息
	 * @return UserInfo
	 * */
	public Accounts getMyinfo() {
		Accounts myinfo = new Accounts();
		if(IsUserLogin() == false){
			return myinfo;
		}
		myinfo.setUserId(getLong(KEYS.USER_ID));
		myinfo.setUsername(getString(KEYS.ACCOUNT));
		myinfo.setPassword(getString(KEYS.PASSWORD));
		myinfo.setRealname(getString(KEYS.REAL_NAME));
		myinfo.setNickname(getString(KEYS.NICK_NAME));
		myinfo.setPhotoUrl(getString(KEYS.PHOTOURL));
		myinfo.setUserType(getString(KEYS.USER_TYPE));

		return myinfo;
	}

	/**
	 * 保存当前用户信息到本地缓存
	 * @param myinfo
	 * */
	public void putMyInfo(Accounts myinfo) {
		putValueToMyinfo(KEYS.ACCOUNT, myinfo.getUsername());
		putValueToMyinfo(KEYS.PASSWORD, myinfo.getPassword());
		putValueToMyinfo(KEYS.USER_ID, myinfo.getUserId());
		putValueToMyinfo(KEYS.REAL_NAME, myinfo.getRealname());
		putValueToMyinfo(KEYS.NICK_NAME, myinfo.getNickname());
		putValueToMyinfo(KEYS.PHOTOURL, myinfo.getPhotoUrl());
		putValueToMyinfo(KEYS.USER_TYPE, myinfo.getUserType());
	}

	/**
	 * 将不同字段的值存储到当前用户信息缓存
	 * @param key
	 * @param value
	 * */
	private <T>void putValueToMyinfo(String key,T value){
		putValueToMyinfo(key,value,true);
	}

	/**
	 * 将不同字段的值存储到当前用户信息缓存
	 * @param key
	 * @param value
	 * */
	private <T>void putValueToMyinfo(String key,T value,boolean isSaveNull){
		if(isSaveNull == true && value == null){
			putString(key, "");
		}else if(value instanceof String && StringUtil.isEmpty(value.toString())){
			putString(key, "");
		}else{
			save(key, value);
		}
	}
	
	/**
	 * 判断当前用户是否已经登录
	 * @return boolean
	 * */
 	public static boolean IsUserLogin(){
		//获取当前用户ID
		//String myID = AppPersistedHelper.getInstance().getNowUserId()+"";
		//if(StringUtil.isEmpty(myID) || myID.equalsIgnoreCase("0")){
 		long myID = AppPersistedHelper.getInstance().getNowUserId();
		if(myID <= 0){
			return false;
		}
		return true;
	}

	/**
	 * 用户信息记录
	 * @return boolean
	 * */
	public static void LoginOn(Accounts user){
		AppPersistedHelper.getInstance().setNowUserId(user.getUserId());
		AccountHelper.getInstance().putMyInfo(user);
	}

	/**
	 * 用户信息销毁
	 * @return boolean
	 * */
	public static void LoginOff(){
		AppPersistedHelper.getInstance().setNowUserId(0);
		//AccountHelper.getInstance().cleanMyInfo();
		ICache.clearAll();
	}

	/**
	 * 登录失败处理
	 * */
	public static void LoginFailed(){
		AppPersistedHelper.getInstance().setNowUserId(0);
		AccountHelper.getInstance().cleanMyInfo();
	}

	/**
	 * 登录失败处理
	 * */
	public static void setLastAccount(String account){
		getInstance().putValueToMyinfo(KEYS.LAST_USERACCOUNT, account);
	}

	/**
	 * 登录失败处理
	 * */
	public static String getLastAccount(){
		return getInstance().getString(KEYS.LAST_USERACCOUNT);
	}

	public void cleanMyInfo(){
		putMyInfo(new Accounts());
	}


}
