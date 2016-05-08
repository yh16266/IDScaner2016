package com.haozi.idscaner2016.client.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.bean.client.SystemCodeEntity;
import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.client.data.sqlite.SystemCodeTable;
import com.haozi.idscaner2016.client.utils.ViewUtils;
import com.haozi.idscaner2016.common.base.BasePageAdapter;
import com.haozi.idscaner2016.common.utils.DateUtil;
import com.haozi.idscaner2016.common.utils.StringUtil;

/**
 * Created by Haozi on 2016/5/4.
 */
public class UnitManageListViewAdapter extends BasePageAdapter<SystemCodeEntity>{

    private View.OnClickListener mListener;

    public UnitManageListViewAdapter(Context context){
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setmListener(View.OnClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected void refreshRowData(View convertView, int position) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if(holder == null || mData.get(position) == null){
            return;
        }
        SystemCodeEntity entity = mData.get(position);
        holder.txv_unitname.setText(entity.getName());
        holder.txv_time.setText(DateUtil.convertDateyyyyMMdd(entity.getCreateTime()));
        holder.txv_delete.setTag(position);
        holder.txv_edit.setTag(position);
        if(mListener != null){
            holder.txv_delete.setOnClickListener(mListener);
            holder.txv_edit.setOnClickListener(mListener);
        }
    }

    @Override
    protected View newItemView(ViewGroup parent) {
        View convertView = mInflater.inflate(R.layout.unit_manage_listview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.txv_unitname = (TextView) convertView.findViewById(R.id.txv_unitname);
        viewHolder.txv_time = (TextView) convertView.findViewById(R.id.txv_time);
        viewHolder.txv_delete = (TextView) convertView.findViewById(R.id.txv_delete);
        viewHolder.txv_edit = (TextView) convertView.findViewById(R.id.txv_edit);
        convertView.setTag(viewHolder);
        return convertView;
    }

    private class ViewHolder{
        TextView txv_unitname;
        TextView txv_time;
        TextView txv_delete;
        TextView txv_edit;
    }

}
