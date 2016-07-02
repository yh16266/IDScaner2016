package com.haozi.idscaner2016.client.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.bean.client.Accounts;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.client.data.sqlite.UserTable;
import com.haozi.idscaner2016.client.utils.ViewUtils;
import com.haozi.idscaner2016.common.base.BaseCompatActivity;
import com.haozi.idscaner2016.common.utils.StringUtil;

import java.util.List;

/**
 * Created by Haozi on 2016/7/2.
 */
public class UserSettingActivity extends BaseCompatActivity{

    private Spinner spinner_user;
    private ArrayAdapter spinnerAdapter;
    private List<Accounts> userlist;
    private Accounts selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting_actvity);
    }

    @Override
    protected void initView() {
        findViewById(R.id.button_submit).setOnClickListener(this);
        spinner_user=(Spinner)findViewById(R.id.spinner_user);
        userlist = UserTable.getInstance().getRecordList();
        String[] nameList = new String[userlist.size()];
        spinnerAdapter= new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nameList);
        spinner_user.setAdapter(spinnerAdapter);
        spinner_user.setOnItemSelectedListener(new MyOnItemSelectedListener());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.button_submit:
                submit();
                break;
        }
    }

    private class MyOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            selectedUser = userlist.get(position);
            ViewUtils.setEditTextTxt(UserSettingActivity.this,R.id.edt_username,selectedUser.getNickname());
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    private void submit(){
        if(selectedUser == null || selectedUser.getId() <= 0){
            DXToast.show("请选择修改目标用户");
            return;
        }
        String pswold = ViewUtils.getEditString(this,R.id.edt_password);
        if(StringUtil.isEmpty(pswold)){
            DXToast.show("请输入当前密码");
            return;
        }
        String pswnew = ViewUtils.getEditString(this,R.id.edt_password_new);
        if(StringUtil.isEmpty(pswnew)){
            DXToast.show("请输入新密码");
            return;
        }
        String pswconfirm = ViewUtils.getEditString(this,R.id.edt_password_confirm);
        if(StringUtil.isEmpty(pswconfirm)){
            DXToast.show("请再次确认新密码");
            return;
        }
        if(!pswnew.equals(pswconfirm)){
            DXToast.show("两次密码不一致，请重新输入");
            return;
        }
        if(!pswold.equals(selectedUser.getPassword())){
            DXToast.show("当前密码不正确");
            return;
        }
        selectedUser.setPassword(pswconfirm);
        UserTable.getInstance().updateRecord(selectedUser);
        DXToast.show("修改密码成功！");
    }
}
