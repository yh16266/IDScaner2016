package com.haozi.idscaner2016.client.ui.home;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haozi on 2016/5/4.
 */
public class RecordListViewAdapter extends BaseAdapter{

    List<VisitRecordEntity> mDataList = new ArrayList<>();

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
