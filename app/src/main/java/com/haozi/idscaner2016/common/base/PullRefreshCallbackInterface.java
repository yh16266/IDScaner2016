package com.haozi.idscaner2016.common.base;

import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;

/**
 * Created by Admin on 2016/4/5.
 */
public interface PullRefreshCallbackInterface {

    /**
     * 设置是否还在加载中
     * @return
     */
    public void setIsLoading(boolean isloading);

    /**
     * 获取列表引用
     * @return
     */
    public PullToRefreshAdapterViewBase getRefreshView();

    /**
     * 获取当前页数
     * @return
     */
    public int getNowPage();

    /**
     * 获取当前页数
     * @return
     */
    public void setNowPage(int nowPage);
}
