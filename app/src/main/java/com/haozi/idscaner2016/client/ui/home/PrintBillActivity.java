package com.haozi.idscaner2016.client.ui.home;

import android.os.Bundle;
import android.view.View;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.biz.home.VisitRecordHelper;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.common.base.BaseCompatActivity;
import com.haozi.idscaner2016.constants.IActionIntent;

/**
 * Created by Haozi on 2016/5/15.
 */
public class PrintBillActivity extends BaseCompatActivity{

    private long recordId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_bill_activity);
        recordId = getIntent().getLongExtra(IActionIntent.INTENTEXTRA_RECORDID,-1);
        if(recordId <= 0){
            DXToast.show("读取登记信息失败");
            finish();
        }
    }

    @Override
    protected void initView() {
        initToolbar("打印凭条");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //访客单打印被访人签名信息(外加条码)
        String checkCode = VisitRecordHelper.getInstance().getCheckCode(recordId);
        DXToast.show("条形码："+checkCode);
    }
}
