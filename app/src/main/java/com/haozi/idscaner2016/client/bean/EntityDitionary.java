/**
 * 
 */
package com.haozi.idscaner2016.client.bean;

import com.haozi.idscaner2016.common.utils.StringUtil;

/**
 * 类名：EntityDitionary
 * @author yinhao
 * @功能
 * @创建日期 2015年12月7日 下午5:43:54
 * @备注 [修改者，修改日期，修改内容]
 */
public class EntityDitionary {

	/**用户类型*/
	public enum UserType{

		/**管理员*/
		admin(1),
		/**保安*/
		secure(2),
		/**访客*/
		visitor(3);

    	private int mValue;
    	
    	private UserType(int value){
    		mValue = value;
    	}
    	
    	public int getValue(){
    		return mValue;
    	}

    	public String getValueStr(){
    		return String.valueOf(mValue);
    	}

		public static UserType ValueOf(String valueStr) {
			if(StringUtil.isEmpty(valueStr)){
				return null;
			}
			int value = Integer.valueOf(valueStr);
			return  ValueOf(value);
		}

		public static UserType ValueOf(int value) {
			//获取所有枚举
			UserType[] arr = UserType.values();
			if(arr == null || arr.length == 0){
				return null;
			}
			//遍历获取数据
			for(UserType type : arr){
				if(type.mValue == value){
					return type;
				}
			}
			return null;
		}
    }

	/**字典类型*/
	public enum SysCodeType{

		/**单位*/
		unit("unit");

		private String mValue;

		private SysCodeType(String value){
			mValue = value;
		}

		public String getValue(){
			return String.valueOf(mValue);
		}
	}
}
