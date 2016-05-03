package com.haozi.idscaner2016.client.ui.home;

import android.os.Bundle;
import android.view.View;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.biz.home.VisitRecordHelper;
import com.haozi.idscaner2016.client.data.sqlite.VisitRecordTable;
import com.haozi.idscaner2016.client.utils.ViewUtils;
import com.haozi.idscaner2016.common.base.BaseCompatActivity;
import com.haozi.idscaner2016.common.utils.DateUtil;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Haozi on 2016/5/2.
 */
public class RecordSumActivity extends BaseCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_sum_activity);
    }

    @Override
    protected void initView() {
        initToolbar("统计");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //设置日期
        ViewUtils.setTextViewTxt(this,R.id.txv_date, DateUtil.convertDateYYYYMMddHHmm(System.currentTimeMillis()));
        ViewUtils.setTextViewTxt(this,R.id.txv_week, DateUtil.getWeekDate(System.currentTimeMillis()));
        //读取统计数据
        initSumdate();
    }

    private void initSumdate(){
        ViewUtils.setTextViewTxt(this,R.id.txv_visitcount, VisitRecordHelper.getInstance().getVisitorCount()+"人");
        ViewUtils.setTextViewTxt(this,R.id.txv_leavecount, VisitRecordHelper.getInstance().getVisitorLeaveCount()+"人");
        ViewUtils.setTextViewTxt(this,R.id.txv_notleavecount, VisitRecordHelper.getInstance().getVisitorNotLeaveCount()+"人");
    }
}
