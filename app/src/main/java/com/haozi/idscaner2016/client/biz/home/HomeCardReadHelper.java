package com.haozi.idscaner2016.client.biz.home;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.haozi.idscaner2016.client.bean.client.BCardInfo;
import com.haozi.idscaner2016.client.biz.cardread.ClientBCReceiver;
import com.haozi.idscaner2016.client.biz.cardread.MainMsg;
import com.haozi.idscaner2016.client.biz.cardread.ReadCardServiceCallback;
import com.haozi.idscaner2016.client.biz.cardread.ReadCardSound;
import com.haozi.idscaner2016.client.biz.cardread.ReadInfoCallback;
import com.haozi.idscaner2016.client.biz.cardread.ReadServiceConnection;
import com.haozi.idscaner2016.common.base.BaseCompatActivity;
import com.haozi.idscaner2016.common.base.BaseObject;
import com.routon.idr.idrinterface.readcard.IReadCardService;
import com.routon.idr.idrinterface.readcard.ReadMode;
import com.routon.idr.idrinterface.readcard.ReadState;
import com.routon.idr.idrinterface.readcard.ReadType;
import com.routon.idr.idrinterface.readcard.ReaderBean;
import com.routon.idrconst.Action;

/**
 * Created by Haozi on 2016/4/26.
 */
public class HomeCardReadHelper extends BaseObject implements ReadCardServiceCallback{

    private ReadState mClientState = ReadState.st_unknown;
    //滚动执行间隔时间 20ms
    private static final int ROLL_INTERVAL = 20;
    public IReadCardService mReadCardService;
    public ClientBCReceiver mReceiver;
    //读卡提示音
    private ReadCardSound soundPlayer;
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
    private BaseCompatActivity context;

    /**静态单例初始化*/
    private static final HomeCardReadHelper INSTANCE = new HomeCardReadHelper();
    /**单例静态引用*/
    public static HomeCardReadHelper getInstance() {
        return INSTANCE;
    }

    public void init(BaseCompatActivity context,TextView txv_statu, Button mButtonPause,
                     Button mButtonStop, Button mButtonStart, Spinner mSpinnerMode,
                     Spinner mSpinnerType){

        this.context = context;
        this.txv_statu = txv_statu;
        this.mButtonPause = mButtonPause;
        this.mButtonStop = mButtonStop;
        this.mButtonStart = mButtonStart;
        this.mSpinnerMode = mSpinnerMode;
        this.mSpinnerType = mSpinnerType;

        register(context);
        soundPlayer = new ReadCardSound(context);
        conn_readcard = new ReadServiceConnection(this,context.getMainHandler());
        //绑定读卡服务
        bindReadCardService(context);
        mTextStatus="正在初始化";
        mBCardInfo = new BCardInfo();

        setClientState(ReadState.st_init);
    }

    public void startReading(View view){
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
        if(context.getMainHandler()!=null){
            context.getMainHandler().sendMessage(send_msg);
        }
    }

    public Boolean procOnAny(Message msg){
        Boolean is_processed = false;
        switch(msg.what){
            case MainMsg.EVT_UI_PAUSED:
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
                break;
            case MainMsg.EVT_UI_RESUMED:
                if(mClientState.equals(ReadState.st_idle)){
                    mTextStatus="初始化成功";
                    txv_statu.setText(mTextStatus);
                    mButtonPause.setEnabled(false);
                    mButtonStop.setEnabled(false);
                    mButtonStart.setEnabled(true);
                    is_processed = true;
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
                bindReadCardService(context);
                mTextStatus="正在初始化";
                txv_statu.setText(mTextStatus);
                //跳转到st_init状态
                setClientState(ReadState.st_init);
                //启动轮询定时器
                startRollingTimer(context, ROLL_INTERVAL, true);
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

    public int procOnInit(Message msg){
        switch(msg.what){
            case MainMsg.EVT_SERV_CONNECTED_RDCARD:{
                //停止轮询定时器
                startRollingTimer(context,ROLL_INTERVAL, false);
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
                bindReadCardService(context);
                //启动轮询定时器
                startRollingTimer(context,ROLL_INTERVAL, true);
            }
            break;
            default:
                break;
        }
        return 0;
    }

    public int procOnIdle(Message msg,ReadInfoCallback callback){
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
                            startRollingTimer(context, ROLL_INTERVAL, true);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }else{
                    mTextStatus="已停止读卡";
                    callback.updateTextStatus(mTextStatus);
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
                                    callback.updateTextStatus(mTextStatus);
                                    mButtonPause.setEnabled(true);
                                    mButtonStop.setEnabled(true);
                                    mButtonStart.setEnabled(false);
                                }
                                //重启启动查询状态定时器
                                startRollingTimer(context, ROLL_INTERVAL, true);

                                //状态跳转到st_work
                                setClientState(ReadState.st_work);
                            }
                            case st_cardon_a:
                            case st_cardon_b:
                            {
                                //启动成功
                                //启动查状态定时器
                                startRollingTimer(context, ROLL_INTERVAL, true);

                                //状态跳转到st_work
                                setClientState(ReadState.st_work);
                            }
                            break;
                            case st_fault:
                            {
                                //启动失败
                                mTextStatus="设备通信故障";
                                callback.updateTextStatus(mTextStatus);

                                //启动查状态定时器
                                startRollingTimer(context, ROLL_INTERVAL, true);

                                //留在st_idle状态

                                //lihuili 2016-01-14 st_fault是多余, 程序不会调用procOnFault
                                //状态跳转到st_fault
                                //setClientState(ReadState.st_fault);
                            }
                            break;
                            case st_init:
                            {
                                mTextStatus="服务正在初始化";
                                callback.updateTextStatus(mTextStatus);

                                //启动查状态定时器
                                startRollingTimer(context, ROLL_INTERVAL, true);
                            }
                            break;
                            case st_idle:
                            case st_opened:
                            default:
                            {
                                //启动查状态定时器
                                startRollingTimer(context, ROLL_INTERVAL, true);

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
                startRollingTimer(context, ROLL_INTERVAL, false);

                mTextStatus="请放卡";
                callback.updateTextStatus(mTextStatus);
                mButtonPause.setEnabled(true);
                mButtonStop.setEnabled(true);
                mButtonStart.setEnabled(false);

                //启动查状态定时器
                startRollingTimer(context, ROLL_INTERVAL, true);

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

    public int procOnWork(Message msg,ReadInfoCallback callback){
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
                                    callback.updateTextStatus(mTextStatus);
                                    mButtonPause.setEnabled(true);
                                    mButtonStop.setEnabled(true);
                                    mButtonStart.setEnabled(false);
                                }
                                //重启启动查询状态定时器
                                startRollingTimer(context, ROLL_INTERVAL, true);
                            }
                            break;
                            case st_cardon_a:
                            case st_cardon_b:
                            {
                                //启动成功
                                //重启启动查询状态定时器
                                startRollingTimer(context, ROLL_INTERVAL, true);
                            }
                            break;
                            case st_idle:
                            case st_opened:
                            {
                                startRollingTimer(context, ROLL_INTERVAL, true);
                            }
                            break;
                            case st_init:
                            {
                                mTextStatus = "服务正在初始化";
                                callback.updateTextCardInfo(false,ReadType.B);
                                callback.updateTextStatus(mTextStatus);
                                startRollingTimer(context, ROLL_INTERVAL, true);
                            }
                            break;
                            case st_fault:
                            {
                                mTextStatus = "设备故障";
                                callback.updateTextCardInfo(false,ReadType.B);
                                callback.updateTextStatus(mTextStatus);

                                startRollingTimer(context, ROLL_INTERVAL, true);

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
                callback.updateTextStatus(mTextStatus);
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
                callback.updateTextStatus(mTextStatus);
                callback.updateTextCardInfo(true,ReadType.A);
            }
            break;
            case MainMsg.EVT_LEAVE_A:
            {
                mTextStatus="TypeA卡已离开";
                callback.updateTextStatus(mTextStatus);
                callback.updateTextCardInfo(false,ReadType.A);
            }
            break;
            case MainMsg.EVT_READ_CARD_A_FAIL:
            {
                mTextStatus="读TypeA卡失败";
                callback.updateTextStatus(mTextStatus);
                callback.updateTextCardInfo(false,ReadType.A);
            }
            break;
            case MainMsg.EVT_SELECT_B:
            {
                mTextStatus="TypeB卡选卡成功...";
                callback.updateTextStatus(mTextStatus);
            }
            break;
            case MainMsg.EVT_FOUND_B:
            {
                mTextStatus="TypeB卡寻卡成功...";
                callback.updateTextStatus(mTextStatus);
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
                callback.updateTextStatus(mTextStatus);
                callback.updateTextCardInfo(true,ReadType.B);
            }
            break;
            case MainMsg.EVT_LEAVE_B:
            {
                mTextStatus="TypeB卡已离开";
                callback.updateTextStatus(mTextStatus);
                callback.updateTextCardInfo(false,ReadType.B);
            }
            break;
            case MainMsg.EVT_READ_CARD_B_FAIL:
            {
                mTextStatus="读TypeB卡失败";
                callback.updateTextStatus(mTextStatus);
                callback.updateTextCardInfo(false,ReadType.B);
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
                callback.updateTextCardInfo(false,ReadType.B);
                callback.updateTextStatus(mTextStatus);
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
                callback.updateTextCardInfo(false,ReadType.B);
                callback.updateTextStatus(mTextStatus);
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

    public int procOnOpened(Message msg,ReadInfoCallback callback){
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
                        startRollingTimer(context, ROLL_INTERVAL, true);
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
                startRollingTimer(context, ROLL_INTERVAL, false);

                mTextStatus="请放卡";
                callback.updateTextStatus(mTextStatus);
                mButtonPause.setEnabled(true);
                mButtonStop.setEnabled(true);
                mButtonStart.setEnabled(false);

                //启动查状态定时器
                startRollingTimer(context, ROLL_INTERVAL, true);

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
                                    callback.updateTextStatus(mTextStatus);
                                    mButtonPause.setEnabled(true);
                                    mButtonStop.setEnabled(true);
                                    mButtonStart.setEnabled(false);
                                }
                                //重启启动查询状态定时器
                                startRollingTimer(context, ROLL_INTERVAL, true);

                                //状态跳转到st_work
                                setClientState(ReadState.st_work);
                            }
                            break;
                            case st_cardon_a:
                            case st_cardon_b:
                            {
                                //启动成功

                                //启动查状态定时器
                                startRollingTimer(context, ROLL_INTERVAL, true);

                                //状态跳转到st_work
                                setClientState(ReadState.st_work);
                            }
                            break;
                            case st_fault:
                            {
                                startRollingTimer(context, ROLL_INTERVAL, true);

                                //lihuili 2016-01-14 无需跳转到st_fault状态，仅需要提示
//							 //状态跳转到st_fault
//							 setClientState(ReadState.st_fault);
                            }
                            break;
                            case st_init:
                            {
                                mTextStatus="服务正在初始化";
                                callback.updateTextStatus(mTextStatus);

                                //启动查状态定时器
                                startRollingTimer(context, ROLL_INTERVAL, true);
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
    public int procOnFault(Message msg,ReadInfoCallback callback){
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
                                    callback.updateTextStatus(mTextStatus);
                                    mButtonPause.setEnabled(true);
                                    mButtonStop.setEnabled(true);
                                    mButtonStart.setEnabled(false);
                                }
                                //重启启动查询状态定时器
                                startRollingTimer(context, ROLL_INTERVAL, true);

                                //状态跳转到st_work
                                setClientState(ReadState.st_work);
                            }
                            case st_cardon_a:
                            case st_cardon_b:
                            {
                                //状态恢复正常

                                //启动查状态定时器
                                startRollingTimer(context, ROLL_INTERVAL, true);
                                //状态跳转到st_work
                                setClientState(ReadState.st_work);
                            }
                            break;
                            case st_fault:
                            {
                                mTextStatus = "设备通信故障";
                                callback.updateTextCardInfo(false,ReadType.B);
                                callback.updateTextStatus(mTextStatus);
                                startRollingTimer(context, ROLL_INTERVAL, true);
                            }
                            break;

                            case st_idle:
                            case st_opened:
                            case st_init:
                            default:
                            {
                                //启动查状态定时器
                                startRollingTimer(context, ROLL_INTERVAL, true);
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
                startRollingTimer(context, ROLL_INTERVAL, false);

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
                                callback.updateTextStatus(mTextStatus);
                                mButtonPause.setEnabled(true);
                                mButtonStop.setEnabled(true);
                                mButtonStart.setEnabled(false);

                                //启动查状态定时器
                                startRollingTimer(context, ROLL_INTERVAL, true);
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
                            startRollingTimer(context, ROLL_INTERVAL, true);
                            //状态跳转到st_work
                            setClientState(ReadState.st_work);
                        }
                        break;
                        case st_fault:
                        {
                            mTextStatus = "设备通信故障";
                            callback.updateTextCardInfo(false,ReadType.B);
                            callback.updateTextStatus(mTextStatus);
                            //启动查状态定时器
                            startRollingTimer(context, ROLL_INTERVAL, true);
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

    public void bindReadCardService(Activity context){
        Intent intentReadCard = new Intent(IReadCardService.class.getName());
        Log.d(TAG, "bindReadCardService " + IReadCardService.class.getName());
        context.bindService(intentReadCard, conn_readcard, context.BIND_AUTO_CREATE);
    }

    public void unbindReadCardService(Activity context){
        Log.d(TAG, "unbindReadCardService " + IReadCardService.class.getName());
        if(mReadCardService != null){
            context.unbindService(conn_readcard);
        }
        mReadCardService = null;
    }

    public void register(BaseCompatActivity context){
        mReceiver=new ClientBCReceiver(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action.ACTION_READER_STATE_CHANGED);
        filter.addAction(Action.ACTION_TYPEA_STATUS_CHANGED);
        filter.addAction(Action.ACTION_TYPEB_STATUS_CHANGED);
        filter.addAction(Action.ACTION_READER_PAUSED);
        filter.addAction(Action.ACTION_READER_STOPPED);
        filter.addAction(Action.ACTION_READER_STARTED);
        filter.addAction(Action.ACTION_READER_READMODE_CHANGED);
        filter.addAction(Action.ACTION_READER_READTYPE_CHANGED);
        context.registerReceiver(mReceiver, filter);
    }

    public void unRegister(BaseCompatActivity context){
        if(mReceiver!=null){
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    public ReadState getClientState(){
        return mClientState;
    }

    private void setClientState(ReadState clientState){
        if(mClientState!=clientState){
            Log.d(TAG, mClientState + " to " + clientState);
        }
        mClientState=clientState;
    }

    private int startRollingTimer(BaseCompatActivity activity,int timeout, boolean enable){
        //Log.d(TAG, "startHeartbeatTimer "+ enable);
        if(activity.getMainHandler()!=null){
            if(enable){
                activity.getMainHandler().removeMessages(MainMsg.EVT_TIMEOUT_ROLL);
                activity.getMainHandler().sendEmptyMessageDelayed(MainMsg.EVT_TIMEOUT_ROLL, timeout);
            }else{
                activity.getMainHandler().removeMessages(MainMsg.EVT_TIMEOUT_ROLL);
            }
            roll_timer_running = enable;
        }
        return 0;
    }

    public String getTextStatus(){
        return mTextStatus;
    }

    @Override
    public void setReadCardService(IReadCardService service) {
        this.mReadCardService = service;
    }

    @Override
    public IReadCardService getReadCardService() {
        return mReadCardService;
    }

    public String getCardANo() {
        return cardANo;
    }

    public BCardInfo getBCardInfo() {
        return mBCardInfo;
    }
}
