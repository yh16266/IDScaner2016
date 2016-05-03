package com.haozi.idscaner2016.client.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.client.data.sqlite.VisitRecordTable;
import com.haozi.idscaner2016.common.base.BaseCompatActivity;

import java.util.List;

/**
 * Created by Haozi on 2016/5/2.
 */
public class RecordSearchActivity extends BaseCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_search_activity);
    }

    @Override
    protected void initView() {
        initToolbar("搜索");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.btn_search).setOnClickListener(this);
        findViewById(R.id.btn_clean).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_search:

                break;
            case R.id.btn_clean:

                break;
        }
    }

    public void searchRecord(){
        List<VisitRecordEntity> list = VisitRecordTable.getInstance().getRecordList(0);
    }
}
