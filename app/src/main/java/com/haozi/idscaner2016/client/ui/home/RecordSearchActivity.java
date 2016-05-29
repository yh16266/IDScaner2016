package com.haozi.idscaner2016.client.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.client.biz.home.VisitRecordHelper;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.client.data.sqlite.VisitRecordTable;
import com.haozi.idscaner2016.client.utils.ViewUtils;
import com.haozi.idscaner2016.common.base.BaseCompatActivity;
import com.haozi.idscaner2016.common.base.PullRefreshAcitivity;
import com.haozi.idscaner2016.constants.IConstants;

import java.util.List;

/**
 * Created by Haozi on 2016/5/2.
 */
public class RecordSearchActivity extends PullRefreshAcitivity<VisitRecordEntity> {

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
        findViewById(R.id.btn_output).setOnClickListener(this);
        initListview(R.id.listView,new RecordListViewAdapter(this));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_search:
                searchRecord();
                break;
            case R.id.btn_clean:
                ViewUtils.setTextViewTxt(this,R.id.edt_visittime,"");
                ViewUtils.setEditTextTxt(this,R.id.edt_idnum,"");
                ViewUtils.setEditTextTxt(this,R.id.edt_visitorname,"");
                ViewUtils.setEditTextTxt(this,R.id.edt_bevisited,"");
                ViewUtils.setEditTextTxt(this,R.id.edt_carnum,"");
                break;
            case R.id.btn_output:
                break;
        }
    }

    public void searchRecord(){
        refreshListView(IConstants.PAGE_START_INDEX);
    }

    @Override
    protected void refreshListView(int index) {
        String date = ViewUtils.getEditString(this,R.id.edt_visittime);
        String idnum = ViewUtils.getEditString(this,R.id.edt_idnum);
        String visitorname = ViewUtils.getEditString(this,R.id.edt_visitorname);
        String bevisited = ViewUtils.getEditString(this,R.id.edt_bevisited);
        String carnum = ViewUtils.getEditString(this,R.id.edt_carnum);
        List<VisitRecordEntity> list = VisitRecordTable.getInstance()
                .getRecordList(date,idnum,visitorname,bevisited,carnum,index);
        mListview.onRefreshComplete();
        if(list != null && list.size() > 0){
            setNowPage(index);
        }
        if(index == IConstants.PAGE_START_INDEX){
            mAdapter.setDataList(list);
        }else{
            mAdapter.addDataList(list);
        }
    }

    private void outputRecord(){
        showProgress("查询数据中...");
        String date = ViewUtils.getEditString(this,R.id.edt_visittime);
        String idnum = ViewUtils.getEditString(this,R.id.edt_idnum);
        String visitorname = ViewUtils.getEditString(this,R.id.edt_visitorname);
        String bevisited = ViewUtils.getEditString(this,R.id.edt_bevisited);
        String carnum = ViewUtils.getEditString(this,R.id.edt_carnum);
        List<VisitRecordEntity> list = VisitRecordTable.getInstance()
                .getRecordList(date,idnum,visitorname,bevisited,carnum,-1);
        if(list == null || list.size() == 0){
            dismissProgress();
            DXToast.show("没有可到处数据");
            return;
        }
        showProgress("导出数据中...");
        VisitRecordHelper.getInstance().outputRecord(list);
        dismissProgress();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
