package com.haozi.idscaner2016.client.control;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.common.utils.StringUtil;

/**
 * 类名：DXProgressDialog
 * @author yinhao
 * @功能 公用的进度等待页面
 * @创建日期 2015年10月8日 上午9:55:43
 * @备注 [修改者，修改日期，修改内容]
 */
public class DXProgressDialog extends ProgressDialog {

	private TextView tvwMsg;
	private String msg;
	
	public DXProgressDialog(Context context) {
		super(context, R.style.dx_dialog);
	}
	
	public DXProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public DXProgressDialog(Context context, String msg) {
		this(context);
		this.msg = msg;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dx_progressdialog);
		setCanceledOnTouchOutside(false);
		setCancelable(true);
		tvwMsg = (TextView) findViewById(R.id.tvw_pbar);
		if(StringUtil.isEmpty(msg)==false){
			setMessage(msg);
		}
	}
	
	/**
	 * 设置显示内容
	 * @param msg
	 * */
	public void setMessage(String msg){
		this.msg = msg;
		if(tvwMsg != null) {
			tvwMsg.setText(msg);
		}
	}

	/**
	 * 设置显示内容
	 * @param msg
	 * */
	public void setMessage(CharSequence msg){
		this.msg = msg.toString();
		if(tvwMsg != null) {
			tvwMsg.setText(msg);
		}
		super.setMessage(msg);
	}
	
	/**
	 * 显示
	 * */
	public static DXProgressDialog show(Context context,String msg) {
		DXProgressDialog progress = new DXProgressDialog(context,msg);
		progress.show();
		return progress;
	}
	
	/**
	 * 显示
	 * */
	@Override
	public void show() {
		super.show();
	}
	
	/**
	 * 隐藏
	 * */
	@Override
	public void dismiss() {
		super.dismiss();
	}

}
