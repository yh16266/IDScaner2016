package com.haozi.idscaner2016.client.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;

import com.haozi.idscaner2016.common.base.BaseObject;
import com.haozi.idscaner2016.constants.IConstants;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

/**
 * @author YH
 * Project Name:APP File Name:java Package
 * Name:com.dxhr.myhomeclient.util Date:2015年09月27日下午2:34:27 Copyright
 * (c) 2015, dxhr All Rights Reserved.
 */
public class BitmapUtil extends BaseObject {

	protected static String TAG = "BitmapUtil";

	/**
	 * 把图片写入SD卡
	 * @param bmp 图片
	 * @param file 目标文件
	 * @param quality 图片质量
	 * @return 是否成功
	 */
	public static boolean writeBmpToSDCard(Bitmap bmp, File file, int quality) {
		try {
			ByteArrayOutputStream baosm = new ByteArrayOutputStream();
			//如果文件路径分析出来后缀名为png，则以PNG格式编码存储
			if (file.getPath().toLowerCase(Locale.getDefault())
					.endsWith(IConstants.FILE_NAME_SUFFIX_PNG)) {
				bmp.compress(Bitmap.CompressFormat.PNG, quality, baosm);
			//否则均以JEPG格式编码存储	
			} else {
				bmp.compress(Bitmap.CompressFormat.JPEG, quality, baosm);
			}
			//转换为二进制流
			byte[] bts = baosm.toByteArray();
			//如果文件存在，则删除文件
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			//将文件写入缓存目录（以当前毫秒数为缓存文件名）
			File tempFile = new File(IConstants.PROJECT_CACHE_DIR,
					Long.toString(System.currentTimeMillis()));
			//开始写入文件
			FileOutputStream fosm = new FileOutputStream(tempFile);
			BufferedOutputStream bos = new BufferedOutputStream(fosm);
			bos.write(bts);
			bos.flush();
			bos.close();
			fosm.close();
			//将文件重命名至目标文件
			tempFile.renameTo(file);
			//返回写入成功
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		//返回写入失败
		return false;
	}

	/**
	 * 把图片写入SD卡
	 * @param bmp 图片
	 * @param filePath 目标文件地址
	 * @param quality 图片质量
	 * @return 是否成功
	 */
	public static boolean writeBmpToSDCard(Bitmap bmp, String filePath,int quality) {
		return writeBmpToSDCard(bmp, new File(filePath), quality);
	}

	/**
	 * 得到缩略图
	 * @param srcPath 原始图片地址
	 * @param destPath 目标图片地址
	 * @param destW 目标宽度
	 * @param destH 目标高度
	 * @param roundPX 圆角值
	 * @param quality 图片质量
	 * @return 是否成功
	 */
	private static boolean getScaleBitmapPath(String srcPath, String destPath,
			int destW, int destH, int roundPX, int quality, boolean isWriteFile) {
		//初始化
		boolean isOK = false;
		//获取切圆角的图片bitmap
		Bitmap bmp = getScaleBitmap(srcPath, destW, destH, roundPX);
		//保存bitmap到本地文件
		if (bmp != null) {
			if (isWriteFile) {
				isOK = writeBmpToSDCard(bmp, destPath, quality);
			} else {
				isOK = true;
			}
			bmp.recycle();
		}

		return isOK;
	}

	/**
	 * 检查这张图片是否需要缩放
	 * @param srcPath 图片源地址
	 * @param destW 目标宽度
	 * @param destH 目标高度
	 * @return 是否需要
	 */
	public static boolean checkScaleBitmap(String srcPath, int destW, int destH) {
		//获取图片尺寸
		Options bop = getSizeBitmap(srcPath);
		int tarW = destW;
		int tarH = destH;
		//比较尺寸
		if (bop.outWidth > tarW || bop.outHeight > tarH) {
			return true;
		}
		return false;
	}

	/**
	 * 得到缩略图
	 * 
	 * @param destW 目标宽度
	 * @param destH 目标高度
	 * @param roundPX 圆角值
	 * @return 缩略图
	 */
	public static Bitmap getScaleBitmap(Bitmap maxBmp, int destW, int destH,int roundPX) {
		Bitmap bmp = maxBmp;
		if (maxBmp != null) {
			int curbmW = maxBmp.getWidth();
			int curbmH = maxBmp.getHeight();
			//横屏图片
			if (curbmW > curbmH) {
				int tarWidth = destW;
				//等比例缩小
				int tarHeight = (curbmH * tarWidth) / curbmW;
				//切图
				bmp = Bitmap.createScaledBitmap(maxBmp, tarWidth, tarHeight, false);
			//竖屏图片	
			} else if (curbmW < curbmH) {
				int tarHeight = destH;
				//等比例缩小
				int tarWidth = (curbmW * tarHeight) / curbmH;
				//切图
				bmp = Bitmap.createScaledBitmap(maxBmp, tarWidth, tarHeight,false);
			} else {
				//切图
				bmp = Bitmap.createScaledBitmap(maxBmp, destW, destH, false);
			}
			//切圆角
			if (roundPX > 0) {
				bmp = getRoundBitmap(maxBmp, roundPX);
			}
		}

		return bmp;
	}

	/**
	 * 得到缩略图
	 * @param srcPath 图片源地址
	 * @param destW 目标宽度
	 * @param destH 目标高度
	 * @param roundPX 圆角值
	 * @return 缩略图
	 */
	public static Bitmap getScaleBitmap(String srcPath, int destW, int destH,int roundPX) {
		
		Options bop = new Options();
		bop.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(srcPath, bop);

		bop.inJustDecodeBounds = false;

		int bopW = bop.outWidth / destW;
		int bopH = bop.outHeight / destH;

		int smpSize = bopW > bopH ? bopW : bopH;

		if (smpSize > 1) {
			bop.inSampleSize = smpSize;
			bmp = BitmapFactory.decodeFile(srcPath, bop);
		} else {
			bmp = BitmapFactory.decodeFile(srcPath);
		}

		if (bmp != null) {
			bmp = getScaleBitmap(bmp, destW, destH, roundPX);
		}

		return bmp;
	}

	public static Options getSizeBitmap(String filePath) {
		Options bop = new Options();
		bop.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, bop);
		return bop;
	}

	/**
	 * 得到圆角图片
	 * @param filePath 图片地址
	 * @param roundPX 圆角值
	 * @return 缩略图
	 */
	public static Bitmap getRoundBitmap(String filePath, int destW, int destH,int roundPX) {
		Bitmap bmp = BitmapFactory.decodeFile(filePath);
		return getRoundBitmap(bmp, destW, destH, roundPX);
	}

	/**
	 * 得到圆角图片
	 * @param roundPX 圆角值
	 * @return 缩略图
	 */
	public static Bitmap getRoundBitmap(Bitmap orgbmp, int destW, int destH,int roundPX) {

		Bitmap tarbmp = null;
		if (orgbmp != null) {
			Bitmap roundBmp = null;
			int w = orgbmp.getWidth();
			int h = orgbmp.getHeight();
			if (w > h) {
				int offX = (w - h) >> 1;
				roundBmp = Bitmap.createBitmap(orgbmp, offX, 0, h, h);
			} else if (h > w) {
				int offY = (h - w) >> 1;
				roundBmp = Bitmap.createBitmap(orgbmp, 0, offY, w, w);
			} else {
				roundBmp = orgbmp;
			}

			if (destW != w) {
				roundBmp = Bitmap.createScaledBitmap(roundBmp, destW, destH,false);
			}
			tarbmp = getRoundBitmap(roundBmp, roundPX);
		}

		return tarbmp;
	}

	/**
	 * Get round icon with border.
	 * @param source
	 * @param dstWidth
	 * @param dstHeight
	 * @param borderColor
	 * @param borderWidth set <=0 means never draw border.
	 * @return
	 */
	public static Bitmap getRoundIcon(Bitmap source, int dstWidth,
			int dstHeight, int borderColor, int borderWidth) {
		
		Bitmap ret = Bitmap.createBitmap(dstWidth, dstHeight, Config.ARGB_8888);

		source = getRoundBitmap(source, dstWidth, dstHeight, 999);
		
		Canvas canvas = new Canvas(ret);
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		float radius = (dstWidth * 0.95f) / 2;

		radius -= (borderWidth > 0 ? borderWidth : 0);
		// 画出一个圆
		canvas.drawCircle(dstWidth / 2, dstHeight / 2, radius, paint);
		// 取两层绘制交集,显示上层
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		// 将图片画上去
		canvas.drawBitmap(source, 0, 0, paint);
		paint.reset();
		if (borderWidth > 0) {
			radius += 1.5f;
			paint.setAntiAlias(true);
			paint.setStrokeWidth(borderWidth);
			paint.setColor(borderColor);
			paint.setStyle(Style.STROKE);
			canvas.drawCircle(dstWidth / 2, dstHeight / 2, radius, paint);
		}
		return ret;
	}

	/**
	 * 得到缩略图
	 * @param bitmap 原始图片
	 * @param roundPX 圆角值
	 * @return 缩略图
	 */
	public static Bitmap getRoundBitmap(Bitmap bitmap, int roundPX) {
		if (roundPX > 0) {
			Bitmap obmp = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Config.ARGB_8888);
			Canvas c = new Canvas(obmp);

			final Paint pt = new Paint(Paint.ANTI_ALIAS_FLAG
					| Paint.FILTER_BITMAP_FLAG);
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final RectF rectF = new RectF(rect);

			pt.setColor(0xFF000000);
			c.drawARGB(0, 0, 0, 0);
			c.drawRoundRect(rectF, roundPX, roundPX, pt);

			pt.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			c.drawBitmap(bitmap, rect, rect, pt);

			return obmp;
		}

		return bitmap;
	}
	/**
	 * 加载图片到内存
	 * @param res 引用资源
	 * @param resId 图片资源id
	 * */
	public static BitmapDrawable loadImage(Resources res,int resId){
		Options option=new Options();
		option.inSampleSize=2;
		return loadImage(res, resId, option);
	}
	
	/**
	 * 加载图片到内存
	 * @param res 引用资源
	 * @param resId 图片资源id
	 * @param option 选项
	 * */
	public static BitmapDrawable loadImage(Resources res, int resId,Options option) {
		BitmapDrawable ret = null;
		try {
			Bitmap image = BitmapFactory.decodeResource(res, resId,option);
			ret = new BitmapDrawable(res, image);
		} catch (OutOfMemoryError e) {

		} catch (Exception e) {
			
		}
		return ret;
	}

	/**
	 * 回收bitmap内存
	 * @param image 图片BitmapDrawable
	 * */
	public static void destroyImage(BitmapDrawable image) {
		if (image != null) {
			image.setCallback(null);
			destroyImage(image.getBitmap());
			image = null;
		}
	}
	
	/**
	 * 回收bitmap内存
	 * @param image 图片bitmap
	 * */
	public static void destroyImage(Bitmap image) {
		if (image != null && !image.isRecycled()) {
			image.recycle();
			image = null;
		}
	}
}
