/**
 * 
 */
package com.haozi.idscaner2016.client.control;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.utils.BitmapUtil;
import com.haozi.idscaner2016.common.base.BaseActivity;
import com.haozi.idscaner2016.common.utils.StringUtil;
import com.haozi.idscaner2016.constants.IActionIntent;

/**
 * 类名：DXImageDisplayActivity
 * @author yinhao
 * @功能
 * @创建日期 2015年12月23日 上午11:34:27
 * @备注 [修改者，修改日期，修改内容]
 */
public class DXImageDisplayActivity extends BaseActivity {

	private ImageView img_display;
	private String mImgUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dx_image_view_activity);
		mImgUrl = getIntent().getStringExtra(IActionIntent.INTENTEXTRA_IMG_URL);
		initView();
	}
	
	private void initView(){
		//隐藏左边功能键
		setTitle("图片查看");
		img_display = (ImageView) findViewById(R.id.img_display);
		if(!StringUtil.isEmpty(mImgUrl)){
			Bitmap img = BitmapUtil.getScaleBitmap(mImgUrl,800,600,0);
			img_display.setImageBitmap(img);
		}else{
			img_display.setImageResource(R.drawable.img_default);
		}

	}
	
}
