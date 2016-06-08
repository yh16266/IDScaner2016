package com.haozi.idscaner2016.client.bean.client;

import com.haozi.idscaner2016.client.bean.BaseEntity;
import com.haozi.idscaner2016.client.bean.EntityDitionary;

import java.math.BigDecimal;

/**
 * 
 * @Description:用户账号 ,允许一个用户有多个对应的账号
 * @author jimmy
 * @version 1.0
 */
public class Accounts extends BaseEntity {

	private static final long serialVersionUID = 1L;
	/** 用户名,可以为手机号、微信号、qq号等*/
	private String username;
	/** 用户名姓名*/
	private String realname;
	/** 昵称*/
	private String nickname;
	/**  登录密码*/
	private String password; 
    /** 用户类型,参考Constants中的USER_TYPE*/
    private long userId;
    /** 头像地址*/
    private String photoUrl;
    /** 电话号码*/
    private String mobile;
    /** 电话号码*/
    private String userType;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUserType() {
		return userType;
	}

	public EntityDitionary.UserType UserType() {
		return EntityDitionary.UserType.ValueOf(userType);
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
}
