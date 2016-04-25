/**
 * 
 */
package com.haozi.idscaner2016.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;

import com.haozi.idscaner2016.common.app.MyApp;
import com.haozi.idscaner2016.common.base.BaseObject;
import com.haozi.idscaner2016.constants.IConstants;

/**
 * 类名：UrlPathUtil
 * @author yinhao
 * @功能 统一管理APP各种路径
 * @创建日期 2015年10月13日 上午10:04:30
 * @备注 [修改者，修改日期，修改内容]
 */
public class UrlPathUtil extends BaseObject {

	/**
	 * 根据URI地址获取文件路径
	 * */
	public static String getFilePathFromUri(Uri uri) {
	    String filePath = null;
	    if ("content".equals(uri.getScheme())) {
	        String[] filePathColumn = {MediaColumns.DATA};
	        Cursor c = MyApp.getInstance().getContentResolver().query(uri, filePathColumn, null, null, null);
	        if(c != null) {
	        	c.moveToFirst();
		        filePath = c.getString(c.getColumnIndex(MediaColumns.DATA));
		        c.close();
	        }
	    } else if ("file".equals(uri.getScheme())) {
	        filePath = new File(uri.getPath()).getAbsolutePath();
	    }
	    return filePath;
	}
	
	/**
	 * 根据文件路径获取文件后缀名(包括点符号：".xxx")
	 * */
	public static String getExtName(String filePath) {
    	String exname = filePath.indexOf('.') > 0 ? filePath.substring(filePath.lastIndexOf('.')) : "";
    	return exname;
	}
	
	/**
	 * 改名字
	 * @param srcName 原始名字
	 * @param appStr 中间名字
	 * @return 新名字
	 */
	public static String generateFileUrl(String srcName, String appStr) {
		int index = srcName.lastIndexOf('.');
		if(index > 0) {
			String exName = srcName.substring(index);
			String tempName = srcName.substring(0, index);
			return tempName + appStr + exName;
		}
		return srcName;
	}
	
	/**
	 * 改名字
	 * @param folderPath 文件路径
	 * @param url 文件url
	 * @param defaultAppStr 默认扩展名
	 * @return 新名字
	 */
	public static String generateFilePath(String folderPath, String url, String defaultAppStr) {
		String fname = null;
		int index = url.lastIndexOf('.');
		int indexG = url.lastIndexOf('/');
		String name = url.substring(indexG + 1);
		if(index > 0) {
			fname = folderPath + File.separator + name;// + temp;
		} else {
			fname = folderPath + File.separator + name + defaultAppStr;
		}
		
		return fname;
	}
	
	/**
	 * 解析文件URI
	 * @param uri 文件URI
	 * @return 文件地址
	 */
	public static String parseImagePath(Uri uri) {
		if (uri != null) {
			String scheme = uri.getScheme();
			if ("content".equalsIgnoreCase(scheme)) {
				return getImageFromUri(uri);
			} else if ("file".equalsIgnoreCase(scheme)) {
				return uri.getPath();
			}
		}
		return null;
	}
	
	/**
	 * 根据文件URI得到文件本地路径
	 * @param uri 文件URI
	 * @return 文件本地路径
	 */
	@SuppressLint("NewApi")
	public static String getImageFromUri(Uri uri) {
		//文件路径
		String filePath = null;
		//检查当前系统版本是否大于等于19（即4.4.2）
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			String wholeID = DocumentsContract.getDocumentId(uri);
			if (!TextUtils.isEmpty(wholeID) && wholeID.contains(":")) {
				String id = wholeID.split(":")[1];
				String[] column = { Images.Media.DATA };
				String sel = Images.Media._ID + "=?";

				Cursor csr = MyApp.getInstance().getContentResolver()
						.query(Images.Media.EXTERNAL_CONTENT_URI,
								column, sel, new String[] { id }, null);
				if (csr != null) {
					int columnIndex = csr.getColumnIndex(column[0]);
					if (csr.moveToFirst()) {
						filePath = csr.getString(columnIndex);
					}
					csr.close();
				}
			}
		//若小于，则用兼容方案	
		} else {
			Cursor c = MyApp .getInstance().getContentResolver()
					.query(uri, new String[] { Images.Media.DATA }, null, null,null);
			if(c != null){
				if (c.moveToFirst()) {
					filePath = c.getString(c.getColumnIndexOrThrow(Images.Media.DATA));
				}
				c.close();
			}
		}
		return filePath;
	}
	
	/**
	 * 获取文件类型（例如 abc.ff 返回 ff）
	 * @param fileName
	 * @return 文件后缀名
	 */
	public static String getFileType(String fileName) {
		if (fileName != null) {
			int typeIndex = fileName.lastIndexOf(".");
			if (typeIndex != -1) {
				String fileType = fileName.substring(typeIndex + 1).toLowerCase(Locale.getDefault());
				return fileType;
			}
		}
		return "";
	}

	/**
	 * 根据后缀名判断是否是图片文件
	 * @param filePath
	 * @return 是否是图片结果true or false
	 */
	public static boolean isImage(String filePath) {
		String str = getFileType(filePath);
		if (str != null
				&& (str.equalsIgnoreCase("jpg") || str.equalsIgnoreCase("gif")
						|| str.equalsIgnoreCase("png")
						|| str.equalsIgnoreCase("jpeg")
						|| str.equalsIgnoreCase("bmp")
						|| str.equalsIgnoreCase("wbmp")
						|| str.equalsIgnoreCase("ico") || str
							.equalsIgnoreCase("jpe"))) {
			return true;
		}
		return false;
	}

	/**
	 * 根据后缀名判断是否是视频文件
	 * @param filePath
	 * @return 是否是图片结果true or false
	 */
	public static boolean isVideo(String filePath) {
		String str = getFileType(filePath);
		if (str != null && (str.equalsIgnoreCase("mp4") || str.equalsIgnoreCase("3gp"))) {
			return true;
		}
		return false;
	}

	/**
	 * 根据后缀名判断是否是录音文件
	 * @param filePath
	 * @return 是否是图片结果true or false
	 */
	public static boolean isVoice(String filePath) {
		String str = getFileType(filePath);
		if (str != null && (str.equalsIgnoreCase("amr") || str.equalsIgnoreCase("mp3")|| str.equalsIgnoreCase("aac"))) {
			return true;
		}
		return false;
	}

	/**
	 * 根据后缀名判断是否是图片文件
	 * @param filePath
	 * @return 是否是图片结果true or false
	 */
	public static boolean isGif(String filePath) {
		String str = getFileType(filePath);
		if (str != null && str.equalsIgnoreCase("gif")) {
			try {
				FileInputStream fism = new FileInputStream(new File(filePath));
				int len = fism.available();
				byte[] bt = new byte[len];
				fism.read(bt);
				fism.close();

				Movie gifMovie = Movie.decodeByteArray(bt, 0, bt.length);

				return gifMovie != null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 获取缓存文件名（根据URL的MD5规则）
	 * */
	public static String getFileTempName(String url){
		//后缀名
		String fileExtname = UrlPathUtil.getExtName(url);
		//MD5化文件名
		String tempname = HashUtil.md5(url);
		//返回新的缓存文件名
		return tempname+fileExtname;
	}

	/**
	 * 获取缓存文件本地路径（根据URL的MD5规则）
	 * */
	public static String getFileTempPath(String parentPath,String url){
		String filepath = parentPath+File.separator+getFileTempName(url);
		return filepath;
	}

	/**
	 * 获取音频缓存文件本地路径（根据URL的MD5规则）
	 * */
	public static String getFileAudioTempPath(String url){
		return getFileTempPath(IConstants.PROJECT_AUDIO_DIR, url);
	}

	/**
	 * 获取音频录音缓存路径
	 * */
	public static String getFileAudioRecordPath(){
		return IConstants.PROJECT_AUDIO_DIR+File.separator+"audio_record"+IConstants.FILE_NAME_SUFFIX_AMR;
	}
}
