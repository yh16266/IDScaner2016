package com.haozi.idscaner2016.common.base;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.control.DXProgressDialog;
import com.haozi.idscaner2016.common.cache.ICache;
import com.haozi.idscaner2016.common.net.NetState;
import com.haozi.idscaner2016.common.utils.DXUtil;
import com.haozi.idscaner2016.constants.IActionIntent;

public class BaseActivity extends FragmentActivity implements Handler.Callback, OnClickListener{
	
	/**类标签 */
	protected String TAG = getClass().getSimpleName();
	/**主处理UI器 */
	public Handler mMainHandler;
	/**主布局引用 */
	private View vwLayout;
	/**左边按钮 */
	private Button vwLeftButton;
	/**左边图片按钮*/
	private ImageButton vwleftImageButton;
	/**右边图片按钮*/
	private ImageButton vwRightImageButton;
	/**右边按钮*/
	private Button vwRightButton;
	/** 标题文字*/
	private TextView vwTitleText;
	/**警告View*/
	private View vwWaringLayout;
	/**是否显示警告：默认为显示*/
	private boolean isShowWaring = true;
	/**显示警告*/
	private static final int HEADER_WARING_SHOW = 6001;
	/**隐藏警告*/
	private static final int HEADER_WARING_HIDE = 6002;
	/**构造器*/
	protected LayoutInflater inflater;
	/**网络广播接收器*/
	private BroadcastReceiver brNetCheck = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//显示警告（网络断开）
			if((IActionIntent.ACTION_HEADER_WARING_SHOW.equals(intent.getAction()) ||
				IActionIntent.ACTION_NET_DISCONNECTION_WARING.equals(intent.getAction()))) {
				showWarning();
				if(mMainHandler != null && isShowWaring == true){
					mMainHandler.sendEmptyMessage(HEADER_WARING_SHOW);
				}
			//隐藏
			} else if(IActionIntent.ACTION_HEADER_WARING_HIDE.equals(intent.getAction()) ||
					IActionIntent.ACTION_NET_CONNECTION_WARING.equals(intent.getAction())){
				hideWarning();
				if(mMainHandler != null && isShowWaring == true){
					mMainHandler.sendEmptyMessage(HEADER_WARING_HIDE);
				}
			}
		}
	};
	/**通用广播接收器*/
	/**广播接收器*/
	private BroadcastReceiver br;
	/**进度条 */
	private ProgressDialog progressDialog;

	/**
	 * 广播接收处理
	 * */
	protected void brReceive(Context context, Intent intent) {
		
	}
	
	/**
	 * 注册广播接收器
	 * */
	protected void registerReceiver(IntentFilter itFilter) {
		br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				brReceive(context, intent);
			}
		};
		registerReceiver(br, itFilter);
	}
	
	/**
	 * 取消广播注册
	 * */
	protected void unregisterReceiver() {
		if(br != null) {
			unregisterReceiver(br);
			br = null;
		}
	}
	
	/**
	 * 注册UI处理器
	 * */
	protected void registerHandler() {
		if(mMainHandler == null){
			mMainHandler = new Handler(this);
		}
	}
	
	/**
	 * 处理UIMsg请求
	 * */
	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

	/**
	 * 发送空内容广播
	 * */
	protected void sendEmtpyMessage(int what){
		if(mMainHandler != null){
			mMainHandler.sendEmptyMessage(what);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = LayoutInflater.from(this);
		ICache.alllActivityList.add(this);
		//通过设置是否显示网络断开警告，注册相关UI通知广播接收器
		if(isShowWaring){
			IntentFilter itFilter = new IntentFilter();
			itFilter.addAction(IActionIntent.ACTION_NET_DISCONNECTION_WARING);
			itFilter.addAction(IActionIntent.ACTION_NET_CONNECTION_WARING);
			itFilter.addAction(IActionIntent.ACTION_HEADER_WARING_SHOW);
			itFilter.addAction(IActionIntent.ACTION_HEADER_WARING_HIDE);
			registerReceiver(brNetCheck, itFilter);
		}
		//判断网络是否正常，不正常则发送网络断开广播
		if(NetState.getInstance().isWifiOrMobile() == false){
			DXUtil.sendBroadcast(new Intent(IActionIntent.ACTION_NET_DISCONNECTION_WARING));
		}
	}
	
	/**
	 * 得到左边按钮
	 * @return
	 */
	public Button getTitleLeftButton() {
		return vwLeftButton;
	}
	
	/**
	 * 得到左边布局
	 * @return
	 */
	public ImageButton getTitleLeftImageButton() {
		return vwleftImageButton;
	}
	
	/**
	 * 得到右边布局
	 * @return
	 */
	public ImageButton getTitleRightImageButton() {
		return vwRightImageButton;
	}
	
	/**
	 * 得到右边按钮
	 * @return
	 */
	public Button getTitleRightButton() {
		return vwRightButton;
	}
	
	/**
	 * 得到Title
	 * @return
	 */
	public TextView getTitleText() {
		return vwTitleText;
	}
	
	/**
	 * 得到Title布局
	 * @return
	 */
	public View getTitleLayout() {
		return vwLayout;
	}
	
	@Override
	public void setContentView(int layoutResID) {
		
		super.setContentView(layoutResID);

		vwLayout = findViewById(R.id.title_bar_layout);
		
		View vwLeftBtn = findViewById(R.id.title_left_button);
		if(vwLeftBtn != null){
			vwLeftButton = (Button) vwLeftBtn;
		}
		
		View vwLimg = findViewById(R.id.title_left_img_button);
		if(vwLimg != null) {
			vwleftImageButton = (ImageButton) vwLimg;
		}
		
		View vwRimg = findViewById(R.id.title_right_img_button);
		if(vwRimg != null) {
			vwRightImageButton = (ImageButton) vwRimg;
		}
		
		View vwRightBtn = findViewById(R.id.title_right_button);
		if(vwRightBtn != null) {
			vwRightButton = (Button) vwRightBtn;
		}
		
		vwTitleText = (TextView) findViewById(R.id.txv_titlebar_title);
		
//		vwWaringLayout = findViewById(R.id.rlyout_head_warning);
		
		if(vwWaringLayout != null) {
			vwWaringLayout.setOnClickListener(this);
		}
		
		if(vwleftImageButton != null) {
			vwleftImageButton.setOnClickListener(this);
		}
		
		if(vwLeftButton != null) {
			vwLeftButton.setOnClickListener(this);
		}
		
		if(vwRightImageButton != null) {
			vwRightImageButton.setOnClickListener(this);
		}
		
		if(vwRightButton != null) {
			vwRightButton.setOnClickListener(this);
		}
		
		if(vwTitleText != null) {
			vwTitleText.setOnClickListener(this);
		}
	}
	
	/**
	 * 控件单击响应
	 * */
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_left_button:
		case R.id.title_left_img_button:
			titleLeftBtnPressed();
			break;
		case R.id.title_right_button:
		case R.id.title_right_img_button:
			titleRightBtnPressed();
			break;
		case R.id.txv_titlebar_title:
			titlePressed();
			break;
//		case R.id.rlyout_head_warning:
//			Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//			startActivityForResult(intent, 1);
//			break;
		}
	}
	
	/**
	 * 打开软件键盘
	 * */
	protected void openKeyboard() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 500);
	}
	
    /**
     * 关闭软键盘
     * */
	protected void closeKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}

	/**
	 * 设置左边按钮文字显示
	 * @param str
	 */
	public void setTitleLeftBtnText(String str) {
		if(vwLeftButton != null) {
			vwLeftButton.setText(str);
		}
	}

	/**
	 * 设置左边按钮文字显示
	 * @param textID
	 */
	public void setTitleLeftBtnText(int textID) {
		if(vwLeftButton != null) {
			vwLeftButton.setText(textID);
		}
	}

	/**
	 * 设置左边按钮图片
	 * @param resID
	 */
	public void setTitleLeftBtnResource(int resID) {
		if(vwleftImageButton != null) {
			vwleftImageButton.setImageResource(resID);
		}
	}
	
	/**
	 * 显示,隐藏左边按钮
	 * @param visibility
	 */
	public void setTitleLeftBtnShow(int visibility) {
		//隐藏图片按钮
		if(visibility == View.VISIBLE){
			setTitleLeftImgBtnShow(View.GONE);
		}
		//显示文字按钮
		if(vwLeftButton != null) {
			vwLeftButton.setVisibility(visibility);
		}
	}

	/**
	 * 显示,隐藏左边按钮
	 * @param visibility
	 */
	public void setTitleLeftImgBtnShow(int visibility) {
		//隐藏文字按钮
		if(visibility == View.VISIBLE){
			setTitleLeftBtnShow(View.GONE);
		}
		//显示图片按钮
		if(vwleftImageButton != null){
			vwleftImageButton.setVisibility(visibility);
		}
	}

	/**
	 * 设置右边按钮文字显示
	 * @param str
	 */
	public void setTitleRightBtnText(String str) {
		if(vwRightButton != null) {
			vwRightButton.setText(str);
		}
	}

	/**
	 * 设置右边按钮文字显示
	 * @param textID
	 */
	public void setTitleRightBtnText(int textID) {
		if(vwRightButton != null) {
			vwRightButton.setText(textID);
		}
	}
	
	/**
	 * 设置右边按钮图片
	 * @param resID
	 */
	public void setTitleRightBtnResource(int resID) {
		if(vwRightImageButton != null) {
			vwRightImageButton.setImageResource(resID);
		}
	}

	/**
	 * 显示,隐藏右边按钮
	 * @param visibility
	 */
	public void setTitleRightImgBtnShow(int visibility) {
		//隐藏文字按钮
		if(visibility == View.VISIBLE){
			setTitleRightBtnShow(View.GONE);
		}
		//显示图片按钮
		if(vwRightImageButton != null) {
			vwRightImageButton.setVisibility(visibility);
		}
	}
	
	/**
	 * 显示,隐藏右边按钮
	 * @param visibility
	 */
	public void setTitleRightBtnShow(int visibility) {
		//隐藏文字按钮
		if(visibility == View.VISIBLE){
			setTitleRightImgBtnShow(View.GONE);
		}
		//显示图片按钮
		if(vwRightButton != null) {
			vwRightButton.setVisibility(visibility);
		}
	}
	
	/**
	 * 点击左边按钮
	 */
	protected void titleLeftBtnPressed(){
		finish();
	}
	
	/**
	 * 点击右边按钮
	 */
	protected void titleRightBtnPressed(){
		
	}
	
	/**
	 * 设置是否显示警告
	 * */
	protected void setWarningShowable(boolean isShowWaring){
		this.isShowWaring = isShowWaring;
		registerHandler();
	}
	
	/**
	 * 显示警告
	 * */
	protected void showWarning(){
		if(vwWaringLayout != null){
			if(vwWaringLayout.getVisibility() != View.VISIBLE){
				vwWaringLayout.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 隐藏警告
	 * */
	protected void hideWarning(){
		if(vwWaringLayout != null){
			if(vwWaringLayout.getVisibility() != View.GONE){
				vwWaringLayout.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 显示警告
	 * @param str 警告内容
	 * */
	protected void showWarning(String str){
		if(vwWaringLayout != null){
//			TextView tv = (TextView) vwWaringLayout.findViewById(R.id.txv_headerwaring);
//			if(tv == null){
//				return;
//			}
//			tv.setText(str);
			vwWaringLayout.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 标题点击响应
	 * */
	protected void titlePressed() {
		
	}
	
	/**
	 * 设置标题背景色
	 * */
	public void setTitleBackgroundColor(int color) {
		if(vwLayout != null) {
			vwLayout.setBackgroundColor(color);
		}
	}
	
	/**
	 * 设置标题背景
	 * */
	public void setTitleBackgroundRes(int resID) {
		if(vwLayout != null) {
			vwLayout.setBackgroundResource(resID);
		}
	}

	/**
	 * 设置标题文字内容
	 * */
	@Override
	public void setTitle(CharSequence title) {
		if(vwTitleText != null){
			vwTitleText.setText(title);
		}
	}

	/**
	 * 设置标题文字内容
	 * */
	@Override
	public void setTitle(int textID) {
		if(vwTitleText != null){
			vwTitleText.setText(textID);
		}
	}
	
	/**
	 * 设置标题是否可见
	 * */
	public void setTitleVisible(int visibility) {
		if(vwTitleText != null){
			vwTitleText.setVisibility(visibility);
		}
	}
	
	/**
	 * 设置标题图标
	 * */
	public void setTitleIcon(Drawable left, Drawable top, Drawable right, Drawable bottom) {
		if(vwTitleText != null){
			vwTitleText.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//关闭进度条
		dismissProgress();
		//注销广播
		unregisterReceiver();
		//注销网络监听广播
		unregisterReceiver(brNetCheck);
		//移除缓存中的当前activity
		ICache.alllActivityList.remove(this);
	}
	
	/**
	 * 打开进度条
	 * */
	public void showProgress(){
		showProgress(R.string.progress_text);
	}
	
	/**
	 * 打开进度条
	 * */
	public void showProgress(int messageRes){
		String message = getResources().getString(messageRes);
		showProgress(message);
	}

	/**
	 * 打开进度条
	 * */
	public void showProgress(String message){
		if(progressDialog == null){
			progressDialog = DXProgressDialog.show(this, message);
		}else{
			progressDialog.setMessage(message);
			progressDialog.show();
		}
	}
	
	/**
	 * 关闭进度条
	 * */
	public void dismissProgress(){
		if(progressDialog != null && progressDialog.isShowing() == true){
			progressDialog.dismiss();
		}
	}
}
