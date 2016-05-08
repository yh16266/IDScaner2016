/**
 * 
 */
package com.haozi.idscaner2016.client.data.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.haozi.idscaner2016.client.bean.client.SystemCodeEntity;
import com.haozi.idscaner2016.client.bean.client.SystemCodeEntity;
import com.haozi.idscaner2016.client.data.sqlite.base.BaseTable;
import com.haozi.idscaner2016.client.data.sqlite.base.BaseTableBody;
import com.haozi.idscaner2016.client.data.sqlite.base.SqliteUtils;
import com.haozi.idscaner2016.common.utils.DateUtil;
import com.haozi.idscaner2016.common.utils.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * 类名：MessageTable
 * @author yinhao
 * @功能
 * @创建日期 2016年1月12日 下午4:36:20
 * @备注 [修改者，修改日期，修改内容]
 */
public class SystemCodeTable extends BaseTable<SystemCodeEntity> {

	private static final int PAGE_SIZE = 20;

	/** 静态单例初始化 */
	private static class SingletonHolder {
		/** 静态初始化器，由JVM来保证线程安全 */
		private static SystemCodeTable instance = new SystemCodeTable();
	}

	/** 单例静态引用 */
	public static SystemCodeTable getInstance() {
		return SingletonHolder.instance;
	}
	
	private class Table extends BaseTableBody {
		/** 类型 */
		public static final String TYPE = "TYPE";
		/** 名字 */
		public static final String NAME = "NAME";
		/** 代码 */
		public static final String CODE = "CODE";
		/** 排序 */
		public static final String ORDER_INDDEX = "ORDER_INDDEX";
		/** 创建时间 */
		public static final String CREATE_TIME = "CREATE_TIME";
	}
	
	/**
	 * @return
	 */
	public String getCreateTableSql() {
		StringBuffer sql = new StringBuffer();
		sql.append(Table.TYPE).append(" TEXT, ");
		sql.append(Table.NAME).append(" TEXT, ");
		sql.append(Table.CODE).append(" TEXT, ");
		sql.append(Table.ORDER_INDDEX).append(" TEXT, ");
		sql.append(Table.CREATE_TIME).append(" BIGINT ");
		return SqliteUtils.getInstance().getCreateSql(getTableName(), sql.toString());
	}

    /**
	 * @param conver
	 * @return
	 */
	private ContentValues putContentValues(SystemCodeEntity conver) {
		return putContentValues(conver, new ContentValues());
	}
	
	@Override
	protected ContentValues putContentValues(SystemCodeEntity tableEntity, ContentValues cv) {
		//cv.put(Table.ID, tableEntity.getId());
		cv.put(Table.TYPE, tableEntity.getType());
		cv.put(Table.NAME, tableEntity.getName());
		cv.put(Table.CODE, tableEntity.getCode());
		cv.put(Table.ORDER_INDDEX,tableEntity.getOrder());
		cv.put(Table.CREATE_TIME,tableEntity.getCreateTime());
		return cv;
	}

	/**
	 * @param csr
	 * @return
	 */
	private SystemCodeEntity refreshTableEntity(Cursor csr) {
		return refreshTableEntity(new SystemCodeEntity(), csr);
	}
	
	@Override
	protected SystemCodeEntity refreshTableEntity( SystemCodeEntity tableEntity, Cursor csr) {
		if(tableEntity == null){
			return tableEntity;
		}
		tableEntity.setId(SqliteUtils.getInstance().getLongColumn(csr, Table.ID));
		tableEntity.setType(SqliteUtils.getInstance().getStringColumn(csr, Table.TYPE));
		tableEntity.setName(SqliteUtils.getInstance().getStringColumn(csr, Table.NAME));
		tableEntity.setCode(SqliteUtils.getInstance().getStringColumn(csr, Table.CODE));
		tableEntity.setOrder(SqliteUtils.getInstance().getIntColumn(csr, Table.ORDER_INDDEX));
		tableEntity.setCreateTime(SqliteUtils.getInstance().getLongColumn(csr, Table.CREATE_TIME));
		return tableEntity;
	}

    /**
     * 插入更新记录
     * @param msg
     */
    public void insertUpdateRecord(SystemCodeEntity msg) {
    	if(msg == null){
    		return;
    	}
    	ContentValues cv = putContentValues(msg);
    	if(msg.getId() > 0){
    		SqliteUtils.getInstance().insertUpdate(cv, getTableName(), Table.ID + "=" + msg.getId());
    	}else{
    		SqliteUtils.getInstance().insert(cv, getTableName());
    	}
    }

	/**
	 * 更新记录
	 * @param msg
	 */
	public void updateRecordById(SystemCodeEntity msg) {
		if(msg == null){
			return;
		}
		ContentValues cv = putContentValues(msg);
		if(msg.getId() > 0){
			SqliteUtils.getInstance().update(cv, getTableName(), Table.ID + "=" + msg.getId());
		}else{
			return;
		}
	}

	/**
	 * 更新记录
	 * @param msg
	 */
	public void updateRecordByTypeAndCode(SystemCodeEntity msg) {
		if(msg == null){
			return;
		}
		ContentValues cv = putContentValues(msg);
		if(msg.getId() > 0){
			SqliteUtils.getInstance().update(cv, getTableName(),
					Table.TYPE + "='" + msg.getType()+"' and "+Table.CODE +"='"+msg.getCode()+"'");
		}else{
			return;
		}
	}

	/**
     * 删除记录
     * @param recordId
     */
    public void deleteRecord(long recordId) {
    	SqliteUtils.getInstance().delete(getTableName(), Table.ID + "=" + recordId);
    }
    
    /**
     * 查询记录
     * @param type
     */
    public SystemCodeEntity getRecordByTypeAndCode(String type,String code) {
		SystemCodeEntity entity = null;
		Cursor csr = SqliteUtils.getInstance().getCursor(getTableName(), Table.TYPE + "='" + type
				+"' and "+ Table.CODE +" ='"+code+"'");
		if(csr != null) {
			if(csr.moveToFirst()) {
				entity = refreshTableEntity(csr);
			}
			csr.close();
		}
		return entity;
	}

    /**
     * 查询记录
     * @param recordId
     */
    public SystemCodeEntity getRecord(long recordId) {
    	SystemCodeEntity entity = null;
    	Cursor csr = SqliteUtils.getInstance().getCursor(getTableName(), Table.ID + "=" + recordId);
    	if(csr != null) {
    		if(csr.moveToFirst()) {
    			entity = refreshTableEntity(csr);
    		}
    		csr.close();
    	}
    	return entity;
    }

	/**
	 * 查询记录
	 */
	public List<SystemCodeEntity> getRecordListPageByType(String codeType,String name,int index) {
		List<SystemCodeEntity> list = new ArrayList<>();
		//检索条件
		StringBuffer whereBf = new StringBuffer();
		//类型
		whereBf.append(Table.TYPE).append(" = '").append(codeType).append("'");
		//名称模糊查询
		if(StringUtil.isEmpty(name) == false){
			whereBf.append(" and ").append(Table.NAME).append(" like '%").append(name).append("%'");
		}
		//排序分页
		whereBf.append(" order by ").append(Table.ID).append(" limit ").append(PAGE_SIZE).append(" offset ").append((index-1)*PAGE_SIZE);

		Cursor csr = SqliteUtils.getInstance().getCursor(getTableName(), whereBf.toString());
		if(csr != null) {
			while (csr.moveToNext()){
				list.add(refreshTableEntity(csr));
			}
			csr.close();
		}
		return list;
	}

	/**
	 * 查询记录
	 */
	public List<SystemCodeEntity> getRecordListByType(String codeType,String name) {
		//列表
		List<SystemCodeEntity> list = new ArrayList<>();
		//查询条件
		StringBuffer whereBf = new StringBuffer();
		//类型
		whereBf.append(Table.TYPE).append(" = '").append(codeType).append("'");
		//名称模糊查询
		if(StringUtil.isEmpty(name) == false){
			whereBf.append(" and ").append(Table.NAME).append(" like '%").append(name).append("%'");
		}
		//排序分页
		whereBf.append(" order by ").append(Table.ID);

		Cursor csr = SqliteUtils.getInstance().getCursor(getTableName(), whereBf.toString());
		if(csr != null) {
			while (csr.moveToNext()){
				list.add(refreshTableEntity(csr));
			}
			csr.close();
		}
		return list;
	}

	/**
	 * 查询记录
	 */
	public String[] getRecordNameArray(String codeType,String name) {
		//列表
		String[] list;
		//查询条件
		StringBuffer whereBf = new StringBuffer();
		//类型
		whereBf.append(Table.TYPE).append(" = '").append(codeType).append("'");
		//名称模糊查询
		if(StringUtil.isEmpty(name) == false){
			whereBf.append(" and ").append(Table.NAME).append(" like '%").append(name).append("%'");
		}
		//排序分页
		whereBf.append(" order by ").append(Table.ID);

		list = SqliteUtils.getInstance().getStringArray(Table.NAME, getTableName(), whereBf.toString());

		return list;
	}
}
