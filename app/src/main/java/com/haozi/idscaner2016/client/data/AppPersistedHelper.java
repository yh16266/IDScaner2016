/**
 * 
 */
package com.haozi.idscaner2016.client.data;

/**
 * 类名：AppPersistedHelper
 * @author yinhao
 * @功能 主要用于存储APP配置和用户基础信息
 * @创建日期 2015年10月8日 上午9:55:43
 * @备注 [修改者，修改日期，修改内容]
 */
public class AppPersistedHelper extends BasePersistedHelper {
	/**存储空间名*/
	protected static final String NAME = "AppPersistedHelper";
	/**表格数据KEY*/
	protected class KEYS{
		/** 用户ID */
		public static final String USER_ID = "USER_ID";
		/** 用户名 */
		public static final String ACCOUNT = "ACCOUNT";
		/** 登录密码 */
		public static final String PASSWORD = "PASSWORD";
		/** 真实姓名 */
		public static final String REAL_NAME = "REAL_NAME";
		/** 昵称 */
		public static final String NICK_NAME = "NICK_NAME";
		/** 头像路径 */
		public static final String PHOTOURL = "PHOTOURL";
		/** 头像路径 */
		public static final String USER_TYPE = "USER_TYPE";

	}
	
	/**静态单例初始化*/
	private static final AppPersistedHelper INSTANCE = new AppPersistedHelper();
	/**单例静态引用*/
	public static AppPersistedHelper getInstance() {
		return INSTANCE;
	}

	/**
	 * 获取当前用户ID
	 * */
	public long getNowUserId(){
		return getLong(KEYS.USER_ID);
	}

	/**
	 * 设置当前用户ID
	 * */
	public void setNowUserId(long value){
		putLong(KEYS.USER_ID, value);
	}
}
