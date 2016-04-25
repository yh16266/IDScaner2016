package com.haozi.idscaner2016.common.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 类名：BaseService
 * @author Yang.Gao
 * 涂鸦
 * 创建日期�?013�?2�?�?
 * [修改者，修改日期，修改内容]
 */
public class BaseService extends Service {
	protected String TAG = getClass().getSimpleName();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
