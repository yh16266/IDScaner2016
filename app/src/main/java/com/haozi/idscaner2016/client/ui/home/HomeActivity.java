package com.haozi.idscaner2016.client.ui.home;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.bean.client.BCardInfo;
import com.haozi.idscaner2016.client.biz.cardread.ClientBCReceiver;
import com.haozi.idscaner2016.client.biz.cardread.MainMsg;
import com.haozi.idscaner2016.client.biz.cardread.ReadCardServiceCallback;
import com.haozi.idscaner2016.client.biz.cardread.ReadCardSound;
import com.haozi.idscaner2016.client.biz.cardread.ReadServiceConnection;
import com.haozi.idscaner2016.client.utils.ViewUtils;
import com.haozi.idscaner2016.common.base.BaseCompatActivity;
import com.routon.idr.idrinterface.readcard.IReadCardService;
import com.routon.idr.idrinterface.readcard.ReadMode;
import com.routon.idr.idrinterface.readcard.ReadState;
import com.routon.idr.idrinterface.readcard.ReadType;
import com.routon.idr.idrinterface.readcard.ReaderBean;
import com.routon.idrconst.Action;

    public class HomeActivity extends BaseCompatActivity implements ReadCardServiceCallback {

    private ReadState mClientState = ReadState.st_unknown;
    private static final int ROLL_INTERVAL = 20;  //滚动执行间隔时间 20ms
    public IReadCardService mReadCardService;
    public ClientBCReceiver mReceiver;
    private ReadCardSound soundPlayer;//读卡提示音
    private ReadServiceConnection conn_readcard;
    public boolean isServRDcardConned = false;
    public boolean roll_timer_running = false;
    public boolean isNeedStart = false;
    private ReaderBean mRdrBean = null;
    private String cardANo;
    private String mTextStatus;
    private BCardInfo mBCardInfo;
    private ReadState m_lastServRdSt = ReadState.st_unknown;

    private TextView txv_statu;
    private Button mButtonStart;
    private Button mButtonPause;
    private Button mButtonStop;
    private Spinner mSpinnerMode;
    private Spinner mSpinnerType;
    private ArrayAdapter spinnerModeAdapter;
    private ArrayAdapter spinnerTypeAdapter;
    private ImageView img_headicon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
    }

    @Override
    protected void initView() {
        initToolbar("首页");
        txv_statu = (TextView) findViewById(R.id.txv_statu);
        mButtonStart = (Button) findViewById(R.id.btn_start);
        mButtonPause = (Button) findViewById(R.id.btn_pause);
        mButtonStop = (Button) findViewById(R.id.btn_stop);
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

        initCardRead();
    }

    private void initCardRead(){
        registerHandler();
        register();
        soundPlayer = new ReadCardSound(this);
        conn_readcard = new ReadServiceConnection(this,mMainHandler);
        //绑定读卡服务
        bindReadCardService();
        mTextStatus="正在初始化";
        txv_statu.setText(mTextStatus);

        mBCardInfo = new BCardInfo();

        setClientState(ReadState.st_init);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        startReading(v);
    }

    private void startReading(View view){
        Message send_msg = new Message();
        if(view==mButtonPause)
        {
            send_msg.what = MainMsg.EVT_PAUSE;
        }else if(view==mButtonStop){
            send_msg.what = MainMsg.EVT_STOP;
        }else if(view==mButtonStart){
            ReaderBean rdrBean = new ReaderBean();
            if(mSpinnerMode!=null){
                rdrBean.setReadMode(ReadMode.valueOf(mSpinnerMode.getSelectedItemPosition()));
            }
            if(mSpinnerType!=null){
                rdrBean.setReadType(ReadType.valueOf(mSpinnerType.getSelectedItemPosition()));
            }
            send_msg.what = MainMsg.EVT_START;
            send_msg.obj = rdrBean;
        }
        //else if(arg0==mButtonReadSam){
        //    send_msg.what = MainMsg.EVT_GET_SAMID;
        //}else if(arg0==mButtonReadMcuVersion){
        //    send_msg.what = MainMsg.EVT_GET_MCU_VERSION;
        //}else if(arg0==mButtonReadVersion){
        //    send_msg.what = MainMsg.EVT_GET_VERSION;
        //}
        if(mMainHandler!=null){
            mMainHandler.sendMessage(send_msg);
        }
    }

    public void bindReadCardService(){
        Intent intentReadCard = new Intent(IReadCardService.class.getName());
        Log.d(TAG, "bindReadCardService " + IReadCardService.class.getName());
        bindService(intentReadCard, conn_readcard, BIND_AUTO_CREATE);
    }

    public void unbindReadCardService(){
        Log.d(TAG, "unbindReadCardService " + IReadCardService.class.getName());
        if(mReadCardService != null){
            unbindService(conn_readcard);
        }
        mReadCardService = null;
    }

//-------------------------------备份注释部分，恢复时取消注释---------------------------------------------------
    private void register(){
        mReceiver=new ClientBCReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.ACTION_READER_STATE_CHANGED);
        filter.addAction(Action.ACTION_TYPEA_STATUS_CHANGED);
        filter.addAction(Action.ACTION_TYPEB_STATUS_CHANGED);
        filter.addAction(Action.ACTION_READER_PAUSED);
        filter.addAction(Action.ACTION_READER_STOPPED);
        filter.addAction(Action.ACTION_READER_STARTED);
        filter.addAction(Action.ACTION_READER_READMODE_CHANGED);
        filter.addAction(Action.ACTION_READER_READTYPE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    private void unRegister(){
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void setClientState(ReadState clientState){
        if(mClientState!=clientState){
            Log.d(TAG, mClientState + " to " + clientState);
        }
        mClientState=clientState;
    }

    private int startRollingTimer(int timeout, boolean enable){
        //Log.d(TAG, "startHeartbeatTimer "+ enable);
        if(mMainHandler!=null){
            if(enable){
                mMainHandler.removeMessages(MainMsg.EVT_TIMEOUT_ROLL);
                mMainHandler.sendEmptyMessageDelayed(MainMsg.EVT_TIMEOUT_ROLL, timeout);
            }else{
                mMainHandler.removeMessages(MainMsg.EVT_TIMEOUT_ROLL);
            }
            roll_timer_running = enable;
        }
        return 0;
    }

    @Override
    public boolean handleMessage(Message msg) {
        boolean isProcessed = procOnAny(msg);
        if(!isProcessed){
            switch(mClientState){
                case st_init:
                    procOnInit(msg);
                    break;
                case st_idle:
                    procOnIdle(msg);
                    break;
                case st_work:
                    procOnWork(msg);
                    break;
                case st_opened:
                    procOnOpened(msg);
                    break;
                case st_fault:
                    //lihuili 2016-01-14 st_fault是多余, 程序不会调用procOnFault
                    procOnFault(msg);
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private Boolean procOnAny(Message msg){
        Boolean is_processed = false;
        switch(msg.what){
            case MainMsg.EVT_UI_PAUSED:{
                if(mReadCardService!=null){
                    try {
                        //停止读卡
                        mReadCardService.stop();
                        isNeedStart = false;
                        //跳转到st_idle状态
                        setClientState(ReadState.st_idle);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    is_processed = true;
                }
            }
            break;
            case MainMsg.EVT_UI_RESUMED:{
                if(mClientState.equals(ReadState.st_idle)){
                    mTextStatus="初始化成功";
                    txv_statu.setText(mTextStatus);
                    mButtonPause.setEnabled(false);
                    mButtonStop.setEnabled(false);
                    mButtonStart.setEnabled(true);
                    is_processed = true;
                }
            }
            break;
            case MainMsg.EVT_GET_VERSION:{
//                if(mReadCardService!=null){
//                    try {
//                        String ver = mReadCardService.getVersion();
//                        if(ver!=null){
//                            mEditTextVersion.setText(ver);
//                        }else{
//                            mEditTextVersion.setText("读服务版本号失败!");
//                        }
//                    } catch (RemoteException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
            }
            break;
            case MainMsg.EVT_SERV_DISCONNECTED_RDCARD:{
                mTextStatus="读卡服务程序已退出";
                txv_statu.setText(mTextStatus);
                mButtonPause.setEnabled(false);
                mButtonStop.setEnabled(false);
                mButtonStart.setEnabled(false);
                bindReadCardService();
                mTextStatus="正在初始化";
                txv_statu.setText(mTextStatus);
                //跳转到st_init状态
                setClientState(ReadState.st_init);
                //启动轮询定时器
                startRollingTimer(ROLL_INTERVAL, true);
                is_processed = true;
            }
            break;
            case MainMsg.EVT_READMODE_CHANGED_SERV:{
                //修改读卡模式
                ReadMode rdMode = (ReadMode)msg.obj;
                mSpinnerMode.setSelection(rdMode.getValue());
            }
            break;
            case MainMsg.EVT_READTYPE_CHANGED_SERV:{
                //修改读卡类型
                ReadType rdType = (ReadType)msg.obj;
                mSpinnerType.setSelection(rdType.getValue());
            }
            break;
            default:
                break;
        }
        return is_processed;
    }

    private int procOnInit(Message msg){
        switch(msg.what){
            case MainMsg.EVT_SERV_CONNECTED_RDCARD:{
                //停止轮询定时器
                startRollingTimer(ROLL_INTERVAL, false);
                //读卡服务连接成功后,等待用户的界面操作
                if(mReadCardService!=null){
                    isServRDcardConned = true;
                    mTextStatus="初始化成功";
                    txv_statu.setText(mTextStatus);
                    mButtonPause.setEnabled(false);
                    mButtonStop.setEnabled(false);
                    mButtonStart.setEnabled(true);
                    setClientState(ReadState.st_idle);
                }
            }
            break;
            case MainMsg.EVT_TIMEOUT_ROLL: {
                bindReadCardService();
                //启动轮询定时器
                startRollingTimer(ROLL_INTERVAL, true);
            }
            break;
            default:
                break;
        }
        return 0;
    }

    private int procOnIdle(Message msg){
        switch(msg.what){
            case MainMsg.EVT_START:
            {
                //接收到启动读卡请求
                mTextStatus="正在启动读卡";
                txv_statu.setText(mTextStatus);

                mButtonPause.setEnabled(false);
                mButtonStop.setEnabled(true);
                mButtonStart.setEnabled(false);

                mRdrBean = (ReaderBean)msg.obj;
                if(mReadCardService!=null){
                    if(mRdrBean!=null){
                        try {
                            isNeedStart = true;

                            //先发送停止读卡请求,然后等待事件EVT_STOPPED
                            mReadCardService.stop();
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
            break;
            case MainMsg.EVT_STOPPED:
            {
                //停止成功
                if(isNeedStart){
                    isNeedStart = false;
                    if(  (mReadCardService!=null)
                            &&(mRdrBean!=null)){
                        try {
                            mReadCardService.start(mRdrBean);
                            startRollingTimer(ROLL_INTERVAL, true);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }else{
                    mTextStatus="已停止读卡";
                    updateTextStatus(mTextStatus);
                    mButtonPause.setEnabled(false);
                    mButtonStop.setEnabled(false);
                    mButtonStart.setEnabled(true);
                }
            }
            break;
            case MainMsg.EVT_TIMEOUT_ROLL:
            {
                //查询读卡服务的状态
                if(mReadCardService!=null){
                    ReadState servRdSt;
                    try {
                        servRdSt = mReadCardService.getState();
                        switch(servRdSt)
                        {
                            case st_idle_auto:
                            case st_idle_online:
                            {
                                if(!m_lastServRdSt.equals(servRdSt)){
                                    mTextStatus="请放卡";
                                    updateTextStatus(mTextStatus);
                                    mButtonPause.setEnabled(true);
                                    mButtonStop.setEnabled(true);
                                    mButtonStart.setEnabled(false);
                                }
                                //重启启动查询状态定时器
                                startRollingTimer(ROLL_INTERVAL, true);

                                //状态跳转到st_work
                                setClientState(ReadState.st_work);
                            }
                            case st_cardon_a:
                            case st_cardon_b:
                            {
                                //启动成功
                                //启动查状态定时器
                                startRollingTimer(ROLL_INTERVAL, true);

                                //状态跳转到st_work
                                setClientState(ReadState.st_work);
                            }
                            break;
                            case st_fault:
                            {
                                //启动失败
                                mTextStatus="设备通信故障";
                                updateTextStatus(mTextStatus);

                                //启动查状态定时器
                                startRollingTimer(ROLL_INTERVAL, true);

                                //留在st_idle状态

                                //lihuili 2016-01-14 st_fault是多余, 程序不会调用procOnFault
                                //状态跳转到st_fault
                                //setClientState(ReadState.st_fault);
                            }
                            break;
                            case st_init:
                            {
                                mTextStatus="服务正在初始化";
                                updateTextStatus(mTextStatus);

                                //启动查状态定时器
                                startRollingTimer(ROLL_INTERVAL, true);
                            }
                            break;
                            case st_idle:
                            case st_opened:
                            default:
                            {
                                //启动查状态定时器
                                startRollingTimer(ROLL_INTERVAL, true);

                                //留在st_idle状态
                            }
                            break;
                        }
                        m_lastServRdSt = servRdSt;
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            break;
            case MainMsg.EVT_STARTED:
            {
                //启动成功
                //停定时器
                startRollingTimer(ROLL_INTERVAL, false);

                mTextStatus="请放卡";
                updateTextStatus(mTextStatus);
                mButtonPause.setEnabled(true);
                mButtonStop.setEnabled(true);
                mButtonStart.setEnabled(false);

                //启动查状态定时器
                startRollingTimer(ROLL_INTERVAL, true);

                //状态跳转到st_work
                setClientState(ReadState.st_work);
            }
            break;
            case MainMsg.EVT_STOP:
            {
                if(mReadCardService!=null){
                    try {
                        isNeedStart = false;
                        mTextStatus="正在停止读卡";
                        txv_statu.setText(mTextStatus);
                        //停止读卡,且等待EVT_STOPPED事件
                        mReadCardService.stop();
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            break;
            default:
                break;
        }
        return 0;
    }

    private int procOnWork(Message msg){
        switch(msg.what){
            case MainMsg.EVT_TIMEOUT_ROLL:
            {
                if(mReadCardService!=null){
                    //查询读卡服务的状态
                    ReadState servRdSt;
                    try {
                        servRdSt = mReadCardService.getState();
                        switch(servRdSt)
                        {
                            case st_idle_auto:
                            case st_idle_online:
                            {
                                if(!m_lastServRdSt.equals(servRdSt)){
                                    mTextStatus="请放卡";
                                    updateTextStatus(mTextStatus);
                                    mButtonPause.setEnabled(true);
                                    mButtonStop.setEnabled(true);
                                    mButtonStart.setEnabled(false);
                                }
                                //重启启动查询状态定时器
                                startRollingTimer(ROLL_INTERVAL, true);
                            }
                            break;
                            case st_cardon_a:
                            case st_cardon_b:
                            {
                                //启动成功
                                //重启启动查询状态定时器
                                startRollingTimer(ROLL_INTERVAL, true);
                            }
                            break;
                            case st_idle:
                            case st_opened:
                            {
                                startRollingTimer(ROLL_INTERVAL, true);
                            }
                            break;
                            case st_init:
                            {
                                mTextStatus = "服务正在初始化";
                                updateTextCardInfo(false,ReadType.B);
                                updateTextStatus(mTextStatus);
                                startRollingTimer(ROLL_INTERVAL, true);
                            }
                            break;
                            case st_fault:
                            {
                                mTextStatus = "设备故障";
                                updateTextCardInfo(false,ReadType.B);
                                updateTextStatus(mTextStatus);

                                startRollingTimer(ROLL_INTERVAL, true);

                                //lihuili 2016-01-14 无需跳转到st_fault状态，仅需要提示
//							 //状态跳转到st_fault
//							 setClientState(ReadState.st_fault);
                            }
                            break;
                            default:
                                break;
                        }

                        m_lastServRdSt = servRdSt;
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            break;
            case MainMsg.EVT_FOUND_A:
            {
                mTextStatus="正在读TypeA卡...";
                updateTextStatus(mTextStatus);
            }
            break;
            case MainMsg.EVT_READ_CARD_A_SUCCESS:
            {
                //播放读卡提示音
                if(soundPlayer!=null){
                    soundPlayer.playSound(ReadCardSound.NORMAL);
                }
                //更新界面显示
                mTextStatus="读TypeA卡成功";
                byte data[]=(byte[])msg.obj;
                cardANo=String.format("%02X%02X%02X%02X", data[0], data[1], data[2], data[3]);
                updateTextStatus(mTextStatus);
                updateTextCardInfo(true,ReadType.A);
            }
            break;
            case MainMsg.EVT_LEAVE_A:
            {
                mTextStatus="TypeA卡已离开";
                updateTextStatus(mTextStatus);
                updateTextCardInfo(false,ReadType.A);
            }
            break;
            case MainMsg.EVT_READ_CARD_A_FAIL:
            {
                mTextStatus="读TypeA卡失败";
                updateTextStatus(mTextStatus);
                updateTextCardInfo(false,ReadType.A);
            }
            break;
            case MainMsg.EVT_SELECT_B:
            {
                mTextStatus="TypeB卡选卡成功...";
                updateTextStatus(mTextStatus);
            }
            break;
            case MainMsg.EVT_FOUND_B:
            {
                mTextStatus="TypeB卡寻卡成功...";
                updateTextStatus(mTextStatus);
            }
            break;
            case MainMsg.EVT_READ_CARD_B_SUCCESS:
            {
                //播放读卡提示音
                if(soundPlayer!=null){
                    soundPlayer.playSound(ReadCardSound.NORMAL);
                }
                //更新界面显示
                mTextStatus="读TypeB卡成功";

                mBCardInfo=(BCardInfo)msg.obj;
                updateTextStatus(mTextStatus);
                updateTextCardInfo(true,ReadType.B);
            }
            break;
            case MainMsg.EVT_LEAVE_B:
            {
                mTextStatus="TypeB卡已离开";
                updateTextStatus(mTextStatus);
                updateTextCardInfo(false,ReadType.B);
            }
            break;
            case MainMsg.EVT_READ_CARD_B_FAIL:
            {
                mTextStatus="读TypeB卡失败";
                updateTextStatus(mTextStatus);
                updateTextCardInfo(false,ReadType.B);
            }
            break;
            case MainMsg.EVT_GET_SAMID:
            {
//                if(mReadCardService!=null){
//                    try {
//                        String samId = mReadCardService.getSamId();
//                        if(samId!=null){
//                            mEditTextSam.setText(samId);
//                        }else{
//                            mEditTextSam.setText("读安全模块号失败!");
//                        }
//                    } catch (RemoteException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
            }
            break;
            case MainMsg.EVT_GET_MCU_VERSION:
            {
//                if(mReadCardService!=null){
//                    try {
//                        String mcuVer = mReadCardService.getChipVersion();
//                        if(mcuVer!=null){
//                            mEditTextMcuVersion.setText(mcuVer);
//                        }else{
//                            mEditTextMcuVersion.setText("读单片机版本号失败!");
//                        }
//                    } catch (RemoteException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
            }
            break;
            case MainMsg.EVT_PAUSE:
                if(mReadCardService!=null){
                    try {
                        mTextStatus="正在暂停读卡";
                        txv_statu.setText(mTextStatus);
                        //暂停读卡,且等待EVT_PAUSED事件
                        mReadCardService.pause();
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case MainMsg.EVT_PAUSED:
            {
                mTextStatus = "已暂停读卡";
                updateTextCardInfo(false,ReadType.B);
                updateTextStatus(mTextStatus);
                mButtonPause.setEnabled(false);
                mButtonStop.setEnabled(true);
                mButtonStart.setEnabled(true);

                //状态跳转到st_idle
                setClientState(ReadState.st_opened);
            }
            break;
            case MainMsg.EVT_STOP:
                if(mReadCardService!=null){
                    try {
                        mTextStatus="正在停止读卡";
                        txv_statu.setText(mTextStatus);
                        //停止读卡,且等待EVT_STOPPED事件
                        mReadCardService.stop();
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
            case MainMsg.EVT_STOPPED:
            {
                mTextStatus="已停止读卡";
                updateTextCardInfo(false,ReadType.B);
                updateTextStatus(mTextStatus);
                mButtonPause.setEnabled(false);
                mButtonStop.setEnabled(false);
                mButtonStart.setEnabled(true);

                //状态跳转到st_idle
                setClientState(ReadState.st_idle);
            }
            break;
            default:
                break;
        }
        return 0;
    }

    private int procOnOpened(Message msg){
        switch(msg.what){
            case MainMsg.EVT_START:
            {
                //接收到启动读卡请求
                mTextStatus="正在启动读卡";
                txv_statu.setText(mTextStatus);

                mRdrBean = (ReaderBean)msg.obj;
                if(  (mReadCardService!=null)
                        &&(mRdrBean!=null)){
                    //先发送启动读卡请求,然后等待事件EVT_STARTED 或者 查状态定时器到事件EVT_TIMEOUT_ROLL
                    try {
                        mReadCardService.start(mRdrBean);
                        startRollingTimer(ROLL_INTERVAL, true);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            break;
            case MainMsg.EVT_STARTED:
            {
                //启动成功
                //停定时器
                startRollingTimer(ROLL_INTERVAL, false);

                mTextStatus="请放卡";
                updateTextStatus(mTextStatus);
                mButtonPause.setEnabled(true);
                mButtonStop.setEnabled(true);
                mButtonStart.setEnabled(false);

                //启动查状态定时器
                startRollingTimer(ROLL_INTERVAL, true);

                //状态跳转到st_work
                setClientState(ReadState.st_work);
            }
            break;
            case MainMsg.EVT_TIMEOUT_ROLL:
            {
                //查询读卡服务的状态
                if(mReadCardService!=null){
                    ReadState servRdSt;
                    try {
                        servRdSt = mReadCardService.getState();
                        switch(servRdSt)
                        {
                            case st_idle_auto:
                            case st_idle_online:
                            {
                                if(!m_lastServRdSt.equals(servRdSt)){
                                    mTextStatus="请放卡";
                                    updateTextStatus(mTextStatus);
                                    mButtonPause.setEnabled(true);
                                    mButtonStop.setEnabled(true);
                                    mButtonStart.setEnabled(false);
                                }
                                //重启启动查询状态定时器
                                startRollingTimer(ROLL_INTERVAL, true);

                                //状态跳转到st_work
                                setClientState(ReadState.st_work);
                            }
                            break;
                            case st_cardon_a:
                            case st_cardon_b:
                            {
                                //启动成功

                                //启动查状态定时器
                                startRollingTimer(ROLL_INTERVAL, true);

                                //状态跳转到st_work
                                setClientState(ReadState.st_work);
                            }
                            break;
                            case st_fault:
                            {
                                startRollingTimer(ROLL_INTERVAL, true);

                                //lihuili 2016-01-14 无需跳转到st_fault状态，仅需要提示
//							 //状态跳转到st_fault
//							 setClientState(ReadState.st_fault);
                            }
                            break;
                            case st_init:
                            {
                                mTextStatus="服务正在初始化";
                                updateTextStatus(mTextStatus);

                                //启动查状态定时器
                                startRollingTimer(ROLL_INTERVAL, true);
                            }
                            break;
                            case st_idle:
                            case st_opened:
                            default:
                            {

                            }
                            break;
                        }
                        m_lastServRdSt = servRdSt;
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            break;

            case MainMsg.EVT_STOP:
            {
                //接收到停止读卡请求
                if(  (mReadCardService!=null)
                        ){
                    try {
                        mTextStatus="正在停止读卡";
                        txv_statu.setText(mTextStatus);
                        //先发送停止读卡请求,然后等待事件EVT_STOPPED
                        mReadCardService.stop();
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            break;
            case MainMsg.EVT_STOPPED:
            {
                if(  (mReadCardService!=null)
                        &&(mRdrBean!=null)){
                    mTextStatus="已停止读卡";
                    txv_statu.setText(mTextStatus);

                    mButtonPause.setEnabled(false);
                    mButtonStop.setEnabled(false);
                    mButtonStart.setEnabled(true);
                    //状态跳转到st_idle
                    setClientState(ReadState.st_idle);
                }
            }
            break;
            default:
                break;
        }
        return 0;
    }

    //lihuili 2016-01-14 st_fault是多余, 程序不会调用procOnFault
    private int procOnFault(Message msg){
        switch(msg.what){
            case MainMsg.EVT_TIMEOUT_ROLL:
            {
                if(mReadCardService!=null){
                    //查询读卡服务的状态
                    try {
                        ReadState servRdSt = mReadCardService.getState();
                        switch(servRdSt)
                        {
                            case st_idle_auto:
                            case st_idle_online:
                            {
                                if(!m_lastServRdSt.equals(servRdSt)){
                                    mTextStatus="请放卡";
                                    updateTextStatus(mTextStatus);
                                    mButtonPause.setEnabled(true);
                                    mButtonStop.setEnabled(true);
                                    mButtonStart.setEnabled(false);
                                }
                                //重启启动查询状态定时器
                                startRollingTimer(ROLL_INTERVAL, true);

                                //状态跳转到st_work
                                setClientState(ReadState.st_work);
                            }
                            case st_cardon_a:
                            case st_cardon_b:
                            {
                                //状态恢复正常

                                //启动查状态定时器
                                startRollingTimer(ROLL_INTERVAL, true);
                                //状态跳转到st_work
                                setClientState(ReadState.st_work);
                            }
                            break;
                            case st_fault:
                            {
                                mTextStatus = "设备通信故障";
                                updateTextCardInfo(false,ReadType.B);
                                updateTextStatus(mTextStatus);

                                startRollingTimer(ROLL_INTERVAL, true);
                            }
                            break;

                            case st_idle:
                            case st_opened:
                            case st_init:
                            default:
                            {
                                //启动查状态定时器
                                startRollingTimer(ROLL_INTERVAL, true);
                            }
                            break;
                        }
                        m_lastServRdSt = servRdSt;
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            break;
            case MainMsg.EVT_STATE_CHANGE:
            {
                //停定时器
                startRollingTimer(ROLL_INTERVAL, false);

                //读卡服务状态改变
                ReadState servRdSt = (ReadState)msg.obj;
                if(servRdSt!=null){
                    switch(servRdSt)
                    {
                        case st_idle_auto:
                        case st_idle_online:
                        {
                            if(!m_lastServRdSt.equals(servRdSt)){
                                mTextStatus="请放卡";
                                updateTextStatus(mTextStatus);
                                mButtonPause.setEnabled(true);
                                mButtonStop.setEnabled(true);
                                mButtonStart.setEnabled(false);

                                //启动查状态定时器
                                startRollingTimer(ROLL_INTERVAL, true);
                                //状态跳转到st_work
                                setClientState(ReadState.st_work);
                            }
                        }
                        break;
                        case st_cardon_a:
                        case st_cardon_b:
                        {
                            //状态恢复正常

                            //启动查状态定时器
                            startRollingTimer(ROLL_INTERVAL, true);
                            //状态跳转到st_work
                            setClientState(ReadState.st_work);
                        }
                        break;
                        case st_fault:
                        {
                            mTextStatus = "设备通信故障";
                            updateTextCardInfo(false,ReadType.B);
                            updateTextStatus(mTextStatus);

                            //启动查状态定时器
                            startRollingTimer(ROLL_INTERVAL, true);
                        }
                        break;
                        case st_idle:
                        case st_opened:
                        case st_init:
                        default:
                        {
                            //do nothing
                        }
                        break;
                    }
                    m_lastServRdSt = servRdSt;
                }
            }
            break;
            default:
                break;
        }
        return 0;
    }

    @Override
    public void setReadCardService(IReadCardService service) {
        this.mReadCardService = service;
    }

    @Override
    public IReadCardService getReadCardService() {
        return mReadCardService;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegister();
        if(mReadCardService != null){
            unbindReadCardService();
        }
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
        }else{
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
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            Log.d(TAG,"onNothingSelected");
        }
    }
}
