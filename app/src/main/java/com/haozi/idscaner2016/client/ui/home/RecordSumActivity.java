package com.haozi.idscaner2016.client.ui.home;

import android.os.Bundle;
import android.view.View;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.common.base.BaseCompatActivity;

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
    }
}
