/**
 * 
 */
package com.haozi.idscaner2016.common.base;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haozi.idscaner2016.constants.IConstants;

/**
 * 类名：BasePageAcitivity
 * @author yinhao
 * @功能
 * @创建日期 2015年12月10日 上午11:33:43
 * @备注 [修改者，修改日期，修改内容]
 */
public abstract class PullRefreshAcitivity<T> extends BaseCompatActivity implements PullRefreshCallbackInterface,PullToRefreshBase.OnRefreshListener2<ListView>,OnItemClickListener{

	/**当前页数*/
	protected int mNowPage = IConstants.PAGE_START_INDEX;
	/**列表*/
	protected PullToRefreshListView mListview;
	/**列表适配器*/
	protected BasePageAdapter<T> mAdapter;
	/**是否在加载中*/
	protected boolean isLoadingData = false;

	protected void initListview(int listviewId, BasePageAdapter<T> adapter){
		initListview(listviewId,adapter,true);
	}

	protected void initListview(int listviewId, BasePageAdapter<T> adapter, boolean isInitData){
		//获取列表
		mListview = (PullToRefreshListView) findViewById(listviewId);
		mListview.setMode(PullToRefreshBase.Mode.BOTH);
		mListview.setOnRefreshListener(this);
		//初始化hanlder
		this.mAdapter = adapter;
		//请求第一页数据
		mNowPage = IConstants.PAGE_START_INDEX;
		if(isInitData){
			refreshListView(mNowPage);
		}
		//设置适配器
		mListview.setAdapter(mAdapter);
		//设置滚动监听
		mListview.setOnItemClickListener(this);
	}

	protected abstract void refreshListView(int index);

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		//设置加载
		if(this.isLoadingData == true){
			return;
		}
		this.isLoadingData = true;
		//加载数据
		mNowPage = IConstants.PAGE_START_INDEX;
		refreshListView(mNowPage);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		//设置加载
		if(this.isLoadingData == true){
			return;
		}
		this.isLoadingData = true;
		//加载数据
		refreshListView(mNowPage +1);
	}

	public void setIsLoading(boolean isloading){
		this.isLoadingData = isloading;
	}

	/**
	 * @return
	 */
	public PullToRefreshListView getRefreshView() {
		return mListview;
	}
	
	public int getNowPage(){
		return this.mNowPage;
	}

	@Override
	public void setNowPage(int nowPage) {
		this.mNowPage = nowPage;
	}
}
