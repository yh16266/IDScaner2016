package com.haozi.idscaner2016.client.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.common.base.BasePageAdapter;
import com.haozi.idscaner2016.common.utils.StringUtil;

/**
 * Created by Haozi on 2016/5/4.
 */
public class RecordListViewAdapter extends BasePageAdapter<VisitRecordEntity> {

    public RecordListViewAdapter(Context context){
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    protected void refreshRowData(View convertView, int position) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if(holder == null || mData.get(position) == null){
            return;
        }
        VisitRecordEntity entity = mData.get(position);
        holder.txv_name.setText(entity.getName());
        holder.txv_sex.setText(entity.getSex());
        holder.txv_idnum.setText(entity.getIdNum());
        holder.txv_visitetime.setText(entity.getVisitTimeStr());
        holder.txv_contract.setText(entity.getVisitContract());
        holder.txv_carnum.setText(entity.getVisitCarnum());
        holder.txv_visiteunit.setText(entity.getVisitUnit());
        holder.txv_reson.setText(entity.getVisitReson());
        holder.txv_bevisited.setText(entity.getBeVisited());
        if(StringUtil.isEmpty(entity.getCheckCode())){
            holder.txv_printstatu.setText("未打印");
        }else{
            holder.txv_printstatu.setText("已打印（条形码："+entity.getCheckCode()+"）");
        }
        if(entity.getLeaveTime() <= 0){
            holder.txv_leavetime.setText("未离开");
        }else{
            holder.txv_leavetime.setText(entity.getLeaveTimeStr());
        }
    }

    @Override
    protected View newItemView(ViewGroup parent) {
        View convertView = mInflater.inflate(R.layout.record_listview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.txv_name = (TextView) convertView.findViewById(R.id.txv_name);
        viewHolder.txv_sex = (TextView) convertView.findViewById(R.id.txv_sex);
        viewHolder.txv_idnum = (TextView) convertView.findViewById(R.id.txv_idnum);
        viewHolder.txv_visitetime = (TextView) convertView.findViewById(R.id.txv_visitetime);
        viewHolder.txv_contract = (TextView) convertView.findViewById(R.id.txv_contract);
        viewHolder.txv_carnum = (TextView) convertView.findViewById(R.id.txv_carnum);
        viewHolder.txv_visiteunit = (TextView) convertView.findViewById(R.id.txv_visiteunit);
        viewHolder.txv_reson = (TextView) convertView.findViewById(R.id.txv_reson);
        viewHolder.txv_bevisited = (TextView) convertView.findViewById(R.id.txv_bevisited);
        viewHolder.txv_printstatu = (TextView) convertView.findViewById(R.id.txv_printstatu);
        viewHolder.txv_leavetime = (TextView) convertView.findViewById(R.id.txv_leavetime);
        convertView.setTag(viewHolder);
        return convertView;
    }

    private class ViewHolder{
        TextView txv_name;
        TextView txv_sex;
        TextView txv_idnum;
        TextView txv_visitetime;
        TextView txv_contract;
        TextView txv_carnum;
        TextView txv_visiteunit;
        TextView txv_reson;
        TextView txv_bevisited;
        TextView txv_printstatu;
        TextView txv_leavetime;
    }
}
