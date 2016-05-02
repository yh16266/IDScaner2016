package com.haozi.idscaner2016.client.control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.common.app.MyApp;
import com.haozi.idscaner2016.common.utils.DateUtil;
import com.haozi.idscaner2016.common.utils.StringUtil;
import com.haozi.idscaner2016.constants.IConstants;

import java.io.File;

public class DXSignPop extends PopupWindow implements View.OnClickListener{
	
	/**上下文引用*/
	private Activity mContext;
	/**根布局*/
	private LinearLayout mRootLayout;
	/**实例化工具*/
	private LayoutInflater mInflater;
	/**点击监听类*/
	private View.OnClickListener mlistener;
	//签名区域框框
	private SignatureView mSignature;
	private Button mButtonCleanSignature;
	private Button mButtonSaveSignature;
	private Button mButtonCancelSignature;

	private String mIDNum;
	private String mUsername;

	@SuppressLint("InflateParams")
	public DXSignPop(Activity context) {
		super(context);
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mRootLayout = (LinearLayout) mInflater.inflate(R.layout.dx_sign_pop, null);
		setContentView(mRootLayout);
		setBackgroundDrawable(MyApp.getInstance().getResources().getDrawable(R.color.white));
		initPopMenu();
	}

	private void initView(){
		mSignature = (SignatureView) mRootLayout.findViewById(R.id.signatureView);

		mButtonCleanSignature = (Button)mRootLayout.findViewById(R.id.buttonClean_signature);
		mButtonSaveSignature = (Button)mRootLayout.findViewById(R.id.buttonSave_signature);
		mButtonCancelSignature = (Button)mRootLayout.findViewById(R.id.buttonCancel_signature);

		mButtonCleanSignature.setOnClickListener(this);
		mButtonSaveSignature.setOnClickListener(this);
		mButtonCancelSignature.setOnClickListener(this);
	}

	/**
	 * 初始化页面
	 * */
	private void initPopMenu(){
		setFocusable(true);
		setOutsideTouchable(true);
		setWidth(800);
		setHeight(500);
		initView();
	}
	
	@Override
	public void showAsDropDown(View anchor) {
		super.showAsDropDown(anchor);
	}
	
	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff) {
		super.showAsDropDown(anchor, xoff, yoff);
	}
	
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
	}

	public void showPop(View parent,String username,String idnum){
		mUsername = username;
		mIDNum = idnum;
		if(StringUtil.isEmpty(idnum)){
			DXToast.show("请先扫描身份证，然后再签字登记！");
			return;
		}
		showAtLocation(parent, Gravity.CENTER,0,0);
	}

	/**
	 * 响应click事件
	 * */
	@Override
	public void onClick(View v) {
		if(v == mButtonCancelSignature){
			dismiss();
		}else if(v == mButtonCleanSignature) {
			mSignature.clear();
		}else if(v == mButtonSaveSignature){
			saveSignImage();
		}else if(mlistener != null){
			mlistener.onClick(v);
		}
	}

	/**
	 * 设置Item点击监听
	 * */
	public void setItemOnclickListener(View.OnClickListener listener){
		mlistener = listener;
	}

	private void saveSignImage(){
		//mSignature.savePng("/mnt/sdcard/signaturedemo.png");
		String ymdStr = DateUtil.convertDateToYMDShort(System.currentTimeMillis());
		File file = new File(IConstants.PROJECT_DIR + File.separator + ymdStr);
		if(!file.exists()) {
			file.mkdirs();
		}
		String HHmmssStr = DateUtil.convertDateHHmmss(System.currentTimeMillis());
		String signFilePath = IConstants.PROJECT_DIR + File.separator + ymdStr + File.separator + mUsername+"_"+mIDNum+"_"+HHmmssStr+"_sign.png";
		Bitmap signBitmap = mSignature.savePng(signFilePath);
		mSignature.clear();
		dismiss();
		if(callback != null){
			callback.callback(signFilePath,signBitmap);
		}
	}

	private SignCacllBack callback;

	public void setSignCallback(SignCacllBack callback){
		this.callback = callback;
	}

	public interface SignCacllBack{
		public void callback(String filePath, Bitmap signBitmap);
	}
}
