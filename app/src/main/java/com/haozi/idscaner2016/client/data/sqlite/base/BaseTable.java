/**
 * 
 */
package com.haozi.idscaner2016.client.data.sqlite.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 类名：BaseTable
 * @author yinhao
 * @功能
 * @创建日期 2016年1月12日 下午4:31:12
 * @备注 [修改者，修改日期，修改内容]
 */
public abstract class BaseTable<T> {
	
	protected String TAG = getClass().getSimpleName();
	
	/**
	 * Get writable database.
	 * @return
	 */
	protected SQLiteDatabase getWritableDB() {
		return DatabaseHelper.getInstance().getWritableDatabase();
	}

	/**
	 * Get readable database.
	 * @return
	 */
	protected SQLiteDatabase getReadableDB() {
		return DatabaseHelper.getInstance().getReadableDatabase();
	}

	/**
	 * 获取表名
	 * @return
	 */
	public String getTableName(){
		return TAG;
	}

	/**
	 * 获取创建表SQL
	 * @return
	 */
	public abstract String getCreateTableSql();
	
	/**
     * 刷新数据到数据库指针
     * @param tableEntity
     * @param csr
     */
	protected abstract ContentValues putContentValues(T tableEntity, ContentValues cv);
    
	/**
     * 刷新数据到实体
     * @param tableEntity
     * @param csr
     */
	protected abstract T refreshTableEntity(T tableEntity, Cursor csr);
}
