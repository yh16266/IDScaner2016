package com.haozi.idscaner2016.client.ui.home;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.Routon.iDR410SDK.SignatureView;
import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.common.base.BaseCompatActivity;
import com.haozi.idscaner2016.common.utils.DateUtil;
import com.haozi.idscaner2016.common.utils.StringUtil;
import com.haozi.idscaner2016.constants.IActionIntent;
import com.haozi.idscaner2016.constants.IConstants;

import java.io.File;

public class SignActivity extends BaseCompatActivity implements OnClickListener {
	private static final String[] mPenStyle = {"铅笔", "钢笔", "毛笔", "蜡笔"};
	private static final String[] mPenColor = {"黑色", "蓝色", "红色", "绿色", "紫色"};
	private static final String[] mPenWidth = {"细", "中等", "粗"};
	private static final String[] mBackgroud = {"虚线边框", "无背景", "有背景图片"};
	
	private LinearLayout mLayoutSignature;
	private SignatureView mSignature;//签名区域框框
	
	private ArrayAdapter<String> mAdapter;
	private Spinner mSpinnerStyle;
	private Spinner mSpinnerColor;
	private Spinner mSpinnerWidth;
	private Spinner mSpinnerBackgroud;
	
	private int mStyle;
	private int mColorType;
	private int mWidth;
	
	private Button mButtonCleanSignature;
	private Button mButtonSaveSignature;
	private Button mButtonCancelSignature;

	private String mIDNum;

	private SignatureView signatureView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mIDNum = getIntent().getStringExtra(IActionIntent.INTENTEXTRA_IDNUM);
		if(StringUtil.isEmpty(mIDNum)){
			DXToast.show("请先扫描身份证，然后再签字登记！");
			finish();
		}

		setContentView(R.layout.sign_activity);
		
		mLayoutSignature = (LinearLayout)findViewById(R.id.imageViewSignature);
		
		mSpinnerStyle = (Spinner)findViewById(R.id.spinner_style);
		mSpinnerColor = (Spinner)findViewById(R.id.spinner_color);
		mSpinnerWidth = (Spinner)findViewById(R.id.spinner_width);
		mSpinnerBackgroud = (Spinner)findViewById(R.id.spinner_backgroud);
        
    	mButtonCleanSignature = (Button)findViewById(R.id.buttonClean_signature);
        mButtonSaveSignature = (Button)findViewById(R.id.buttonSave_signature);
        mButtonCancelSignature = (Button)findViewById(R.id.buttonCancel_signature);
        
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mPenStyle);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerStyle.setAdapter(mAdapter);
        mSpinnerStyle.setSelection(1);
        
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mPenColor);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerColor.setAdapter(mAdapter);
        
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mPenWidth);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerWidth.setAdapter(mAdapter);
        
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mBackgroud);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerBackgroud.setAdapter(mAdapter);
        
        mButtonCleanSignature.setOnClickListener(this);
        mButtonSaveSignature.setOnClickListener(this);
        mButtonCancelSignature.setOnClickListener(this);
        
        mSpinnerStyle.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mStyle = position + 1;
				if(mSignature != null)
				mSignature.setPenStyle(mStyle);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}});
        
        mSpinnerColor.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch(position) {
				case 0:
					mColorType = 6;
					break;
				case 1:
					mColorType = 0;
					break;
				case 2:
					mColorType = 3;
					break;
				case 3:
					mColorType = 1;
					break;
				case 4:
					mColorType = 11;
					break;
				default:
					break;
				}
				if(mSignature != null)
					mSignature.setColorType(mColorType);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}});
        
        mSpinnerWidth.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch(position) {
					case 0:
						mWidth = 1;
						break;
					case 1:
						mWidth = 3;
						break;
					case 2:
						mWidth = 5;
						break;
					default:
						break;
				}
				if(mSignature != null)
				mSignature.setPenWidth(mWidth);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}});
        
        mSpinnerBackgroud.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
					mSignature = new SignatureView(SignActivity.this, 1100, 680);
					
					switch(position) {
					case 0:
						mSignature.setBackgroudRes(0);
						break;
					case 1:
						mSignature.setBackgroudRes(-1);
						break;
					case 2:
						mSignature.setBackgroudRes(R.mipmap.signature_area);
						break;
					default:
						break;
					}
					mSignature.setPenStyle(mStyle);
					mSignature.setColorType(mColorType);
					mSignature.setPenWidth(mWidth);
					mSignature.init();
				    mLayoutSignature.removeAllViews();
				    mLayoutSignature.addView(mSignature);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}});
	}

	@Override
	protected void initView() {

	}

	@Override
    public void onClick(View v) {
    	if(v == mButtonCancelSignature){
	    	finish();
	    }
	    else if(v == mButtonCleanSignature) {
	    	mSignature.clear();
	    }
	    else if(v == mButtonSaveSignature){
			String ymdStr = DateUtil.convertDateToYMDShort(System.currentTimeMillis());
			File file = new File(IConstants.PROJECT_IMAGE_DIR + File.separator + ymdStr);
			if(!file.exists()) {
				file.mkdirs();
			}
			String signFilePath = IConstants.PROJECT_IMAGE_DIR + File.separator + ymdStr + File.separator+ mIDNum+"_sign.png";
			mSignature.savePng(signFilePath);
	    	mSignature.clear();
	    }
    }
	
	@Override
	public void onResume() {
		super.onResume();
		if(mSignature != null)
			mSignature.invalidate();
	}

}
