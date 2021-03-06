package com.haozi.idscaner2016.client.ui.home;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.print.PrinterSettingActivity;
import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.bean.EntityDitionary;
import com.haozi.idscaner2016.client.bean.client.BCardInfo;
import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.client.biz.cardread.MainMsg;
import com.haozi.idscaner2016.client.biz.cardread.ReadInfoCallback;
import com.haozi.idscaner2016.client.biz.home.HomeCardReadHelper;
import com.haozi.idscaner2016.client.biz.home.UnityManageHelper;
import com.haozi.idscaner2016.client.biz.home.VisitRecordHelper;
import com.haozi.idscaner2016.client.control.DXSignPop;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.client.data.AccountHelper;
import com.haozi.idscaner2016.client.data.sqlite.UserTable;
import com.haozi.idscaner2016.client.utils.ViewUtils;
import com.haozi.idscaner2016.common.base.BaseCompatActivity;
import com.haozi.idscaner2016.common.utils.DateUtil;
import com.haozi.idscaner2016.common.utils.StringUtil;
import com.haozi.idscaner2016.constants.IActionIntent;
import com.haozi.idscaner2016.printer.PrinterHelper;
import com.routon.idr.idrinterface.readcard.ReadType;

import java.io.File;

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
    private EditText edt_unit;
    private TextView txv_unit;
    private RadioGroup radioGroup_type;

    private BroadcastReceiver mBroadcastRecevier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadCastReciver();
        setContentView(R.layout.home_activity);
    }

    @Override
    protected void initView() {
        initToolbar("首页New");
        radioGroup_type = (RadioGroup) findViewById(R.id.radioGroup_type);
        findViewById(R.id.btn_clean).setOnClickListener(this);
        findViewById(R.id.btn_print).setOnClickListener(this);
        findViewById(R.id.btn_leve).setOnClickListener(this);

        txv_statu = (TextView) findViewById(R.id.txv_statu);
        txv_sign = (TextView) findViewById(R.id.txv_sign);
        txv_sign.setOnClickListener(this);
        img_sign = (ImageView) findViewById(R.id.img_sign);
        mButtonStart = (Button) findViewById(R.id.btn_start);
        mButtonPause = (Button) findViewById(R.id.btn_pause);
        mButtonStop = (Button) findViewById(R.id.btn_stop);
        editText_reson = (EditText) findViewById(R.id.editText_reson);

        edt_unit = (EditText) findViewById(R.id.edt_unit);
        txv_unit = (TextView) findViewById(R.id.txv_unit);
        //edt_unit.setVisibility(View.GONE);
        txv_unit.setOnClickListener(this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_title_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                if(AccountHelper.getInstance().IsUserLogin() == false) {
                    DXToast.show("请登陆!");
                }else{
                    Intent intent = new Intent(this,RecordSearchActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.menu_sum:
                if(AccountHelper.getInstance().IsUserLogin() == false) {
                    DXToast.show("请登陆!");
                }else{
                    Intent intent = new Intent(this,RecordSumActivity.class);
                    startActivity(intent);
                }
                break;
//            case R.id.menu_unit:
//                intent = new Intent(this,UnitManageActivity.class);
//                startActivity(intent);
//                break;
            case R.id.menu_printer_setting:
                Intent intent = new Intent(this,PrinterSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_user_setting:
                if(AccountHelper.getInstance().IsUserLogin() == false) {
                    DXToast.show("请登陆!");
                }else if(EntityDitionary.UserType.admin == AccountHelper.getInstance().getMyinfo().UserType()){
                    intent = new Intent(this, UserSettingActivity.class);
                    startActivity(intent);
                }else{
                    DXToast.show("只有管理员能执行此操作");
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void initCardRead(){
        registerHandler();
        HomeCardReadHelper.getInstance().init(this,txv_statu,mButtonPause,mButtonStop,mButtonStart,mSpinnerMode,mSpinnerType);
        txv_statu.setText(HomeCardReadHelper.getInstance().getTextStatus());
        //自动启动读卡
        HomeCardReadHelper.getInstance().startReading(mButtonStart);
    }

    public Handler getMainHandler(){
        return mMainHandler;
    }

    private void registerBroadCastReciver(){
        mBroadcastRecevier = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(IActionIntent.ACTION_VISITOR_LEAVE.equals(intent.getAction())){
                    cleanIDInfo();
                    cleanVisitInfo(false);
                }else if(IActionIntent.ACTION_INFO_CLEAN.equals(intent.getAction())){
                    //如果是团队访问，则不会清理来访信息
                    if(radioGroup_type.getCheckedRadioButtonId() == R.id.radio_person){
                        cleanVisitInfo(false);
                    }
                    cleanIDInfo();
                }
            }
        };
        IntentFilter ift = new IntentFilter(IActionIntent.ACTION_VISITOR_LEAVE);
        ift.addAction(IActionIntent.ACTION_INFO_CLEAN);
        registerReceiver(mBroadcastRecevier,ift);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.txv_unit:
                showUnitDialog(v);
                break;
            case R.id.txv_sign:
                showSignView(v);
                break;
            case R.id.btn_clean:
                cleanIDInfo();
                cleanVisitInfo(true);
                break;
            case R.id.btn_leve:
                showLeaveDailog();
                break;
            case R.id.btn_print:
                printAndSaveVisitInfo();
                //intent = new Intent(this,GprinterMainActivity.class);
                //startActivity(intent);
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
        PrinterHelper.getInstance().unBindConnection();
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
            //如果是团队访问，则不会清理来访信息
            if(radioGroup_type.getCheckedRadioButtonId() == R.id.radio_person){
                cleanVisitInfo(false);
            }
            //刷新来访时间
            long visitTime = System.currentTimeMillis();
            ViewUtils.setTextViewTxt(this,R.id.txv_time, DateUtil.convertDateYYYYMMddHHmm(visitTime));
            ViewUtils.setViewTag(this,R.id.txv_time, visitTime);
            //查询是否已登记并且未登记离开，若成立，则提示离开登记
            String mIdNum = ViewUtils.getTextViewString(this,R.id.tv_idnumber);
            if(!StringUtil.isEmpty(mIdNum)){
                VisitRecordEntity mRecord = VisitRecordHelper.getInstance().getRecordNotLeave(mIdNum);
                if(mRecord != null){
                    LeaveConfirmDialog.showByIdNum(this,mIdNum);
                    return;
                }
            }
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

    private void cleanVisitInfo(boolean isCleanSignFile){
        ViewUtils.setTextViewTxt(this,R.id.txv_time, "");
        ViewUtils.setEditTextTxt(this,R.id.edt_unit, "");
        ViewUtils.setEditTextTxt(this,R.id.edt_contractway, "");
        ViewUtils.setEditTextTxt(this,R.id.edt_carnum, "");
        ViewUtils.setEditTextTxt(this,R.id.edt_visiteto, "");
        spinner_reson.setSelection(0);
        ViewUtils.setEditTextTxt(this,R.id.editText_reson, "");
        radioGroup_type.check(R.id.radio_person);
        //清除签字缓存文件
        img_sign.setImageResource(R.color.white);
        if(img_sign.getTag() != null){
            if(isCleanSignFile){
                File cacheFile = new File(img_sign.getTag().toString());
                if(cacheFile.exists()){
                    cacheFile.delete();
                }
            }
            img_sign.setTag(null);
        }
        txv_sign.setVisibility(View.VISIBLE);
    }

    private void showUnitDialog(View view){
        final String items[]= UnityManageHelper.getInstance().getRecordNameArray();
        //dialog参数设置
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("来访单位");
        //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                txv_unit.setText(items[which]);
                edt_unit.setText(items[which]);
            }
        });
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showSignView(View v){
        BCardInfo mBCardInfo = HomeCardReadHelper.getInstance().getBCardInfo();
        if(mBCardInfo == null){
            DXToast.show("请先扫描身份证，然后再签字登记！");
            return;
        }
        //如果有未签离记录，请先执行签离
        VisitRecordEntity mRecord = VisitRecordHelper.getInstance().getRecordNotLeave(mBCardInfo.id);
        if(mRecord != null){
            LeaveConfirmDialog.showByIdNum(this,mBCardInfo.id);
            return;
        }
        DXSignPop signPop = new DXSignPop(this);
        signPop.setSignCallback(new DXSignPop.SignCacllBack() {
            @Override
            public void callback(String filePath, Bitmap signBitmap) {
                img_sign.setImageBitmap(signBitmap);
                img_sign.setTag(filePath);
                txv_sign.setVisibility(View.INVISIBLE);
            }
        });
        signPop.showPop(v, mBCardInfo.name,mBCardInfo.id);
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
                if(tv == null){
                    return;
                }
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

    private void printAndSaveVisitInfo(){
        //保存访客信息
        VisitRecordEntity recordEntity = refershRecordInfo();
        if(recordEntity == null){
            return;
        }
        //如果有未签离记录，请先执行签离
        VisitRecordEntity mRecord = VisitRecordHelper.getInstance().getRecordNotLeave(recordEntity.getIdNum());
        if(mRecord != null){
            LeaveConfirmDialog.showByIdNum(this,recordEntity.getIdNum());
            return;
        }
        //long newID = 0;
        //跳转到打印页面(打印并保存)
        PrinterHelper.getInstance().printVisitCard(this,recordEntity);
    }

    private VisitRecordEntity refershRecordInfo(){
        //保存访客信息
        VisitRecordEntity recordEntity = new VisitRecordEntity();
        recordEntity.setName(ViewUtils.getTextViewString(this,R.id.tv_name));
        recordEntity.setSex(ViewUtils.getTextViewString(this,R.id.tv_sex));
        recordEntity.setNation(ViewUtils.getTextViewString(this,R.id.tv_nation));
        recordEntity.setBirthday(ViewUtils.getTextViewString(this,R.id.tv_birthday));
        recordEntity.setAddress(ViewUtils.getTextViewString(this,R.id.tv_address));
        recordEntity.setIdNum(ViewUtils.getTextViewString(this,R.id.tv_idnumber));

        recordEntity.setVisitTime(ViewUtils.getViewTagLong(this,R.id.txv_time));
        recordEntity.setVisitUnit(ViewUtils.getTextViewString(this,R.id.edt_unit));
        if(StringUtil.isEmpty(recordEntity.getVisitUnit())){
            DXToast.show("请填写来访单位");
            return null;
        }
        recordEntity.setVisitContract(ViewUtils.getTextViewString(this,R.id.edt_contractway));
        recordEntity.setVisitCarnum(ViewUtils.getTextViewString(this,R.id.edt_carnum));
        recordEntity.setBeVisited(ViewUtils.getTextViewString(this,R.id.edt_visiteto));
        if(StringUtil.isEmpty(recordEntity.getBeVisited())){
            DXToast.show("请填写被访人");
            return null;
        }
        String visitReson = "";
        if(spinner_reson.getSelectedItemPosition() == 4){
            visitReson = ViewUtils.getEditString(this,R.id.editText_reson);
        }else{
            visitReson = spinner_reson.getSelectedItem().toString();
        }
        recordEntity.setVisitReson(visitReson);
        if(StringUtil.isEmpty(recordEntity.getVisitReson())){
            DXToast.show("请填写访问理由");
            return null;
        }

        if(img_sign.getTag() == null || StringUtil.isEmpty(img_sign.getTag().toString())){
            DXToast.show("请来访者签名确认！");
            return null;
        }
        recordEntity.setVisitSign(img_sign.getTag().toString());

        return recordEntity;
    }

    private void showLeaveDailog(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("签离方式");
//        builder.setNegativeButton("手动签离", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                    showInputCheckCodeDailog();
//                }
//            });
//        builder.setPositiveButton("扫描签离", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                    Intent intent = new Intent(HomeNewActivity.this,CodeScanActivity.class);
//                    //intent.putExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_BARCODE_MODE);
//                    startActivity(intent);
//                    //LeaveConfirmDialog.showByIdNum(this,"510622198709084211");
//                }
//            });
//        builder.create().show();

        showInputCheckCodeDailog();
    }

    private void showInputCheckCodeDailog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入单号");
        builder.setIcon(android.R.drawable.ic_dialog_info);
        final EditText edit = new EditText(this);
        builder.setView(edit);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(edit.getText() == null || StringUtil.isEmpty(edit.getText().toString())){
                    DXToast.show("单号不能为空，签离失败");
                }else{
                    String checkCode = edit.getText().toString();
                    LeaveConfirmDialog.showByCheckCode(HomeNewActivity.this,checkCode);
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        PrinterHelper.getInstance().onActivityResult(this,requestCode, resultCode, data);
//    }
}
