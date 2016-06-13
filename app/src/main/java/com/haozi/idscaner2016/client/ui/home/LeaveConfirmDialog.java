package com.haozi.idscaner2016.client.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.client.biz.home.VisitRecordHelper;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.common.utils.DateUtil;
import com.haozi.idscaner2016.common.utils.StringUtil;
import com.haozi.idscaner2016.constants.IActionIntent;

/**
 * Created by Haozi on 2016/5/7.
 */
public class LeaveConfirmDialog extends Dialog implements View.OnClickListener{

    private Context mContext;
    private String mIdNum;
    private String mCheckCode;
    private VisitRecordEntity mRecord;

    private TextView txv_time;
    private TextView txv_unit;
    private TextView txv_contractway;
    private TextView txv_carnum;
    private TextView txv_visiteto;
    private TextView txv_reson;

    /**
     * @param context
     */
    public LeaveConfirmDialog(Context context) {
        super(context, R.style.dx_dialog);
    }


    /**
     * @param context
     * @param theme
     */
    public LeaveConfirmDialog(Context context, int theme) {
        super(context, R.style.dx_dialog);
    }

    public LeaveConfirmDialog(Context context,String idNum,String checkCode) {
        super(context, R.style.dx_dialog);
        mContext = context;
        mIdNum = idNum;
        mCheckCode = checkCode;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置页面内容
        this.setContentView(R.layout.leave_pop);
        //区域外点击
        setCanceledOnTouchOutside(false);
        //可取消
        setCancelable(true);

        txv_time = (TextView) findViewById(R.id.txv_time);
        txv_unit = (TextView) findViewById(R.id.txv_unit);
        txv_contractway = (TextView) findViewById(R.id.txv_contractway);
        txv_carnum = (TextView) findViewById(R.id.txv_carnum);
        txv_visiteto = (TextView) findViewById(R.id.txv_visiteto);
        txv_reson = (TextView) findViewById(R.id.txv_reson);
        findViewById(R.id.button_confirm).setOnClickListener(this);
        findViewById(R.id.button_cancel).setOnClickListener(this);

        if(!StringUtil.isEmpty(mIdNum)){
            mRecord = VisitRecordHelper.getInstance().getRecordNotLeave(mIdNum);
        }else if(!StringUtil.isEmpty(mCheckCode)){
            mRecord = VisitRecordHelper.getInstance().getRecordByCheckCode(mCheckCode);
        }
        if(mRecord == null){
            DXToast.show("该人员并未进行来访登记");
            findViewById(R.id.button_confirm).setEnabled(false);
            dismiss();
        }else if(mRecord.getLeaveTime() > 0){
            DXToast.show("该人员已经登记离开，不能重复登记");
            findViewById(R.id.button_confirm).setEnabled(false);
            dismiss();
        }else{
            txv_time.setText(mRecord.getVisitTimeStr());
            txv_unit.setText(mRecord.getVisitUnit());
            txv_contractway.setText(mRecord.getVisitContract());
            txv_carnum.setText(mRecord.getVisitCarnum());
            txv_visiteto.setText(mRecord.getBeVisited());
            txv_reson.setText(mRecord.getVisitReson());
        }
    }

    public static LeaveConfirmDialog showByIdNum(Context context,String idNum){
        LeaveConfirmDialog dialog = new LeaveConfirmDialog(context,idNum,null);
        dialog.show();
        return dialog;
    }

    public static LeaveConfirmDialog showByCheckCode(Context context,String checkCode){
        LeaveConfirmDialog dialog = new LeaveConfirmDialog(context,null,checkCode);
        dialog.show();
        return dialog;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_confirm:
                VisitRecordHelper.getInstance().visiterLeave(mRecord.getId());
                mContext.sendBroadcast(new Intent(IActionIntent.ACTION_VISITOR_LEAVE));
                dismiss();
                break;
            case R.id.button_cancel:
                dismiss();
                break;
        }
    }
}
