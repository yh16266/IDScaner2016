package com.haozi.idscaner2016.client.control;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.common.app.MyApp;
import com.haozi.idscaner2016.common.utils.StringUtil;

/**
 * 类名：DXToast
 * @author yinhao
 * @功能 公用的进度等待页面
 * @创建日期 2015年10月8日 上午9:55:43
 * @备注 [修改者，修改日期，修改内容]
 */
public class DXToast {

	/**
	 * 显示toast
	 * @param str 显示文本
	 */
	@SuppressLint("InflateParams")
	public static Toast show(String str) {
		if (!StringUtil.isEmpty(str)) {
			Toast tst = new Toast(MyApp.getInstance());
			tst.setDuration(Toast.LENGTH_SHORT);
			tst.setGravity(Gravity.CENTER, 0, 0);
			ViewGroup vgp = (ViewGroup) LayoutInflater.from(MyApp.getInstance()).inflate(R.layout.dx_toast, null);
			TextView tvw = (TextView) vgp.findViewById(R.id.my_toast);
			tvw.setText(str);
			tst.setView(vgp);
			tst.show();
			return tst;
		}
		return null;
	}

	/**
	 * 显示toast
	 * @param str 显示文本
	 */
	@SuppressLint("InflateParams")
	public static Toast showlong(String str) {
		if (!StringUtil.isEmpty(str)) {
			Toast tst = new Toast(MyApp.getInstance());
			tst.setDuration(Toast.LENGTH_LONG);
			tst.setGravity(Gravity.CENTER, 0, 0);

			ViewGroup vgp = (ViewGroup) LayoutInflater.from(
					MyApp.getInstance()).inflate(R.layout.dx_toast, null);
			TextView tvw = (TextView) vgp.findViewById(R.id.my_toast);
			tvw.setText(str);
			tst.setView(vgp);
			tst.show();
			return tst;
		}
		return null;
	}

	/**
	 * 显示toast
	 * @param strID 显示文本
	 */
	public static Toast showlong(int strID) {
		return showlong(MyApp.getInstance().getString(strID));
	}
	
	/**
	 * 显示toast
	 * @param strID 显示文本
	 */
	public static Toast show(int strID) {
		return show(MyApp.getInstance().getString(strID));
	}
}
