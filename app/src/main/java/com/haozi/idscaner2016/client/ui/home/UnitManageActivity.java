package com.haozi.idscaner2016.client.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.bean.client.SystemCodeEntity;
import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.client.biz.home.UnityManageHelper;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.client.data.sqlite.SystemCodeTable;
import com.haozi.idscaner2016.client.data.sqlite.VisitRecordTable;
import com.haozi.idscaner2016.client.utils.ViewUtils;
import com.haozi.idscaner2016.common.base.PullRefreshAcitivity;
import com.haozi.idscaner2016.common.utils.StringUtil;
import com.haozi.idscaner2016.constants.IConstants;

import java.util.List;

/**
 * Created by Haozi on 2016/5/2.
 */
public class UnitManageActivity extends PullRefreshAcitivity<SystemCodeEntity> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerHandler();
        setContentView(R.layout.unit_manage_activity);
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
        initListview(R.id.listView,new UnitManageListViewAdapter(this));
        mListview.setMode(PullToRefreshBase.Mode.DISABLED);
        ((UnitManageListViewAdapter)mAdapter).setmListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.unitymanage_title_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                showAddDialog();
                break;
            default:
                break;
        }
        return true;
    }

    private void showAddDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("编辑");
        final EditText editText = new EditText(this);
        editText.setHint("请输入单位名");
        builder.setView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String unitname = ViewUtils.getEditString(editText);
                if(StringUtil.isEmpty(unitname)){
                    DXToast.show("单位名称不能为空，修改失败");
                }else{
                    UnityManageHelper.getInstance().addUnit(unitname);
                    mListview.setRefreshing();
                    DXToast.show("添加成功");
                }
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void deleteItem(View view){
        if(view.getTag() == null){
            return;
        }
        final int position = (int) view.getTag();
        if(position < 0){
            return;
        }
        final long entityId = mAdapter.getItemEntity(position).getId();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否删除该记录?"); //设置内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SystemCodeTable.getInstance().deleteRecord(entityId);
                mAdapter.remove(position);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void updateItem(View view){
        if(view.getTag() == null){
            return;
        }
        final int position = (int) view.getTag();
        if(position < 0){
            return;
        }
        final SystemCodeEntity entity = mAdapter.getItemEntity(position);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("编辑");
        final EditText editText = new EditText(this);
        editText.setText(entity.getName());
        builder.setView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String content = ViewUtils.getEditString(editText);
                if(StringUtil.isEmpty(content)){
                    DXToast.show("单位名称不能为空，修改失败");
                }else{
                    entity.setName(content);
                    SystemCodeTable.getInstance().updateRecordById(entity);
                }
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_search:
                searchRecord();
                break;
            case R.id.btn_clean:
                ViewUtils.setTextViewTxt(this,R.id.edt_unitname,"");
                break;
            case R.id.txv_edit:
                updateItem(v);
                break;
            case R.id.txv_delete:
                deleteItem(v);
                break;
        }
    }

    public void searchRecord(){
        refreshListView(IConstants.PAGE_START_INDEX);
    }

    @Override
    protected void refreshListView(int index) {
        String unitName = ViewUtils.getEditString(this,R.id.edt_unitname);
        List<SystemCodeEntity> list = UnityManageHelper.getInstance().getRecordListByType(unitName);
        mAdapter.setDataList(list);
        mMainHandler.sendEmptyMessageDelayed(1000,1000);
        //if(list != null && list.size() > 0){
        //    setNowPage(index);
        //}
        //if(index == IConstants.PAGE_START_INDEX){
        //    mAdapter.setDataList(list);
        //}else{
        //    mAdapter.addDataList(list);
        //}
    }

    @Override
    public boolean handleMessage(Message msg) {
        mListview.onRefreshComplete();
        return super.handleMessage(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
