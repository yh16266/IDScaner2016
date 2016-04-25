package com.haozi.idscaner2016.constants;


import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.common.app.MyApp;

public class IErrorCode {
	
	/**服务器返回代码：正常*/
	public static final int RETURN_CODE_SUCCESS = 200;
	/**服务器返回代码：请求超时*/
	public static final int ERROR_CODE_NG101 = -101;
	
	/** 服务器提供的错误代码 */
	public enum SERVER_ERROR {
		
		/**请求错误*/
		ERROR_PARAM_REQUEST(400, R.string.error_param_request),
		/**传参错误*/
		ERROR_PARAM_ERROR(401,R.string.error_param_error),

		/**传参错误*/
		ERROR_BALANCE_NOTENOUGH(601,R.string.error_balance_notenough),
		
		/**登录失败*/
		ERROR_LOGIN_FAILED(412,R.string.error_login_failed),
		/**用户不存在*/
		ERROR_LOGIN_NOACCOUNT(410,R.string.error_login_noaccount),
		/**用户密码错误*/
		ERROR_LOGIN_PSWERROR(411,R.string.error_login_pswerror);
		
		// 定义私有变量
		private final int mCode;
		// 定义私有变量
		private final String mContent;
		// 构造函数，枚举类型只能为私有
		private SERVER_ERROR(int code,int contentId) {
			this.mCode = code;
			this.mContent = MyApp.getInstance().getString(contentId);
		}

		public String getErrorContent() {
			return mContent;
		}

		public int getErrorCode(){
			return mCode;
		}
		
		@Override
		public String toString() {
			return String.valueOf(this.mContent);
		}

		/**
		 * @param errorCode
		 */
		public static SERVER_ERROR valueOf(int errorCode) {
			//获取所有枚举
			SERVER_ERROR[] arr = SERVER_ERROR.values();
			if(arr == null || arr.length == 0){
				return null;
			}
			//遍历获取数据
			for(SERVER_ERROR err : arr){
				if(err.mCode == errorCode){
					return err;
				}
			}
			return null;
		}
	}
	
	/** 系统错误代码*/
	public enum SYSTEM_ERROR {
		/**请求错误*/
		ERROR_CODE_400(400,R.string.error_code_400),
		/**请求资源不存在*/
		ERROR_CODE_404(404,R.string.error_code_404),
		/**服务器内部错误*/
		ERROR_CODE_500(500,R.string.error_code_500),
		/**未知错误*/
		ERROR_CODE_999(9999,R.string.error_code_999),
		/**链接超时*/
		ERROR_CODE_NG101(IErrorCode.ERROR_CODE_NG101,R.string.error_request_failed_timeout);
		
		// 定义私有变量
		private final int mCode;
		// 定义私有变量
		private final String mContent;
		// 构造函数，枚举类型只能为私有
		private SYSTEM_ERROR(int code,int contentId) {
			this.mCode = code;
			this.mContent = MyApp.getInstance().getString(contentId);
		}

		public String getErrorContent() {
			return mContent;
		}

		public int getErrorCode(){
			return mCode;
		}
		
		@Override
		public String toString() {
			return String.valueOf(this.mContent);
		}

		/**
		 * @param errorCode
		 */
		public static SYSTEM_ERROR valueOf(int errorCode) {
			//获取所有枚举
			SYSTEM_ERROR[] arr = SYSTEM_ERROR.values();
			if(arr == null || arr.length == 0){
				return null;
			}
			//遍历获取数据
			for(SYSTEM_ERROR err : arr){
				if(err.mCode == errorCode){
					return err;
				}
			}
			return null;
		}
	}
}
