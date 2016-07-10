package com.haozi.idscaner2016.printer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.android.print.BluetoothOperation;
import com.android.print.Constants;
import com.android.print.IPrinterOpertion;
import com.android.print.PrintUtils;
import com.android.print.PrinterSettingActivity;
import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.client.biz.home.VisitRecordHelper;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.client.utils.BitmapUtil;
import com.haozi.idscaner2016.common.app.MyApp;
import com.haozi.idscaner2016.common.utils.StringUtil;
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

    public void printVisitCard(Context mcontext, VisitRecordEntity recordEntity){
        //跳转到打印页面
        boolean isConnected = PrinterHelper.getInstance().isPrinterConnected();
        if(isConnected == false){
            Intent intent = new Intent(mcontext,PrinterSettingActivity.class);
            mcontext.startActivity(intent);
        }else{
            //保存录入信息
            long recordId = VisitRecordHelper.getInstance().saveVisitInfo(recordEntity);
            //生成签离码
            String checkCode = VisitRecordHelper.getInstance().getCheckCode(recordId);
            PrinterHelper.getInstance().printVisitCard(checkCode);
            //发送清理信息通知
            mcontext.sendBroadcast(new Intent(IActionIntent.ACTION_INFO_CLEAN));
        }
    }

    public void printVisitCard(Context mcontext, long recordId){
        //跳转到打印页面
        boolean isConnected = PrinterHelper.getInstance().isPrinterConnected();
        if(isConnected == false){
            Intent intent = new Intent(mcontext,PrinterSettingActivity.class);
            mcontext.startActivity(intent);
        }else{
            //生成签离码
            String checkCode = VisitRecordHelper.getInstance().getCheckCode(recordId);
            PrinterHelper.getInstance().printVisitCard(checkCode);
        }
    }

    public void printVisitCard(String checkCode){
        if(mPrinter != null){
            VisitRecordEntity recordEntity = VisitRecordHelper.getInstance().getRecordByCheckCode(checkCode);
            if(recordEntity == null){
                DXToast.show("该单号无效");
                return;
            }
            mPrinter.init();
            printVisitInfo(recordEntity);
        }else{
            DXToast.show("请连接打印机");
        }
    }

    private void printVisitInfo(VisitRecordEntity recordEntity){
        mPrinter.printText("===========访客单==========");
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText("来访人："+recordEntity.getName());
        // 换行
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
        String idNumStr = recordEntity.getIdNum();
        if(!StringUtil.isEmpty(idNumStr) && idNumStr.length() > 7){
            if(idNumStr.length() - 7 > 6){
                idNumStr = idNumStr.substring(0,7)+"****";
            }else{
                idNumStr = idNumStr.substring(0,7)+"****"+idNumStr.substring(13);
            }
        }
        mPrinter.printText("身份证号："+idNumStr);
        // 换行
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
        mPrinter.printText("来访时间："+recordEntity.getVisitTimeStr());
        // 换行
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
        mPrinter.printText("来访事由："+recordEntity.getVisitReson());
        // 换行
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
        mPrinter.printText("来访单位："+recordEntity.getVisitUnit());
        // 换行
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
        mPrinter.printText("被访人："+recordEntity.getBeVisited());
        // 换行
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
        if(StringUtil.isEmpty(recordEntity.getVisitSign())){
            mPrinter.printText("签字：未签字");
        }else{
            Bitmap signimg = BitmapUtil.getScaleBitmap(recordEntity.getVisitSign(),400,400,0);
            if(signimg == null){
                mPrinter.printText("签字：签字文件丢失");
            }else{
                mPrinter.printImage(signimg);
            }
        }

        //打印访问码
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);
        printQrcode(recordEntity.getCheckCode());
    }

    private void printQrcode(String checkCode){
        if(StringUtil.isEmpty(checkCode)){
            return;
        }
        // "QRCODE"
        mPrinter.printText("单号："+checkCode);
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_NEWLINE);

        mPrinter.setCharacterMultiple(0, 0);
        /**设置左边距,nL,nH 设置宽度为(nL+nH*256)* 横向移动单位. 设置左边距对打印条码的注释位置有影响.*/
        mPrinter.setLeftMargin(15, 0);
        // mPrinter.setPrinter(BluetoothPrinter.COMM_ALIGN,BluetoothPrinter.COMM_ALIGN_LEFT);
        /**
         * 参数1: 设置条码横向宽度 2<=n<=6,默认为2 参数2: 设置条码高度 1<=n<=255,默认162 参数3:
         * 设置条码注释打印位置.0不打印,1上方,2下方,3上下方均有,默认为0 参数4:
         * 设置条码类型.BluetoothPrinter.BAR_CODE_TYPE_ 开头的常量,默认为CODE128
         */
        Barcode barcode = new Barcode(PrinterConstants.BarcodeType.QRCODE, 2, 3, 6, checkCode);
        mPrinter.printBarCode(barcode);
        // 换2行
        mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);

        //切刀
        mPrinter.cutPaper();
    }

}
