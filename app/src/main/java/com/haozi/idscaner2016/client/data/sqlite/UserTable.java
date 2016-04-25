/**
 * 
 */
package com.haozi.idscaner2016.client.data.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.haozi.idscaner2016.client.bean.EntityDitionary;
import com.haozi.idscaner2016.client.bean.client.Accounts;
import com.haozi.idscaner2016.client.data.sqlite.base.BaseTable;
import com.haozi.idscaner2016.client.data.sqlite.base.BaseTableBody;
import com.haozi.idscaner2016.client.data.sqlite.base.SqliteUtils;
import com.haozi.idscaner2016.constants.Configeration;

/**
 * 类名：MessageTable
 * @author yinhao
 * @功能
 * @创建日期 2016年1月12日 下午4:36:20
 * @备注 [修改者，修改日期，修改内容]
 */
public class UserTable extends BaseTable<Accounts> {
	
	/** 静态单例初始化 */
	private static class SingletonHolder {
		/** 静态初始化器，由JVM来保证线程安全 */
		private static UserTable instance = new UserTable();
	}

	/** 单例静态引用 */
	public static UserTable getInstance() {
		return SingletonHolder.instance;
	}
	
	private class Table extends BaseTableBody {
		/** 用户ID */
		public static final String USER_ID = "USER_ID";
		/** 用户名 */
		public static final String ACCOUNT = "ACCOUNT";
		/** 登录密码 */
		public static final String PASSWORD = "PASSWORD";
		/** 真实姓名 */
		public static final String REAL_NAME = "REAL_NAME";
		/** 昵称 */
		public static final String NICK_NAME = "NICK_NAME";
		/** 头像路径 */
		public static final String PHOTOURL = "PHOTOURL";
		/** 头像路径 */
		public static final String USER_TYPE = "USER_TYPE";
	}
	
	/**
	 * @return
	 */
	public String getCreateTableSql() {
		StringBuffer sql = new StringBuffer();
		sql.append(Table.USER_ID).append(" BIGINT, ");
		sql.append(Table.ACCOUNT).append(" TEXT, ");
		sql.append(Table.PASSWORD).append(" TEXT, ");
		sql.append(Table.REAL_NAME).append(" TEXT, ");
		sql.append(Table.NICK_NAME).append(" TEXT, ");
		sql.append(Table.PHOTOURL).append(" TEXT, ");
		sql.append(Table.USER_TYPE).append(" TEXT ");
		return SqliteUtils.getInstance().getCreateSql(getTableName(), sql.toString());
	}

    /**
	 * @param conver
	 * @return
	 */
	private ContentValues putContentValues(Accounts conver) {
		return putContentValues(conver, new ContentValues());
	}
	
	@Override
	protected ContentValues putContentValues(Accounts tableEntity, ContentValues cv) {
		//cv.put(Table.ID, tableEntity.getId());
		cv.put(Table.USER_ID, tableEntity.getUserId());
		cv.put(Table.ACCOUNT, tableEntity.getUsername());
		cv.put(Table.PASSWORD, tableEntity.getPassword());
		cv.put(Table.REAL_NAME, tableEntity.getRealname());
		cv.put(Table.NICK_NAME, tableEntity.getNickname());
		cv.put(Table.PHOTOURL, tableEntity.getPhotoUrl());
		cv.put(Table.USER_TYPE, tableEntity.getUserType());
		return cv;
	}

	/**
	 * @param csr
	 * @return
	 */
	private Accounts refreshTableEntity(Cursor csr) {
		return refreshTableEntity(new Accounts(), csr);
	}
	
	@Override
	protected Accounts refreshTableEntity( Accounts tableEntity, Cursor csr) {
		if(tableEntity == null){
			return tableEntity;
		}
		tableEntity.setId(SqliteUtils.getInstance().getLongColumn(csr, Table.ID));
		tableEntity.setUserId(SqliteUtils.getInstance().getLongColumn(csr, Table.USER_ID));
		tableEntity.setUsername(SqliteUtils.getInstance().getStringColumn(csr, Table.ACCOUNT));
		tableEntity.setPassword(SqliteUtils.getInstance().getStringColumn(csr, Table.PASSWORD));
		tableEntity.setRealname(SqliteUtils.getInstance().getStringColumn(csr, Table.REAL_NAME));
		tableEntity.setNickname(SqliteUtils.getInstance().getStringColumn(csr, Table.NICK_NAME));
		tableEntity.setPhotoUrl(SqliteUtils.getInstance().getStringColumn(csr, Table.PHOTOURL));
		tableEntity.setUserType(SqliteUtils.getInstance().getStringColumn(csr, Table.USER_TYPE));
		return tableEntity;
	}

    /**
     * 插入更新记录
     * @param msg
     */
    public void insertUpdateRecord(Accounts msg) {
    	if(msg == null){
    		return;
    	}
    	ContentValues cv = putContentValues(msg);
    	if(msg.getId() > 0){
    		SqliteUtils.getInstance().insertUpdate(cv, getTableName(), Table.ACCOUNT + "='" + msg.getUsername()+"'");
    	}else{
    		SqliteUtils.getInstance().insert(cv, getTableName());
    	}
    }

	/**
     * 删除记录
     * @param account
     */
    public void deleteRecord(String account) {
    	SqliteUtils.getInstance().delete(getTableName(), Table.ACCOUNT + "='" + account +"'");
    }
    
    /**
     * 查询记录
     * @param account
     */
    public Accounts getRecord(String account) {
		Accounts entity = null;
		Cursor csr = SqliteUtils.getInstance().getCursor(getTableName(), Table.ACCOUNT + "='" + account +"'");
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
     * @param account
     */
    public Accounts login(String account,String password) {
		Accounts entity = null;
		Cursor csr = SqliteUtils.getInstance().getCursor(getTableName(), Table.ACCOUNT + "='" + account
				+"' AND "+ Table.PASSWORD  + "='" +password +"'");
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
     * @param account
     */
    public Accounts getRecord(long account) {
    	Accounts entity = null;
    	Cursor csr = SqliteUtils.getInstance().getCursor(getTableName(), Table.USER_ID + "=" + account);
    	if(csr != null) {
    		if(csr.moveToFirst()) {
    			entity = refreshTableEntity(csr);
    		}
    		csr.close();
    	}
    	return entity;
    }

	public void initUserData(){
		Accounts admin = getRecord(Configeration.USER_ADMIN);
		if(admin == null){
			Accounts newAdmin = new Accounts();
			newAdmin.setNickname("管理员");
			newAdmin.setUserId(Configeration.USER_ADMIN);
			newAdmin.setUsername(Configeration.USER_ADMIN_ACCOUNT);
			newAdmin.setPassword(Configeration.USER_ADMIN_PSW);
			newAdmin.setUserType(EntityDitionary.UserType.admin.getValueStr());
			insertUpdateRecord(newAdmin);
		}
		Accounts secure = getRecord(Configeration.USER_SECURE);
		if(secure == null){
			Accounts newSecure = new Accounts();
			newSecure.setNickname("保安");
			newSecure.setUserId(Configeration.USER_SECURE);
			newSecure.setUsername(Configeration.USER_SECURE_ACCOUNT);
			newSecure.setPassword(Configeration.USER_SECURE_PSW);
			newSecure.setUserType(EntityDitionary.UserType.secure.getValueStr());
			insertUpdateRecord(newSecure);
		}
	}
}
