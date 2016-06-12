package com.haozi.idscaner2016.client.ui.home;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.bean.EntityDitionary;
import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.client.biz.home.VisitRecordHelper;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.client.data.AccountHelper;
import com.haozi.idscaner2016.client.data.sqlite.VisitRecordTable;
import com.haozi.idscaner2016.client.utils.ViewUtils;
import com.haozi.idscaner2016.common.base.BaseCompatActivity;
import com.haozi.idscaner2016.common.base.PullRefreshAcitivity;
import com.haozi.idscaner2016.common.utils.DateUtil;
import com.haozi.idscaner2016.common.utils.StringUtil;
import com.haozi.idscaner2016.constants.IConstants;
import com.haozi.idscaner2016.printer.PrinterHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Haozi on 2016/5/2.
 */
public class RecordSearchActivity extends PullRefreshAcitivity<VisitRecordEntity> {

    private ListView listView;
    private TextView edt_visittime;
    private DatePickerDialog mDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_search_activity);
    }

    @Override
    protected void initView() {
        initToolbar("搜索");
        edt_visittime = (TextView) findViewById(R.id.edt_visittime);
        if(EntityDitionary.UserType.admin != AccountHelper.getInstance().getMyinfo().UserType()) {
            edt_visittime.setText(DateUtil.convertDateyyyyMMdd(System.currentTimeMillis()));
        }
        edt_visittime.setOnClickListener(this);
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
                if(EntityDitionary.UserType.admin == AccountHelper.getInstance().getMyinfo().UserType()){
                    ViewUtils.setTextViewTxt(this,R.id.edt_visittime,"");
                }
                ViewUtils.setEditTextTxt(this,R.id.edt_idnum,"");
                ViewUtils.setEditTextTxt(this,R.id.edt_visitorname,"");
                ViewUtils.setEditTextTxt(this,R.id.edt_bevisited,"");
                ViewUtils.setEditTextTxt(this,R.id.edt_carnum,"");
                break;
            case R.id.btn_output:
                outputRecord();
                break;
            case R.id.edt_visittime:
                showDatePicker();
                break;
        }
    }

    public void searchRecord(){
        refreshListView(IConstants.PAGE_START_INDEX);
    }

    @Override
    protected void refreshListView(int index) {
        String date = ViewUtils.getTextViewString(this,R.id.edt_visittime);
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
            DXToast.show("没有可导出数据");
            return;
        }
        showProgress("导出数据中...");
        VisitRecordHelper.getInstance().outputRecord(list);
        dismissProgress();
        DXToast.show("导出成功");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //VisitRecordEntity entity =  mAdapter.getItemEntity(position-1);
        //PrinterHelper.getInstance().printVisitCard(this,entity.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.record_search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_output:
                outputRecord();
                break;
            default:
                break;
        }
        return true;
    }

    private void showDatePicker(){
        Calendar nowCalendar=Calendar.getInstance(Locale.CHINA);
        final String dateStr = ViewUtils.getTextViewString(edt_visittime);
        if(StringUtil.isEmpty(dateStr) && dateStr.length() == 10){
            Date date = DateUtil.StrYYYYMMMDDToDate(dateStr);
            nowCalendar.setTime(date);
        }
        if(mDatePicker == null){
            //创建DatePickerDialog对象
            mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    String monthStr = String.valueOf(month+1);
                    if((month+1) < 10){
                        monthStr = "0"+monthStr;
                    }
                    String dayStr = String.valueOf(day);
                    if(day < 10){
                        dayStr = "0"+dayStr;
                    }
                    edt_visittime.setText(year+"-"+monthStr+"-"+dayStr);
                }
            }, nowCalendar.get(Calendar.YEAR), nowCalendar.get(Calendar.MONTH), nowCalendar.get(Calendar.DAY_OF_MONTH));
        }
        //显示DatePickerDialog组件
        mDatePicker.show();
    }
}
