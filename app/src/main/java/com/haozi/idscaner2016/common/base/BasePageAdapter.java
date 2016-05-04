/**
 * 
 */
package com.haozi.idscaner2016.common.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名：BasePageAdapter
 * @author YH
 * 创建日期：2015年12月12日
 * [修改者，修改日期，修改内容]
 */
public abstract class BasePageAdapter<T> extends BaseAdapter{

	protected LayoutInflater mInflater;
	protected Context mContext;
	protected List<T> mData = new ArrayList<T>();
	
	/**
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mData.size();
	}

	/**
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	/**
	 * @see android.widget.Adapter#getItem(int)
	 */
	public T getItemEntity(int position) {
		return mData.get(position);
	}
	
	/**
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * @see android.widget.Adapter#getView(int, View, ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 获取itemview
		if (convertView == null) {
			convertView = newItemView(parent);
		}
		//检查数据
		if(mData==null || mData.isEmpty() || position<0 || position>= mData.size()){
			return convertView;
		}
		//组装数据
		refreshRowData(convertView, position);
		//返回页面
		return convertView;
	}
	
	/**
	 * @param convertView
	 * @param position
	 */
	protected abstract void refreshRowData(View convertView, int position);

	/**
	 * @return
	 */
	protected abstract View newItemView(ViewGroup parent);
	
	public void addData(T data){
		this.addData(data, true);
	}

	public void addData(T data,boolean isReplace){
		if(data == null){
			return;
		}
		if(isReplace == true && this.mData.contains(data)){
			int index = this.mData.indexOf(data);
			this.mData.set(index, data);
		}else{
			this.mData.add(data);
		}
		notifyDataSetChanged();
	}
	
	public void addDataFirst(T data){
		if(data == null){
			return;
		}
		if(this.mData.contains(data)){
			this.mData.remove(data);
		}
		this.mData.add(0,data);
		notifyDataSetChanged();
	}
	
	public void addDataList(List<T> data){
		addDataList(data, true);
	}
	
	public void addDataList(List<T> data,boolean isReresh){
		synchronized (this) {
			if(data != null && data.size() > 0){
				this.mData.addAll(data);
			}
			if(isReresh == true){
				notifyDataSetChanged();
			}
		}
	}

	public void setDataList(List<T> data){
		setDataList(data, true);
	}
	
	public void setDataList(List<T> data,boolean isReresh){
		this.mData.clear();
		if(isReresh == true){
			notifyDataSetChanged();
		}
		if(data != null && data.size() > 0){
			this.mData.addAll(data);
		}
		if(isReresh == true){
			notifyDataSetChanged();
		}
	}

	public void addAllInHead(List<T> list){
		this.mData.addAll(0, list);
	}
	
	/**
	 * 删除指定位置的item  
	 * */
	public void remove(int index) {
		remove(index, true);
	}
	
	/**
	 * 删除指定位置的item  
	 * */
	public void remove(int index,boolean isReresh) {
		mData.remove(index);
        //不要忘记更改适配器对象的数据源  
		if(isReresh == true){
			notifyDataSetChanged();
		}
    }  
    
	/**
	 * 在指定位置插入item  
	 * */
	public void insert(T item, int index) {
		insert(item, index, true);
	}
	
	/**
	 * 在指定位置插入item  
	 * */
    public void insert(T item, int index,boolean isReresh) {
    	mData.add(index, item);
    	if(isReresh == true){
			notifyDataSetChanged();
		}
    }
	
    public void clear() {
    	clear(true);
    }

    public void clear(boolean isReresh) {
		this.mData.clear();
		if(isReresh == true){
			notifyDataSetChanged();
		}
	}
}
