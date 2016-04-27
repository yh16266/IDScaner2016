package com.haozi.idscaner2016.constants;

import android.content.Intent;

public class IActionIntent {
	
	/**检查新版本*/
	public static final String ACTION_CHECK_NEW_VERSION = "ACTION_CHECK_NEW_VERSION";
	/**启动检查新版本任务*/
	public static final String ACTION_START_CHECK_NEW_VERSION = "ACTION_START_CHECK_NEW_VERSION";
	
	/** 显示标题上的警告提示 */
	public static final String ACTION_HEADER_WARING_SHOW = "ACTION_HEADER_WARING_SHOW";
	/** 隐藏标题上的警告提示 */
	public static final String ACTION_HEADER_WARING_HIDE = "ACTION_HEADER_WARING_HIDE";
	/** 网络链接断开警告提示 */
	public static final String ACTION_NET_DISCONNECTION_WARING = "ACTION_NET_DISCONNECTION_WARING";
	/** 网络链接成功警告提示 */
	public static final String ACTION_NET_CONNECTION_WARING = "ACTION_NET_CONNECTION_WARING";

	/** 广播：登陆成功 */
	public static final String ACTION_LOGIN_SUCCESS = "ACTION_LOGIN_SUCCESS";
	/** 广播：注销成功 */
	public static final String ACTION_LOGOFF_SUCCESS = "ACTION_LOGOFF_SUCCESS";
	
	/** 广播：SOCEKT链接通知 */
	public static final Intent ACTION_SOCKET_CONNECTED = null;

	/*-------------------------------------------------参数常量-------------------------------------------------------*/
	
	/** 身份证号码 */
	public static final String INTENTEXTRA_IDNUM = "INTENTEXTRA_IDNUM";
}
