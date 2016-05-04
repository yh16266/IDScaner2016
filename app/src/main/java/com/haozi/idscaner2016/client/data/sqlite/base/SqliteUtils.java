/**
 * 
 */
package com.haozi.idscaner2016.client.data.sqlite.base;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.haozi.idscaner2016.common.utils.StringUtil;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 类名：SqliteUtils
 * 
 * @author yinhao
 * @功能
 * @创建日期 2016年1月12日 下午5:48:28
 * @备注 [修改者，修改日期，修改内容]
 */
public class SqliteUtils {

	/** 静态单例初始化 */
	private static class SingletonHolder {
		/** 静态初始化器，由JVM来保证线程安全 */
		private static SqliteUtils instance = new SqliteUtils();
	}

	/** 单例静态引用 */
	public static SqliteUtils getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * 获取创建SQL
	 * */
	public String getCreateSql(String tableName, String sqlcontent) {
		StringBuffer sql = new StringBuffer();
		sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName);
		sql.append("(");
		sql.append(BaseTableBody.ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ");
		sql.append(sqlcontent);
		sql.append(" )");
		return sql.toString();
	}

	public Cursor getCursor(String tableName) {
		SQLiteDatabase sdb = DatabaseHelper.getInstance().getWritableDatabase();
		if (sdb != null) {
			return sdb.rawQuery("SELECT * FROM " + tableName, null);
		}
		return null;
	}

	public Cursor getCursor(String tableName, String whereClause) {
		SQLiteDatabase sdb = DatabaseHelper.getInstance().getWritableDatabase();
		if (sdb != null) {
			String sql = whereClause != null && whereClause.length() > 0 ? ("SELECT * FROM "
					+ tableName + " WHERE " + whereClause)
					: ("SELECT * FROM " + tableName);
			return sdb.rawQuery(sql, null);
		}
		return null;
	}

	public Cursor getCursorBySql(String sql, String[] selectionArgs) {
		SQLiteDatabase sdb = DatabaseHelper.getInstance().getWritableDatabase();
		if (sdb != null) {
			return sdb.rawQuery(sql, selectionArgs);
		}
		return null;
	}

	public int count(String tableName, String whereClause) {
		int sresult = 0;
		Cursor cursor = whereClause != null && whereClause.length() > 0 ? getCursorBySql(
				"SELECT _id FROM " + tableName + " WHERE " + whereClause, new String[]{})
				: getCursorBySql("SELECT _id FROM " + tableName, new String[]{});
		if (cursor != null) {
			sresult = cursor.getCount();
			cursor.close();
		}
		return sresult;
	}
	
	public synchronized long insertIfNot(ContentValues cv, String tableName,String whereClause) {
        return insertCheck(cv, tableName, whereClause, false);
    }

    public synchronized long insertUpdate(ContentValues cv, String tableName, String whereClause) {
        return insertCheck(cv, tableName, whereClause, true);
    }

    public synchronized long insertCheck(ContentValues cv, String tableName, String whereClause, boolean isUpdate) {
        long result = -1;
        SQLiteDatabase sdb = DatabaseHelper.getInstance().getWritableDatabase();
        if (sdb != null) {
            if (whereClause != null && whereClause.length() > 0) {
                int count = count(tableName, whereClause);
                if(count > 0) {
                	if(isUpdate) {
                    	result = update(cv, tableName, whereClause);
                    }
                } else {
                	result = insert(cv, tableName);
                }
            } else {
                result = insert(cv, tableName);
            }
        }
        return result;
    }

    public synchronized long insert(ContentValues cv, String tableName) {
        long result = -1;
        SQLiteDatabase sdb = DatabaseHelper.getInstance().getWritableDatabase();
        if (sdb != null) {
            result = sdb.insert(tableName, null, cv);
        }
        return result;
    }

    public synchronized long update(ContentValues cv, String tableName, String whereClause) {
        long result = -1;
        SQLiteDatabase sdb = DatabaseHelper.getInstance().getWritableDatabase();
        if (sdb != null) {
            result = sdb.update(tableName, cv, whereClause, null);
        }
        return result;
    }

    public synchronized long delete(String tableName,String whereClause) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        if (db != null) {
            return db.delete(tableName, whereClause, null);
        }
        return -1;
    }
    
    public String getString(String column, String tableName, String whereClause) {
        String sresult = null;
        Cursor cursor = whereClause != null && whereClause.length() > 0 ? 
        		getCursorBySql("SELECT " + column + " FROM " + tableName + " WHERE " + whereClause, new String[]{}) :
        			getCursorBySql("SELECT " + column + " FROM " + tableName, new String[]{});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                sresult = cursor.getString(cursor.getColumnIndex(column));
            }
            cursor.close();
        }

        return sresult;
    }

    public String[] getStringArray(String column, String tableName, String whereClause) {
        String[] sresult = null;
        Cursor cursor = whereClause != null && whereClause.length() > 0 ? 
        		getCursorBySql("SELECT " + column + " FROM " + tableName + " WHERE " + whereClause, null) : 
        			getCursorBySql("SELECT " + column + " FROM " + tableName, null);
        if (cursor != null) {
            if(cursor.getCount() > 0) {
            	sresult = new String[cursor.getCount()];
            	for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    sresult[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(column));
                }
            }
            cursor.close();
        }

        return sresult;
    }

    public long setString(String column, String value, String tableName, String whereClause) {
        ContentValues cv = new ContentValues();
        cv.put(column, value);
        return update(cv, tableName, whereClause);
    }

    public int getInt(String column, String tableName, String whereClause) {
        int sresult = -1;

        Cursor cursor = whereClause != null && whereClause.length() > 0 ? 
        		getCursorBySql("SELECT " + column + " FROM " + tableName + " WHERE " + whereClause, null) : 
        			getCursorBySql("SELECT " + column + " FROM " + tableName, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                sresult = cursor.getInt(cursor.getColumnIndex(column));
            }
            cursor.close();
        }

        return sresult;
    }

    public long setInt(String column, int value, String tableName, String whereClause) {
        ContentValues cv = new ContentValues();
        cv.put(column, value);
        return update(cv, tableName, whereClause);
    }

    public long setLong(String column, long value, String tableName, String whereClause) {
        ContentValues cv = new ContentValues();
        cv.put(column, value);
        return update(cv, tableName, whereClause);
    }

    public long[] getLongArray(String column, String tableName, String whereClause) {
        long[] sresult = null;

        Cursor cursor = whereClause != null && whereClause.length() > 0 ? 
        		getCursorBySql("SELECT " + column + " FROM " + tableName + " WHERE " + whereClause, null) : 
        			getCursorBySql("SELECT " + column + " FROM " + tableName, null);
        if (cursor != null) {
        	if(cursor.getCount() > 0) {
            	sresult = new long[cursor.getCount()];
            	for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    sresult[cursor.getPosition()] = cursor.getLong(cursor.getColumnIndex(column));
                }
            }
            cursor.close();
        }

        return sresult;
    }

    public long getLong(String column, String tableName, String whereClause) {
        long sresult = -1;

        Cursor cursor = whereClause != null && whereClause.length() > 0 ? 
        		getCursorBySql("SELECT " + column + " FROM " + tableName + " WHERE " + whereClause, null) : 
        			getCursorBySql("SELECT " + column + " FROM " + tableName, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                sresult = cursor.getLong(cursor.getColumnIndex(column));
            }
            cursor.close();
        }

        return sresult;
    }

    public double getDouble(String column, String tableName, String whereClause) {
        double sresult = -1;

        Cursor cursor = whereClause != null && whereClause.length() > 0 ? 
        		getCursorBySql("SELECT " + column + " FROM " + tableName + " WHERE " + whereClause, null) : 
        			getCursorBySql("SELECT " + column + " FROM " + tableName, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                sresult = cursor.getDouble(cursor.getColumnIndex(column));
            }
            cursor.close();
        }

        return sresult;
    }
    
    public int getIntColumn(Cursor csr, String strColumn) {
    	int i = -1;
    	int idx = csr.getColumnIndex(strColumn);
    	if(idx >= 0) {
    		i = csr.getInt(idx);
    	}

    	return i;
    }
    
    public long getLongColumn(Cursor csr, String strColumn) {
    	long i = -1;
    	int idx = csr.getColumnIndex(strColumn);
    	if(idx >= 0) {
    		i = csr.getLong(idx);	
    	}
    	
    	return i;
    }
    
    public String getStringColumn(Cursor csr, String strColumn) {
    	String str = null;
    	int idx = csr.getColumnIndex(strColumn);
    	if(idx >= 0) {
    		str = csr.getString(idx);
    	}
    	
    	return str;
    }

    public BigDecimal getBigDecimalColumn(Cursor csr, String strColumn) {
        String str = null;
        int idx = csr.getColumnIndex(strColumn);
        if(idx >= 0) {
            str = csr.getString(idx);
        }
        if(StringUtil.isEmpty(str) == true){
            return new BigDecimal(0);
        }
        return new BigDecimal(str);
    }

    public Date getDateColumn(Cursor csr, String strColumn) {
    	Date date = null;
    	int idx = csr.getColumnIndex(strColumn);
    	if(idx >= 0) {
    		long datevalue = csr.getLong(idx);
			try {
				date = new Date(datevalue);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	return date;
    }

    /**
     * 将值写入ContentValues中
     * */
    protected void setBitmapColumn(ContentValues cv,boolean isSetEmptyValue,String colName,Bitmap value){
        if(cv == null || StringUtil.isEmpty(colName)){
            return;
        }
        if(isSetEmptyValue == true){
            ByteArrayOutputStream valueDate = new ByteArrayOutputStream();
            if(value != null){
                value.compress(Bitmap.CompressFormat.PNG, 100, valueDate);
            }
            cv.put(colName, valueDate.toByteArray());
        }else if(isSetEmptyValue == false && value != null){
            ByteArrayOutputStream valueDate = new ByteArrayOutputStream();
            value.compress(Bitmap.CompressFormat.PNG, 100, valueDate);
            cv.put(colName, valueDate.toByteArray());
        }
    }

    /**
     * 读取指针的BITMAP值
     * */
    protected Bitmap getBitmapColumn(Cursor cursor,String columnName){
        /**得到Bitmap字节数据**/
        Bitmap value = null;
        if(cursor == null || StringUtil.isEmpty(columnName)){
            return value;
        }
        int columnNum = cursor.getColumnIndex(columnName);
        if(columnNum < 0){
            return value;
        }
        byte[] valuedata = cursor.getBlob(cursor.getColumnIndex(columnName));
        if(valuedata == null || valuedata.length == 0){
            return value;
        }
        /**
         * 根据Bitmap字节数据转换成 Bitmap对象
         * BitmapFactory.decodeByteArray() 方法对字节数据，从0到字节的长进行解码，生成Bitmap对像。
         **/
        value = BitmapFactory.decodeByteArray(valuedata, 0, valuedata.length);
        //返回
        return value;
    }
}
