package com.haozi.idscaner2016.printer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.android.print.BluetoothOperation;
import com.android.print.Constants;
import com.android.print.IPrinterOpertion;
import com.android.print.PrintUtils;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.common.app.MyApp;
import com.haozi.idscaner2016.common.utils.SystemUtil;
import com.haozi.idscaner2016.constants.IActionIntent;

/**
 * Created by Haozi on 2016/6/6.
 */
public class PrinterHelper {

    private static boolean isConnected;
    private IPrinterOpertion myOpertion;
    private PrinterInstance mPrinter;
    private ProgressDialog dialog;
    private Handler mHandler;

    /**静态单例初始化*/
    private static final PrinterHelper INSTANCE = new PrinterHelper();
    /**单例静态引用*/
    public static PrinterHelper getInstance() {
        return INSTANCE;
    }

    public void showProgress(Context mContext){
        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Connecting...");
        dialog.setMessage("Please Wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
    }

    public void openConn(Context mContext){
        if(!isConnected){
            myOpertion = new BluetoothOperation(mContext, getHandler());
            myOpertion.chooseDevice();
        }else{
            unBindConnection();
        }
    }

    public void unBindConnection(){
        if(myOpertion != null){
            myOpertion.close();
        }
        myOpertion = null;
        mPrinter = null;
    }

    public Handler getHandler(){
        if(mHandler == null){
            mHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case PrinterConstants.Connect.SUCCESS:
                            isConnected = true;
                            mPrinter = myOpertion.getPrinter();
                            MyApp.getInstance().sendBroadcast(new Intent(IActionIntent.ACTION_PRINTER_CONNECTEDUPDATE));
                            break;
                        case PrinterConstants.Connect.FAILED:
                            isConnected = false;
                            DXToast.show( "连接失败...");
                            break;
                        case PrinterConstants.Connect.CLOSED:
                            isConnected = false;
                            DXToast.show( "连接已关闭...");
                            break;
                        default:
                            break;
                    }
                    //updateButtonState();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            };
        }
        return mHandler;
    }

    public void onActivityResult(Context mContext,final int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {
            case Constants.CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    showProgress(mContext);
                    new Thread(new Runnable(){
                        public void run() {
                            myOpertion.open(data);
                        }
                    }).start();
                }
                break;
            case Constants.ENABLE_BT:
                if (resultCode == Activity.RESULT_OK){
                    myOpertion.chooseDevice();
                }else{
                    DXToast.show( "蓝牙没有启动");
                }
        }
    }

    public boolean isPrinterConnected(){
        return isConnected;
    }

    public void printVisitCard(String checkCode){
        if(mPrinter != null){
            PrintUtils.printText(MyApp.getInstance().getResources(),mPrinter);
        }
    }

}
