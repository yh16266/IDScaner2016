package com.haozi.idscaner2016.constants;


import com.haozi.idscaner2016.common.base.BaseObject;

public class IConstants extends BaseObject {
	
	public final static String NEW_VERSION_TYPE = "android";
	
	/** 项目资源文件夹 (绝对路径)*/
	public static String PROJECT_DIR;
	public static String PROJECT_ROOT_DIR;
	public static String PROJECT_THUMBNAIL_DIR;
	public static String PROJECT_IMAGE_DIR;
	public static String PROJECT_AUDIO_DIR;
	public static String PROJECT_CACHE_DIR;

	/**项目资源文件夹 (相对路径)*/
	public static String PROJECT_RELATIVE_ROOT_DIR;
	public static String PROJECT_RELATIVE_THUMBNAIL_DIR;
	public static String PROJECT_RELATIVE_IMAGE_DIR;
	public static String PROJECT_RELATIVE_AUDIO_DIR;
	public static String PROJECT_RELATIVE_CACHE_DIR;
	
	/**调用系统资源路径参数*/
	public static final String CONTENT_TYPE_APP = "application/vnd.android.package-archive";
	public static final String CONTENT_TYPE_IMAGE = "image/*";
	public static final String CONTENT_TYPE_AUDIO = "audio/*";
	public static final String CONTENT_TYPE_AUDIO_AMR = "audio/amr";
	public static final String CONTENT_TYPE_VIDEO = "video/*";
	public static final String CONTENT_TYPE_VIDEO_MP4 = "video/mp4";
	public static final String CONTENT_TYPE_UOKNOWN = "*/*";
	
	public static final int ACTIVITY_REQUEST_CODE_MAPNEARBY = 20;
	
	/**项目资源文件后缀名*/
	public static final String FILE_NAME_SUFFIX_ZIP = ".zip";
	public static final String FILE_NAME_SUFFIX_PNG = ".png";
	public static final String FILE_NAME_SUFFIX_JPEG = ".jpeg";
	public static final String FILE_NAME_SUFFIX_JPG = ".jpg";
	public static final String FILE_NAME_SUFFIX_AMR = ".amr";
	public static final String FILE_NAME_SUFFIX_AAC = ".aac";
	public static final String FILE_NAME_SUFFIX_MP4 = ".mp4";

	/**图片资源下标：0 缩略图*/
	public static final int SCALE_IMAGE_THUMBNAIL = 0;
	/**图片资源下标：1 大图*/
	public static final int SCALE_IMAGE_MAX = 1;
	/**缩略图片低质量百分比（满质量是100）*/
	public static final int THUMBNAIL_SCALE_QUALITY_LOWER = 50;
	/**缩略图片高质量百分比（满质量是100）*/
	public static final int THUMBNAIL_SCALE_QUALITY_HIGH = 80;

	/**大图大小限制*/
	public static final int MAX_IMAGE_WIDTH_1024 = 1024;
	public static final int MAX_IMAGE_HEIGHT_1024 = 1024;
	
	/**缩略图大小限制*/
	public static final int THUMBNAIL_WIDTH_256 = 256;
	public static final int THUMBNAIL_HEIGHT_256 = 256;
	public static final int THUMBNAIL_WIDTH_512 = 512;
	public static final int THUMBNAIL_HEIGHT_512 = 512;

	/**通过相册添加图片*/
	public static final int ACTIVITY_REQUEST_CODE_ATTACH_ADD_IMAGE = 100;
	/**通过相机添加图片*/
	public static final int ACTIVITY_REQUEST_CODE_ATTACH_TAKE_PHOTO = 101;
	
	/**翻页查询，起始页数*/
	public static final int PAGE_START_INDEX = 1;

}
