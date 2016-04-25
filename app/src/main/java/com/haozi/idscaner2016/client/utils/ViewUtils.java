/**
 * 
 */
package com.haozi.idscaner2016.client.utils;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.haozi.idscaner2016.common.utils.StringUtil;

import java.math.BigDecimal;

/**
 * 类名：ViewUtils
 * @author yinhao
 * @功能
 * @创建日期 2015年11月9日 下午1:32:49
 * @备注 [修改者，修改日期，修改内容]
 */
public class ViewUtils {

	/**
	 * 获取EditText的值
	 * */
	public static String getEditString(EditText edt){
		if(edt == null || edt.getText() == null){
			return null;
		}
		return edt.getText().toString();
	}

	/**
	 * 获取EditText的值
	 * */
	public static String getEditString(Activity activity,int edtId){
		View view = activity.findViewById(edtId);
		if(view != null && view instanceof EditText){
			return getEditString((EditText)view);
		}
		return null;
	}
	
	/**
	 * 获取EditText的值
	 * */
	public static BigDecimal getEditBigdecimal(Activity activity,int edtId){
		View view = activity.findViewById(edtId);
		if(view != null && view instanceof EditText){
			return getEditBigdecimal((EditText)view);
		}
		return null;
	}
	
	/**
	 * 获取EditText的值
	 * */
	public static BigDecimal getEditBigdecimal(EditText edt){
		if(edt == null || edt.getText() == null){
			return null;
		}
		String str = edt.getText().toString();
		if(StringUtil.isUpFloat(str) == false){
			return null;
		}
		return new BigDecimal(str);
	}
	
	/**
	 * 获取TextView的值
	 * */
	public static String getTextViewString(Activity activity,int txvId){
		View view = activity.findViewById(txvId);
		if(view != null && view instanceof TextView){
			return getTextViewString((TextView)view);
		}
		return null;
	}
	
	/**
	 * 获取TextView的值
	 * */
	public static String getTextViewString(TextView txv){
		if(txv == null || txv.getText() == null){
			return null;
		}
		return txv.getText().toString();
	}

	/**
	 * 获取TextView的Tag值
	 * */
	public static long getViewTagLong(Activity activity,int viewId){
		View view = activity.findViewById(viewId);
		if(view != null){
			return getViewTagLong(view);
		}
		return 0;
	}
	
	/**
	 * 获取TextView的Tag值
	 * */
	public static long getViewTagLong(View txv){
		if(txv == null || txv.getTag() == null){
			return 0;
		}
		String value = txv.getTag().toString();
		if(StringUtil.isNumeric(value)){
			return Long.valueOf(txv.getTag().toString());
		}
		return 0;
	}

	/**
	 * 获取TextView的Tag值
	 * */
	public static int getViewTagInt(View txv){
		if(txv == null || txv.getTag() == null){
			return 0;
		}
		String value = txv.getTag().toString();
		if(StringUtil.isNumeric(value)){
			return Integer.valueOf(txv.getTag().toString());
		}
		return 0;
	}

	/**
	 * 获取TextView的Tag值
	 * */
	public static String getViewTag(View view){
		if(view == null || view.getTag() == null){
			return "";
		}
		return view.getTag().toString();
	}
	
	/**
	 * 获取EditText的值
	 * */
	public static int getEditInt(View parent,int edtId){
		View view = parent.findViewById(edtId);
		if(view != null && view instanceof EditText){
			return getIntegerValue((EditText)view);
		}
		return 0;
	}
	
	/**
	 * 获取EditText的值
	 * */
	public static int getEditInt(Activity activity,int edtId){
		View view = activity.findViewById(edtId);
		if(view != null && view instanceof EditText){
			return getIntegerValue((EditText)view);
		}
		return 0;
	}
	
	/**
	 * 获取editext的int值
	 * @param edt
	 * @return double
	 * */
	public static int getIntegerValue(EditText edt){
		if(edt == null || edt.getText() == null){
			return 0;
		}
		if(StringUtil.isEmpty(edt.getText().toString())){
			return 0;
		}
		return Integer.valueOf(edt.getText().toString());
	}
	
	/**
	 * 获取editext的double值
	 * @param edt
	 * @return double
	 * */
	public static double getDoubleValue(EditText edt){
		if(edt == null || edt.getText() == null){
			return 0;
		}
		if(StringUtil.isEmpty(edt.getText().toString())){
			return 0;
		}
		return Double.valueOf(edt.getText().toString());
	}

	/**
	 * 获取EditText的值
	 * */
	public static float getFloatValue(Activity activity,int edtId){
		View view = activity.findViewById(edtId);
		if(view != null && view instanceof EditText){
			return getFloatValue((EditText)view);
		}
		return 0;
	}
	
	/**
	 * 获取editext的double值
	 * @param edt
	 * @return double
	 * */
	public static float getFloatValue(EditText edt){
		if(edt == null || edt.getText() == null){
			return 0;
		}
		if(StringUtil.isEmpty(edt.getText().toString())){
			return 0;
		}
		return Float.valueOf(edt.getText().toString());
	}

	/**
	 * 获取editext的double值
	 * @param edt
	 * @return double
	 * */
	public static String getStringValue(EditText edt){
		if(edt == null || edt.getText() == null){
			return "";
		}
		if(StringUtil.isEmpty(edt.getText().toString())){
			return "";
		}
		return edt.getText().toString();
	}

	/**
	 * 获取bigdecimal值用于显示
	 * */
	public static String getValueFromBigdecimal(BigDecimal num,String unit){
		String value = "不详";
		if(num != null){
			value = num.toString()+unit;
		}
		return value;
	}
	
	/**
	 * 设置Texview值
	 * */
	public static void setTextViewTxt(Activity activity,int txvId,String text){
		View view = activity.findViewById(txvId);
		if(view != null && view instanceof TextView){
			((TextView)view).setText(text);
		}
	}
	
	/**
	 * 设置Texview值(追加内容)
	 * */
	public static void addTextViewTxt(Activity activity,int txvId,String textadd){
		View view = activity.findViewById(txvId);
		if(view != null && view instanceof TextView){
			String txt = "";
			if(((TextView)view).getText() != null){
				txt = ((TextView)view).getText().toString();
			}
			txt = txt+textadd;
			((TextView)view).setText(txt);
		}
	}
	
	/**
	 * 设置Texview值
	 * */
	public static void setTextViewTxt(View parent,int txvId,String text){
		View view = parent.findViewById(txvId);
		if(view != null && view instanceof TextView){
			((TextView)view).setText(text);
		}
	}

	/**
	 * 设置EditText值
	 * */
	public static void setEditTextTxt(Activity activity,int txvId,String text){
		View view = activity.findViewById(txvId);
		if(view != null && view instanceof TextView){
			((EditText)view).setText(text);
		}
	}
	
	/**
	 * 设置EditText值
	 * */
	public static void setEditTextTxt(View parent,int txvId,String text){
		View view = parent.findViewById(txvId);
		if(view != null && view instanceof TextView){
			((EditText)view).setText(text);
		}
	}

	/**
	 * 获取TextView的Tag值
	 * */
	public static boolean setViewTag(Activity activity,int viewId,Object data){
		View view = activity.findViewById(viewId);
		if(view != null){
			view.setTag(data);
			return true;
		}
		return false;
	}

	/**
	 * 获取TextView的Tag值
	 * */
	public static boolean setViewTag(Activity activity,int viewId,int tagindex,Object data){
		View view = activity.findViewById(viewId);
		if(view != null){
			view.setTag(tagindex,data);
			return true;
		}
		return false;
	}
}
