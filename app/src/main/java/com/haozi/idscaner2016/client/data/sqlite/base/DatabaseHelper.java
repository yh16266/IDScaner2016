/**
 * 
 */
package com.haozi.idscaner2016.client.data.sqlite.base;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.client.data.sqlite.UserTable;
import com.haozi.idscaner2016.client.data.sqlite.VisitRecordTable;
import com.haozi.idscaner2016.common.app.MyApp;

/**
 * 类名：DatabaseHelper
 * 
 * @author yinhao
 * @功能
 * @创建日期 2016年1月12日 下午4:23:57
 * @备注 [修改者，修改日期，修改内容]
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	/** 数据库名称 */
	private static final String DB_NAME = "mydata.db";
	/** 数据库版本 */
	private static final int version = 10;

	public DatabaseHelper() {
		super(MyApp.getInstance(), DB_NAME, null, version);
	}

	/** 静态单例初始化 */
	private static class SingletonHolder {
		/** 静态初始化器，由JVM来保证线程安全 */
		private static DatabaseHelper instance = new DatabaseHelper();
	}

	/** 单例静态引用 */
	public static DatabaseHelper getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * 创建数据库
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		createAllTable(db);
	}

	/**
	 * 更新数据库
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		deleteAllTable(db);
		createAllTable(db);
	}

	private void createAllTable(SQLiteDatabase db) {
		db.execSQL(UserTable.getInstance().getCreateTableSql());
		db.execSQL(VisitRecordTable.getInstance().getCreateTableSql());

	}

	private void deleteAllTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS "+ UserTable.getInstance().getTableName());
		db.execSQL("DROP TABLE IF EXISTS "+ VisitRecordTable.getInstance().getTableName());
	}

}
