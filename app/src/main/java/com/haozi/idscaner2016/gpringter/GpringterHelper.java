package com.haozi.idscaner2016.gpringter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.io.GpDevice;
import com.gprinter.printer.PrinterConnectDialog;
import com.gprinter.printer.Util;
import com.gprinter.service.GpPrintService;
import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.common.app.MyApp;

import org.apache.commons.lang.ArrayUtils;

import java.util.Vector;

/**
 * Created by Haozi on 2016/5/18.
 */
public class GpringterHelper {

    private GpService mGpService = null;
    public static final String CONNECT_STATUS = "connect.status";
    private static final String DEBUG_TAG = "GprinterMainActivity";
    private PrinterServiceConnection conn = null;
    private int mPrinterIndex = 0;
    private int mTotalCopies1 = 0, mTotalCopies2 = 0, mTotalCopies3 = 0;

    /**静态单例初始化*/
    private static final GpringterHelper INSTANCE = new GpringterHelper();
    /**单例静态引用*/
    public static GpringterHelper getInstance() {
        return INSTANCE;
    }


    public void connection(Activity mContext) {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent("com.gprinter.aidl.GpPrintService");
        mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    public boolean[] getConnectState() {
        boolean[] state = new boolean[GpPrintService.MAX_PRINTER_CNT];
        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
            state[i] = false;
        }
        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
            try {
                if (mGpService.getPrinterConnectStatus(i) == GpDevice.STATE_CONNECTED) {
                    state[i] = true;
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return state;
    }

    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ServiceConnection", "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
        }
    }

    public void unBindConnection(Activity mContext){
        if (conn != null) {
            mContext.unbindService(conn); // unBindService
        }
    }

    public boolean getPrinterConnectStatusClicked() {
        boolean statuRst = false;
        try {
            int status = mGpService.getPrinterConnectStatus(0);
            String str = new String();
            if (status == GpDevice.STATE_CONNECTED) {
                str = "打印机已连接";
                statuRst = true;
            } else {
                str = "打印机断开";
            }
            DXToast.show("打印机：" + mPrinterIndex + " 连接状态：" + str);
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return statuRst;
    }

    public boolean getPrinterStatusClicked() {
        boolean rst = false;
        try {
            mTotalCopies1 = 0;
            mTotalCopies2 = 0;
            mTotalCopies3 = 0;
            int status = mGpService.queryPrinterStatus(mPrinterIndex, 500);
            String str = new String();
            if (status == GpCom.STATE_NO_ERR) {
                str = "打印机正常";
                rst = true;
            } else {
                str = "打印机 ";
                if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
                    str += "脱机";
                }
                if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
                    str += "缺纸";
                }
                if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
                    str += "打印机开盖";
                }
                if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
                    str += "打印机出错";
                }
                if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {
                    str += "查询超时";
                }
                DXToast.show("打印机：" + mPrinterIndex + " 状态：" + str);
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
        return rst;
    }

    public void openPortDialogueClicked(Activity mContext) {
        Log.d(DEBUG_TAG, "openPortConfigurationDialog ");
        Intent intent = new Intent(mContext,PrinterConnectDialog.class);
        boolean[] state = getConnectState();
        intent.putExtra(CONNECT_STATUS, state);
        mContext.startActivity(intent);
    }

    public void sendReceipt() {
        EscCommand esc = new EscCommand();
        esc.addPrintAndFeedLines((byte) 3);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);//设置打印居中
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);//设置为倍高倍宽
        esc.addText("Sample\n");   //  打印文字
        esc.addPrintAndLineFeed();

		/*打印文字*/
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);//取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);//设置打印左对齐
        esc.addText("Print text\n");   //  打印文字
        esc.addText("Welcome to use Gprinter!\n");   //  打印文字

		/*打印繁体中文  需要打印机支持繁体字库*/
        String message = Util.SimToTra("佳博票据打印机\n");
        esc.addText(message, "BIG5");
        esc.addPrintAndLineFeed();

		/*打印图片*/
        esc.addText("Print bitmap!\n");   //  打印文字
        Bitmap b = BitmapFactory.decodeResource(MyApp.getInstance().getResources(), R.mipmap.gprinter);
        esc.addRastBitImage(b, b.getWidth(), 0);   //打印图片

		/*打印一维条码*/
        esc.addText("Print code128\n");   //  打印文字
        esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);//设置条码可识别字符位置在条码下方
        esc.addSetBarcodeHeight((byte) 60); //设置条码高度为60点
        esc.addCODE128("Gprinter");  //打印Code128码
        esc.addPrintAndLineFeed();

		/*QRCode命令打印
			此命令只在支持QRCode命令打印的机型才能使用。
			在不支持二维码指令打印的机型上，则需要发送二维条码图片
		*/
//		esc.addText("Print QRcode\n");   //  打印文字
//		esc.addSelectErrorCorrectionLevelForQRCode((byte)0x31); //设置纠错等级
//		esc.addSelectSizeOfModuleForQRCode((byte)3);//设置qrcode模块大小
//		esc.addStoreQRCodeData("www.gprinter.com.cn");//设置qrcode内容
//		esc.addPrintQRCode();//打印QRCode
//		esc.addPrintAndLineFeed();

		/*打印文字*/
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);//设置打印左对齐
        esc.addText("Completed!\r\n");   //  打印结束
        esc.addPrintAndFeedLines((byte) 8);

        Vector<Byte> datas = esc.getCommand(); //发送数据
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = ArrayUtils.toPrimitive(Bytes);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel;
        try {
            rel = mGpService.sendEscCommand(mPrinterIndex, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                DXToast.show( GpCom.getErrorText(r));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
