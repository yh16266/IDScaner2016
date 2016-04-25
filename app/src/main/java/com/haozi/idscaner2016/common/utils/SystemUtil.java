package com.haozi.idscaner2016.common.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.common.app.MyApp;
import com.haozi.idscaner2016.common.base.BaseObject;
import com.haozi.idscaner2016.constants.IConstants;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 类名：SystemUtil
 * @author YH 创建日期：2014年11月24日 [修改者，修改日期，修改内容]
 */
public class SystemUtil extends BaseObject {

	/**相机图片缓存*/
	private static File takePicFile;

	/**
	 * 获取缓存中最近一次图片引用
	 * */
	public static File getTakePicFile() {
		return takePicFile;
	}

	/**
	 * 得到IMEI号
	 * @return
	 */
	public static String getDeviceIMEI() {
		String strIMEI = null;
		try {
			TelephonyManager tm = (TelephonyManager) MyApp.getInstance()
					.getSystemService(Context.TELEPHONY_SERVICE);
			strIMEI = tm.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (StringUtil.isEmpty(strIMEI)) {
			strIMEI = Long.toString(System.nanoTime());
		}
		return strIMEI;
	}

	/**
	 * 检查操作系统版本是否大于10
	 * */
	public static boolean checkVersionGreaterThan10() {
		return Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1;
	}

	/**
	 * 检查操作系统版本是否大于17
	 * */
	public static boolean checkVersionGreaterThan17() {
		return Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

	/**
	 * 获取apk文件包信息
	 * @param filePath apk文件路径
	 * */
	public static PackageInfo getAPKInfo(String filePath) {
		PackageManager pkgManger = MyApp.getInstance().getPackageManager();
		PackageInfo pkgInfo = pkgManger.getPackageArchiveInfo(filePath,
				PackageManager.GET_ACTIVITIES);
		return pkgInfo;
	}

	/**
	 * 判断分辨率
	 * */
	public static boolean is480X320() {
		return MyApp.getInstance().getResources().getDimension(R.dimen.check_480x320) > 0;
	}
	
	/**
	 * 检查intent是否可用
	 * @param intent 查询的目标intent
	 * */
	public static boolean isIntentAvailable(Intent intent) {
		final PackageManager packageManager = MyApp.getInstance().getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,PackageManager.GET_ACTIVITIES);
		return list != null && list.size() > 0;
	}

	/**
	 * 判断APP是否已经安装
	 * @param pkg app的package信息
	 * */
	public static boolean isAppInstalled(String pkg) {
		try {
			PackageInfo packageInfo = MyApp.getInstance().getPackageManager()
					.getPackageInfo(pkg, PackageManager.GET_UNINSTALLED_PACKAGES);
			if (packageInfo != null) {
				return true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 获取调用系统添加图片功能参数
	 */
	@SuppressLint("InlinedApi")
	private static Intent getAddImgIntent(){
		//使用Intent.ACTION_GET_CONTENT这个Action
		Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
		//开启Pictures画面Type设定为image
		innerIntent.setType(IConstants.CONTENT_TYPE_IMAGE);
		//根据版本设置不同参数
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
			innerIntent.addCategory(Intent.CATEGORY_OPENABLE);
		} else {
			innerIntent.setAction(Intent.ACTION_GET_CONTENT);
		}
		return innerIntent;
	}
	
	/**
	 * 剪切图片
	 * @param act
	 * @param uri
	 * @param requestCode
	 */
	public static void cropImage(Activity act, Uri uri, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", IConstants.MAX_IMAGE_WIDTH_1024);
		intent.putExtra("outputY", IConstants.MAX_IMAGE_HEIGHT_1024);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		act.startActivityForResult(intent, requestCode);
	}
	
	/**
	 * 调用系统添加图片功能
	 * @param act
	 * @param requestCode
	 */
	@TargetApi(19)
	public static void addImage(Activity act, int requestCode) {
		act.startActivityForResult(getAddImgIntent(), requestCode);
	}

	/**
	 * 调用系统添加图片功能
	 * @param act
	 * @param requestCode
	 */
	@TargetApi(19)
	public static void addImage(Fragment frag, int requestCode) {
		frag.startActivityForResult(getAddImgIntent(), requestCode);
	}
	
	/**
	 * 调用系统添加图片功能
	 * @param act
	 * @param requestCode
	 */
	public static File getAddPicFile(Intent data) {
		//获取URI
		Uri uri = data != null ? data.getData() : null;
		//转换为本地路径
		String imgPath = UrlPathUtil.parseImagePath(uri);
		if(imgPath != null && imgPath.length() > 0) {
			File file = new File(imgPath);
			if(file.exists()) {
				if(UrlPathUtil.isImage(imgPath)) {
					return file;
				} else {
					DXLog.e("SystemUtil", "getAddPicture:Failed for Not img-file!");
					return null;
				}
			} else {
				DXLog.e("SystemUtil", "getAddPicture:Failed for No File exsists!");
				return null;
			}
		} else {
			DXLog.e("SystemUtil", "getAddPicture:Failed for EMPTY PATH!");
			return null;
		}
	}
	
	/**
	 * 获取拍照参数
	 * */
	private static Intent getTakePictureIntent(){
		takePicFile = new File(IConstants.PROJECT_IMAGE_DIR, System.nanoTime()
				+ IConstants.FILE_NAME_SUFFIX_JPEG);
		try {
			takePicFile.createNewFile();
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(takePicFile));
			return intent;
		} catch (IOException e) {
			e.printStackTrace();
			FileUtil.initDir();
		}
		return null;
	}
	
	/**
	 * 调用系统照相功能
	 * @param act
	 * @param requestCode
	 */
	public static void takePicture(Activity act, int requestCode) {
		act.startActivityForResult(getTakePictureIntent(), requestCode);
	}
	
	/**
	 * 调用系统照相功能
	 * @param act
	 * @param requestCode
	 */
	public static void takePicture(Fragment frag, int requestCode) {
		frag.startActivityForResult(getTakePictureIntent(), requestCode);
	}
}
