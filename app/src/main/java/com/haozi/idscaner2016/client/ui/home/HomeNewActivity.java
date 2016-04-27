package com.haozi.idscaner2016.client.ui.home;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.bean.client.BCardInfo;
import com.haozi.idscaner2016.client.biz.cardread.ClientBCReceiver;
import com.haozi.idscaner2016.client.biz.cardread.MainMsg;
import com.haozi.idscaner2016.client.biz.cardread.ReadCardServiceCallback;
import com.haozi.idscaner2016.client.biz.cardread.ReadCardSound;
import com.haozi.idscaner2016.client.biz.cardread.ReadInfoCallback;
import com.haozi.idscaner2016.client.biz.cardread.ReadServiceConnection;
import com.haozi.idscaner2016.client.biz.home.HomeCardReadHelper;
import com.haozi.idscaner2016.client.control.DXSignPop;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.client.utils.ViewUtils;
import com.haozi.idscaner2016.common.base.BaseCompatActivity;
import com.haozi.idscaner2016.common.utils.DateUtil;
import com.haozi.idscaner2016.constants.IActionIntent;
import com.routon.idr.idrinterface.readcard.IReadCardService;
import com.routon.idr.idrinterface.readcard.ReadMode;
import com.routon.idr.idrinterface.readcard.ReadState;
import com.routon.idr.idrinterface.readcard.ReadType;
import com.routon.idr.idrinterface.readcard.ReaderBean;
import com.routon.idrconst.Action;

public class HomeNewActivity extends BaseCompatActivity implements ReadInfoCallback {

    private TextView txv_statu;
    private TextView txv_sign;
    private ImageView img_sign;
    private Button mButtonStart;
    private Button mButtonPause;
    private Button mButtonStop;
    private Spinner mSpinnerMode;
    private Spinner mSpinnerType;
    private Spinner spinner_reson;
    private ArrayAdapter spinnerModeAdapter;
    private ArrayAdapter spinnerTypeAdapter;
    private ArrayAdapter spinnerResonAdapter;
    private ImageView img_headicon;
    private EditText editText_reson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
    }

    @Override
    protected void initView() {
        initToolbar("首页New");
        txv_statu = (TextView) findViewById(R.id.txv_statu);
        txv_sign = (TextView) findViewById(R.id.txv_sign);
        txv_sign.setOnClickListener(this);
        img_sign = (ImageView) findViewById(R.id.img_sign);
        mButtonStart = (Button) findViewById(R.id.btn_start);
        mButtonPause = (Button) findViewById(R.id.btn_pause);
        mButtonStop = (Button) findViewById(R.id.btn_stop);
        editText_reson = (EditText) findViewById(R.id.editText_reson);

        mButtonPause.setOnClickListener(this);
        mButtonStart.setOnClickListener(this);
        mButtonStop.setOnClickListener(this);
        img_headicon = (ImageView) findViewById(R.id.img_headicon);

        mSpinnerMode=(Spinner)findViewById(R.id.spinnermode);
        spinnerModeAdapter=ArrayAdapter.createFromResource(this, R.array.read_mode, android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMode.setAdapter(spinnerModeAdapter);
        mSpinnerMode.setOnItemSelectedListener(new MyOnItemSelectedListener());

        mSpinnerType=(Spinner)findViewById(R.id.spinnertype);
        spinnerTypeAdapter= ArrayAdapter.createFromResource(this, R.array.read_type, android.R.layout.simple_spinner_dropdown_item);
        mSpinnerType.setAdapter(spinnerTypeAdapter);
        mSpinnerType.setOnItemSelectedListener(new MyOnItemSelectedListener());

        spinner_reson=(Spinner)findViewById(R.id.spinner_reson);
        spinnerResonAdapter= ArrayAdapter.createFromResource(this, R.array.visite_reson, android.R.layout.simple_spinner_dropdown_item);
        spinner_reson.setAdapter(spinnerResonAdapter);
        spinner_reson.setOnItemSelectedListener(new MyOnItemSelectedListener());

        initCardRead();
    }

    private void initCardRead(){
        registerHandler();
        HomeCardReadHelper.getInstance().init(this,txv_statu,mButtonPause,mButtonStop,mButtonStart,mSpinnerMode,mSpinnerType);
        txv_statu.setText(HomeCardReadHelper.getInstance().getTextStatus());
    }

    public Handler getMainHandler(){
        return mMainHandler;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.txv_sign:
//                BCardInfo mBCardInfo = HomeCardReadHelper.getInstance().getBCardInfo();
//                if(mBCardInfo == null){
//                    DXToast.show("请先扫描身份证，然后再签字登记！");
//                    return;
//                }
//                DXSignPop signPop = new DXSignPop(this);
//                signPop.showPop(v,mBCardInfo.name);
                Intent intent = new Intent(HomeNewActivity.this,SignActivity.class);
                intent.putExtra(IActionIntent.INTENTEXTRA_IDNUM,"510622");
                startActivity(intent);
                break;
            default:
                HomeCardReadHelper.getInstance().startReading(v);
                break;
        }

    }

    @Override
    public boolean handleMessage(Message msg) {
        boolean isProcessed = HomeCardReadHelper.getInstance().procOnAny(msg);
        if(!isProcessed){
            switch(HomeCardReadHelper.getInstance().getClientState()){
                case st_init:
                    HomeCardReadHelper.getInstance().procOnInit(msg);
                    break;
                case st_idle:
                    HomeCardReadHelper.getInstance().procOnIdle(msg,this);
                    break;
                case st_work:
                    HomeCardReadHelper.getInstance().procOnWork(msg,this);
                    break;
                case st_opened:
                    HomeCardReadHelper.getInstance().procOnOpened(msg,this);
                    break;
                case st_fault:
                    //lihuili 2016-01-14 st_fault是多余, 程序不会调用procOnFault
                    HomeCardReadHelper.getInstance().procOnFault(msg,this);
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HomeCardReadHelper.getInstance().unRegister(this);
        HomeCardReadHelper.getInstance().unbindReadCardService(this);
        Log.d(TAG,"onDestroy");
    }

    @Override
    protected void onPause() {
        Log.d(TAG,"onPause");
        super.onPause();
        Message send_msg = new Message();
        send_msg.what = MainMsg.EVT_UI_PAUSED;
        if(mMainHandler!=null){
            mMainHandler.sendMessage(send_msg);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        Message send_msg = new Message();
        send_msg.what = MainMsg.EVT_UI_RESUMED;
        if(mMainHandler!=null){
            mMainHandler.sendMessage(send_msg);
        }
    }

    public void updateTextCardInfo(boolean flag,ReadType rdType){
        if(flag){
            if(rdType==ReadType.B){
                BCardInfo mBCardInfo = HomeCardReadHelper.getInstance().getBCardInfo();
                ViewUtils.setTextViewTxt(this,R.id.tv_name,mBCardInfo.name);
                ViewUtils.setTextViewTxt(this,R.id.tv_sex,mBCardInfo.gender);
                ViewUtils.setTextViewTxt(this,R.id.tv_nation,mBCardInfo.nation);
                String birthday = mBCardInfo.birthday.substring(0, 4)+"年"
                        +mBCardInfo.birthday.substring(4, 6)+"月"
                        +mBCardInfo.birthday.substring(6, 8)+"日";
                ViewUtils.setTextViewTxt(this,R.id.tv_birthday,birthday);
                ViewUtils.setTextViewTxt(this,R.id.tv_address,mBCardInfo.address);
                ViewUtils.setTextViewTxt(this,R.id.tv_idnumber,mBCardInfo.id);
                //mTextViewAgency.setText(mBCardInfo.agency);//发证机关
                //mTextViewExpire.setText(mBCardInfo.expireStart + " - " + mBCardInfo.expireEnd);//有效日期
                img_headicon.setImageBitmap(mBCardInfo.photo);
            }else{
                String cardANo = HomeCardReadHelper.getInstance().getCardANo();
                ViewUtils.setTextViewTxt(this,R.id.tv_name,"");
                ViewUtils.setTextViewTxt(this,R.id.tv_sex,"");
                ViewUtils.setTextViewTxt(this,R.id.tv_nation,"");
                ViewUtils.setTextViewTxt(this,R.id.tv_birthday,"");
                ViewUtils.setTextViewTxt(this,R.id.tv_address,"");
                ViewUtils.setTextViewTxt(this,R.id.tv_idnumber,cardANo);
                //mTextViewAgency.setText(mBCardInfo.agency);//发证机关
                //mTextViewExpire.setText(mBCardInfo.expireStart + " - " + mBCardInfo.expireEnd);//有效日期
                img_headicon.setImageResource(R.mipmap.icon_default_male);
            }
            //刷新来访时间
            ViewUtils.setTextViewTxt(this,R.id.txv_time, DateUtil.convertDateYYYYMMddHHmm(System.currentTimeMillis()));
        }else{
            //cleanIDInfo();
        }
    }

    /**
     * 清空刷卡结果
     * */
    private void cleanIDInfo(){
        ViewUtils.setTextViewTxt(this,R.id.tv_name,"");
        ViewUtils.setTextViewTxt(this,R.id.tv_sex,"");
        ViewUtils.setTextViewTxt(this,R.id.tv_nation,"");
        ViewUtils.setTextViewTxt(this,R.id.tv_birthday,"");
        ViewUtils.setTextViewTxt(this,R.id.tv_address,"");
        ViewUtils.setTextViewTxt(this,R.id.tv_idnumber,"");
        //mTextViewAgency.setText(mBCardInfo.agency);//发证机关
        //mTextViewExpire.setText(mBCardInfo.expireStart + " - " + mBCardInfo.expireEnd);//有效日期
        img_headicon.setImageResource(R.mipmap.icon_default_male);
    }

    private void cleanVisitInfo(){
        ViewUtils.setTextViewTxt(this,R.id.txv_time, "");
        ViewUtils.setEditTextTxt(this,R.id.edt_unit, "");
        ViewUtils.setEditTextTxt(this,R.id.edt_contractway, "");
        ViewUtils.setEditTextTxt(this,R.id.edt_carnum, "");
        ViewUtils.setEditTextTxt(this,R.id.edt_visiteto, "");
        spinner_reson.setSelection(0);
        ViewUtils.setEditTextTxt(this,R.id.editText_reson, "");
    }

    public void updateTextStatus(String statusStr){
        txv_statu.setText(statusStr);
    }

    private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(adapterView==mSpinnerMode){
                Log.d(TAG,"mSpinnerMode");
            }else if(adapterView==mSpinnerType){
                Log.d(TAG,"mSpinnerType");
            }else if(adapterView==spinner_reson){
                Log.d(TAG,"spinner_reson");
                TextView tv = (TextView)view;
                //设置颜色
                tv.setTextColor(getResources().getColor(R.color.white));
                //设置大小
                tv.setTextSize(18.0f);
                //设置居中
                tv.setGravity(Gravity.CENTER_VERTICAL);
                //其他
                if(i == 4){
                    editText_reson.setVisibility(View.VISIBLE);
                }else{
                    editText_reson.setVisibility(View.INVISIBLE);
                }
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            Log.d(TAG,"onNothingSelected");
        }
    }
}
