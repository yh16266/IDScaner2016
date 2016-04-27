package com.haozi.idscaner2016.common.utils;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.StatFs;

import com.haozi.idscaner2016.common.app.MyApp;
import com.haozi.idscaner2016.common.base.BaseObject;
import com.haozi.idscaner2016.constants.IConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

public class FileUtil extends BaseObject {

	/**
	 * 解压GZIP
	 * @param ism 输入流
	 * @return 压缩内容
	 */
	public static String unZip(InputStream ism) {
		ZipInputStream zism = new ZipInputStream(ism);
		String strResult = null;
		try {
			int len = -1;
			byte[] bt = new byte[2 * 1024];
			ByteArrayOutputStream baosm = new ByteArrayOutputStream();
			while((len = zism.read(bt)) != -1) {
				baosm.write(bt, 0, len);
			}
			strResult = baosm.toString();
			baosm.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(zism != null) {
				try {
					zism.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return strResult;
	}

	/**
	 * 解压ZIP
	 * @param ism 输入流
	 * @return 压缩内容
	 */
	public static String unGZip(InputStream ism) {
		GZIPInputStream zism = null;
		String strResult = null;
		try {
			zism = new GZIPInputStream(ism);
			int len = -1;
			byte[] bt = new byte[2 * 1024];
			ByteArrayOutputStream baosm = new ByteArrayOutputStream();
			while((len = zism.read(bt)) != -1) {
				baosm.write(bt, 0, len);
			}
			strResult = baosm.toString();
			baosm.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(zism != null) {
				try {
					zism.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return strResult;
	}
    
	/**
	 * 获取文件大小,递归
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static long getFileSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	/**
	 * 判断文件是否存在(若为目录则返回false)
	 * */
	public static boolean checkFileExits(String filepath){
		if(StringUtil.isEmpty(filepath)){
			return false;
		}
		File file = new File(filepath);
		if (file.exists() && file.isFile()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 根据路径删除指定的目录或文件，无论存在与否
	 * @param sPath 要删除的目录或文件
	 * @return 删除成功返回 true，否则返回 false。
	 */
	public static boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		if (!file.exists()) {
			return flag;
		} else {
			if (file.isFile()) {
				return deleteFile(sPath);
			} else {
				return deleteDirectory(sPath);
			}
		}
	}

	/**
	 * 删除单个文件
	 * @param sPath 被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * @param sPath 被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String sPath) {
		boolean flag = false;
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		flag = true;
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} 
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 转换
	 * @param fileS
	 * @return
	 */
	public static String formatFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}
	
	/**
	 * 检查存储卡状态
	 * @param isCreateDirs 是否需要创建文件路径
	 * @return 存储卡是否正常
	 */
	public static boolean checkSDCard(boolean isCreateDirs) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
			if (isCreateDirs) {
				initDir();
			}
			return true;
		}
		return false;
	}
	
	/**
     * 获取手机外部可用空间大小
     * @return
     */
    @SuppressLint("NewApi")
	public static long getAvailableInternalMemorySize() {
        if(checkSDCard(true)) {
        	File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            @SuppressWarnings("deprecation")
			long blockSize = SystemUtil.checkVersionGreaterThan17() ? stat.getBlockSizeLong() : stat.getBlockSize();
            @SuppressWarnings("deprecation")
			long availableBlocks = SystemUtil.checkVersionGreaterThan17() ? stat.getAvailableBlocksLong() : stat.getAvailableBlocks();
            long tl = availableBlocks * blockSize;
            return tl;
        }
        return 0;
    }

	/**
	 * 创建文件路径
	 */
	public synchronized static void initDir() {
		/**系统级app缓存目录*/
		String cachePath = null;
		///**大文件缓存目录:外部SD卡*/
		//String largeFileCache = "";
		//如果SD卡可用，或者外部存储卡可被拆卸，则使用系统缓存目录
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
			//存储于android标准的缓存目录下（可以通过android程序管理中的清除缓存来清理掉）
			//cachePath = MyApp.getInstance().getExternalCacheDir().getPath();
			//存储于SD卡中程序包名下(此路径依然为手机内存卡0的路劲)
			cachePath = Environment.getExternalStorageDirectory().getPath()+File.separator+MyApp.getInstance().getPackageName();
		} else {
			cachePath = MyApp.getInstance().getCacheDir().getPath();
		}
		IConstants.PROJECT_DIR = cachePath;// + File.separator + IConstants.PROJECT_RELATIVE_PROJECT_DIR;
		File dir = new File(IConstants.PROJECT_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		IConstants.PROJECT_RELATIVE_ROOT_DIR = /*IConstants.PROJECT_RELATIVE_PROJECT_DIR + File.separator + */""/*+ laccountID*/;
		/**  项目资源文件夹 (相对路径) */
		IConstants.PROJECT_RELATIVE_THUMBNAIL_DIR = 				IConstants.PROJECT_RELATIVE_ROOT_DIR + File.separator + ".thumbnail";
		IConstants.PROJECT_RELATIVE_IMAGE_DIR =					IConstants.PROJECT_RELATIVE_ROOT_DIR + File.separator + "image";
		IConstants.PROJECT_RELATIVE_AUDIO_DIR = 					IConstants.PROJECT_RELATIVE_ROOT_DIR + File.separator + "audio";
		IConstants.PROJECT_RELATIVE_CACHE_DIR = 					IConstants.PROJECT_RELATIVE_ROOT_DIR + File.separator + ".cache";

		/** 项目资源文件夹 (绝对路径) */
		IConstants.PROJECT_ROOT_DIR = 							cachePath + File.separator + IConstants.PROJECT_RELATIVE_ROOT_DIR;
		IConstants.PROJECT_THUMBNAIL_DIR = 						cachePath + File.separator + IConstants.PROJECT_RELATIVE_THUMBNAIL_DIR;
		IConstants.PROJECT_IMAGE_DIR = 							cachePath + File.separator + IConstants.PROJECT_RELATIVE_IMAGE_DIR;
		IConstants.PROJECT_AUDIO_DIR = 							cachePath + File.separator + IConstants.PROJECT_RELATIVE_AUDIO_DIR;
		IConstants.PROJECT_CACHE_DIR = 							cachePath + File.separator + IConstants.PROJECT_RELATIVE_CACHE_DIR;

		dir = new File(IConstants.PROJECT_ROOT_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		dir = new File(IConstants.PROJECT_THUMBNAIL_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		dir = new File(IConstants.PROJECT_IMAGE_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		dir = new File(IConstants.PROJECT_AUDIO_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		dir = new File(IConstants.PROJECT_CACHE_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
	
	/**
	 * 将信息写入日志文件
	 * */
	public static void writeToLogFile(String str) {
		File file = new File(IConstants.PROJECT_DIR + File.separator + "log.txt");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		try {
			FileWriter fw = new FileWriter(file, true);
			fw.write(str);
			fw.write("\n\n");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
