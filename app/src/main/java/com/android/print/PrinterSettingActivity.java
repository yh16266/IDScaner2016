package com.android.print;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.common.base.BaseCompatActivity;
import com.haozi.idscaner2016.constants.IActionIntent;
import com.haozi.idscaner2016.printer.PrinterHelper;

/**
 * Created by Haozi on 2016/6/7.
 */
public class PrinterSettingActivity extends BaseCompatActivity {

    private Button btn_connect;

    private RadioButton paperWidth_58;
    private RadioButton paperWidth_80;

    private RadioButton printer_type_remin;
    private RadioButton printer_type_styuls;

    private boolean is58mm = true;
    private boolean isStylus = false;

    private BroadcastReceiver mBroadcastRecevier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.printer_setting_activity);
        registerBroadCastReciver();
    }

    @Override
    protected void initView() {
        initToolbar("打印机设置");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(this);

        paperWidth_58 = (RadioButton) findViewById(R.id.width_58mm);
        paperWidth_58.setOnClickListener(this);
        paperWidth_80 = (RadioButton) findViewById(R.id.width_80mm);
        paperWidth_80.setOnClickListener(this);

        printer_type_remin = (RadioButton) findViewById(R.id.type_remin);
        printer_type_remin.setOnClickListener(this);
        printer_type_styuls = (RadioButton) findViewById(R.id.type_styuls);
        printer_type_styuls.setOnClickListener(this);

        updateButtonState();
    }

    private void updateButtonState(){
        if(!PrinterHelper.getInstance().isPrinterConnected()){
            String connStr = getResources().getString(R.string.connect);
            connStr = String.format(connStr, "蓝牙");
            btn_connect.setText(connStr);
        }else{
            btn_connect.setText(R.string.disconnect);
            DXToast.show("连接打印机成功");
            if(mConnectPriterCallback != null){
                mConnectPriterCallback.connectSuccess();
            }
            finish();
        }
    }

    public interface ConnectPriterCallback{
        public void connectSuccess();
    }

    private static ConnectPriterCallback mConnectPriterCallback;

    public static void setPrinterConnectCallback(ConnectPriterCallback callback){
        mConnectPriterCallback = callback;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PrinterHelper.getInstance().onActivityResult(this,requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_connect:
                PrinterHelper.getInstance().openConn(this);
                break;
            case R.id.width_58mm:
            case R.id.width_80mm:
                is58mm = v == paperWidth_58;
                paperWidth_58.setChecked(is58mm);
                paperWidth_80.setChecked(!is58mm);
                break;
            case R.id.type_remin:
            case R.id.type_styuls:
                isStylus = v == printer_type_remin;
                printer_type_remin.setChecked(isStylus);
                printer_type_styuls.setChecked(!isStylus);
                break;
        }
    }

    private void registerBroadCastReciver(){
        mBroadcastRecevier = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(IActionIntent.ACTION_PRINTER_CONNECTEDUPDATE.equals(intent.getAction())){
                    updateButtonState();
                }
            }
        };
        IntentFilter ift = new IntentFilter(IActionIntent.ACTION_PRINTER_CONNECTEDUPDATE);
        registerReceiver(mBroadcastRecevier,ift);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBroadcastRecevier != null){
            unregisterReceiver(mBroadcastRecevier);
        }
    }
}
