package com.haozi.idscaner2016.common.base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.control.DXProgressDialog;
import com.haozi.idscaner2016.common.cache.ICache;
import com.haozi.idscaner2016.common.net.NetState;
import com.haozi.idscaner2016.common.utils.DXUtil;
import com.haozi.idscaner2016.common.utils.StringUtil;
import com.haozi.idscaner2016.constants.IActionIntent;

/**
 * Created by Haozi on 2016/4/24.
 */
public abstract class BaseCompatActivity extends AppCompatActivity implements Handler.Callback, View.OnClickListener {
    /**类标签 */
    protected String TAG = getClass().getSimpleName();
    /**主处理UI器 */
    public Handler mMainHandler;
    /**UI构造器*/
    protected LayoutInflater inflater;
    /**通用广播接收器*/
    private BroadcastReceiver br;
    /**进度条 */
    private ProgressDialog progressDialog;

    protected Toolbar mToolbar;
    protected boolean mIsToolbarInit = false;

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

    public Handler getMainHandler(){
        return mMainHandler;
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
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView();
        initToolbar(null,-1,null);
    }

    protected void initToolbar(String title){
        initToolbar(title,-1,null);
    }

    protected void initToolbar(String title,Toolbar.OnMenuItemClickListener listener){
        initToolbar(title,-1,listener);
    }

    protected void initToolbar(String title,int menueRes,Toolbar.OnMenuItemClickListener listener){
        if(mToolbar == null){
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
        if(!StringUtil.isEmpty(title)){
            setTitle(title);
        }
        if(listener != null){
            mToolbar.setOnMenuItemClickListener(listener);
        }
        if(menueRes > 0){
            mToolbar.inflateMenu(menueRes);
        }
        if(mToolbar != null && mIsToolbarInit == false){
            // 标题的文字需在setSupportActionBar之前，不然会无效
            setSupportActionBar(mToolbar);
            mIsToolbarInit = true;
        }
    }

    protected void setTitle(String title){
        if(mToolbar != null){
            mToolbar.setTitle(title);
        }
    }

    protected abstract void initView();

    /**
     * 控件单击响应
     * */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.title_left_button:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭进度条
        dismissProgress();
        //注销广播
        unregisterReceiver();
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
