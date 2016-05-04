/**
 * 
 */
package com.haozi.idscaner2016.client.data.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.haozi.idscaner2016.R;
import com.haozi.idscaner2016.client.bean.EntityDitionary;
import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.client.data.sqlite.base.BaseTable;
import com.haozi.idscaner2016.client.data.sqlite.base.BaseTableBody;
import com.haozi.idscaner2016.client.data.sqlite.base.SqliteUtils;
import com.haozi.idscaner2016.client.utils.ViewUtils;
import com.haozi.idscaner2016.common.utils.DateUtil;
import com.haozi.idscaner2016.common.utils.StringUtil;
import com.haozi.idscaner2016.constants.Configeration;

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
public class VisitRecordTable extends BaseTable<VisitRecordEntity> {

	private static final int PAGE_SIZE = 20;

	/** 静态单例初始化 */
	private static class SingletonHolder {
		/** 静态初始化器，由JVM来保证线程安全 */
		private static VisitRecordTable instance = new VisitRecordTable();
	}

	/** 单例静态引用 */
	public static VisitRecordTable getInstance() {
		return SingletonHolder.instance;
	}
	
	private class Table extends BaseTableBody {
		/** 姓名 */
		public static final String IDCARD_NAME = "IDCARD_NAME";
		/** 性别 */
		public static final String IDCARD_SEX = "IDCARD_SEX";
		/** 国籍 */
		public static final String IDCARD_NATION = "IDCARD_NATION";
		/** 出生日期 */
		public static final String IDCARD_BIRTHDAY = "IDCARD_BIRTHDAY";
		/** 住址 */
		public static final String IDCARD_ADDRESS = "IDCARD_ADDRESS";
		/** ID号 */
		public static final String IDCARD_IDNUM = "IDCARD_IDNUM";
		/** 头像路径 */
		public static final String IDCARD_PHOTO = "IDCARD_PHOTO";

		/** 来访时间 */
		public static final String VISIT_TIME = "VISIT_TIME";
		/** 来访单位 */
		public static final String VISIT_UNIT = "VISIT_UNIT";
		/** 联系方式 */
		public static final String VISIT_CONTRACT = "VISIT_CONTRACT";
		/** 车牌号 */
		public static final String VISIT_CARNUM = "VISIT_CARNUM";
		/** 被访人 */
		public static final String VISIT_BEVISITED = "VISIT_BEVISITED";
		/** 来访事由 */
		public static final String VISIT_RESON = "VISIT_RESON";
		/** 来访签字 */
		public static final String VISIT_SIGN = "VISIT_SIGN";

		/** 条形码 */
		public static final String VISIT_CHECKCODE = "VISIT_CHECKCODE";

		/** 离开时间 */
		public static final String VISIT_LEAVETIME = "VISIT_LEAVETIME";
		/** 离开签字 */
		public static final String VISIT_LEAVESIGN = "VISIT_LEAVESIGN";

	}
	
	/**
	 * @return
	 */
	public String getCreateTableSql() {
		StringBuffer sql = new StringBuffer();
		sql.append(Table.IDCARD_NAME).append(" TEXT, ");
		sql.append(Table.IDCARD_SEX).append(" TEXT, ");
		sql.append(Table.IDCARD_NATION).append(" TEXT, ");
		sql.append(Table.IDCARD_BIRTHDAY).append(" TEXT, ");
		sql.append(Table.IDCARD_ADDRESS).append(" TEXT, ");
		sql.append(Table.IDCARD_IDNUM).append(" TEXT, ");
		sql.append(Table.IDCARD_PHOTO).append(" TEXT, ");
		sql.append(Table.VISIT_TIME).append(" TEXT, ");
		sql.append(Table.VISIT_UNIT).append(" TEXT, ");
		sql.append(Table.VISIT_CONTRACT).append(" TEXT, ");
		sql.append(Table.VISIT_CARNUM).append(" TEXT, ");
		sql.append(Table.VISIT_BEVISITED).append(" TEXT, ");
		sql.append(Table.VISIT_RESON).append(" TEXT, ");
		sql.append(Table.VISIT_SIGN).append(" TEXT, ");
		sql.append(Table.VISIT_CHECKCODE).append(" TEXT, ");
		sql.append(Table.VISIT_LEAVETIME).append(" TEXT, ");
		sql.append(Table.VISIT_LEAVESIGN).append(" TEXT ");
		return SqliteUtils.getInstance().getCreateSql(getTableName(), sql.toString());
	}

    /**
	 * @param conver
	 * @return
	 */
	private ContentValues putContentValues(VisitRecordEntity conver) {
		return putContentValues(conver, new ContentValues());
	}
	
	@Override
	protected ContentValues putContentValues(VisitRecordEntity tableEntity, ContentValues cv) {
		//cv.put(Table.ID, tableEntity.getId());
		cv.put(Table.IDCARD_NAME, tableEntity.getName());
		cv.put(Table.IDCARD_SEX, tableEntity.getSex());
		cv.put(Table.IDCARD_NATION, tableEntity.getNation());
		cv.put(Table.IDCARD_BIRTHDAY,tableEntity.getBirthday());
		cv.put(Table.IDCARD_ADDRESS,tableEntity.getAddress());
		cv.put(Table.IDCARD_IDNUM,tableEntity.getIdNum());
		cv.put(Table.IDCARD_PHOTO,tableEntity.getPhoto());
		cv.put(Table.VISIT_TIME,tableEntity.getVisitTime());
		cv.put(Table.VISIT_UNIT,tableEntity.getVisitUnit());
		cv.put(Table.VISIT_CONTRACT,tableEntity.getVisitContract());
		cv.put(Table.VISIT_CARNUM,tableEntity.getVisitCarnum());
		cv.put(Table.VISIT_BEVISITED,tableEntity.getBeVisited());
		cv.put(Table.VISIT_RESON,tableEntity.getVisitReson());
		cv.put(Table.VISIT_SIGN,tableEntity.getVisitSign());
		cv.put(Table.VISIT_CHECKCODE,tableEntity.getCheckCode());
		cv.put(Table.VISIT_LEAVETIME,tableEntity.getLeaveTime());
		cv.put(Table.VISIT_LEAVESIGN,tableEntity.getLeaveSign());
		return cv;
	}

	/**
	 * @param csr
	 * @return
	 */
	private VisitRecordEntity refreshTableEntity(Cursor csr) {
		return refreshTableEntity(new VisitRecordEntity(), csr);
	}
	
	@Override
	protected VisitRecordEntity refreshTableEntity( VisitRecordEntity tableEntity, Cursor csr) {
		if(tableEntity == null){
			return tableEntity;
		}
		tableEntity.setId(SqliteUtils.getInstance().getLongColumn(csr, Table.ID));
		tableEntity.setName(SqliteUtils.getInstance().getStringColumn(csr, Table.IDCARD_NAME));
		tableEntity.setSex(SqliteUtils.getInstance().getStringColumn(csr, Table.IDCARD_SEX));
		tableEntity.setNation(SqliteUtils.getInstance().getStringColumn(csr, Table.IDCARD_NATION));
		tableEntity.setBirthday(SqliteUtils.getInstance().getStringColumn(csr, Table.IDCARD_BIRTHDAY));
		tableEntity.setAddress(SqliteUtils.getInstance().getStringColumn(csr, Table.IDCARD_ADDRESS));
		tableEntity.setIdNum(SqliteUtils.getInstance().getStringColumn(csr, Table.IDCARD_IDNUM));
		tableEntity.setPhoto(SqliteUtils.getInstance().getStringColumn(csr, Table.IDCARD_PHOTO));
		tableEntity.setVisitTime(SqliteUtils.getInstance().getLongColumn(csr, Table.VISIT_TIME));
		tableEntity.setVisitUnit(SqliteUtils.getInstance().getStringColumn(csr, Table.VISIT_UNIT));
		tableEntity.setVisitContract(SqliteUtils.getInstance().getStringColumn(csr, Table.VISIT_CONTRACT));
		tableEntity.setVisitCarnum(SqliteUtils.getInstance().getStringColumn(csr, Table.VISIT_CARNUM));
		tableEntity.setBeVisited(SqliteUtils.getInstance().getStringColumn(csr, Table.VISIT_BEVISITED));
		tableEntity.setVisitReson(SqliteUtils.getInstance().getStringColumn(csr, Table.VISIT_RESON));
		tableEntity.setVisitSign(SqliteUtils.getInstance().getStringColumn(csr, Table.VISIT_SIGN));
		tableEntity.setCheckCode(SqliteUtils.getInstance().getStringColumn(csr, Table.VISIT_CHECKCODE));
		tableEntity.setLeaveTime(SqliteUtils.getInstance().getLongColumn(csr, Table.VISIT_LEAVETIME));
		tableEntity.setLeaveSign(SqliteUtils.getInstance().getStringColumn(csr, Table.VISIT_LEAVESIGN));

		return tableEntity;
	}

    /**
     * 插入更新记录
     * @param msg
     */
    public void insertUpdateRecord(VisitRecordEntity msg) {
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
	public void updateRecordById(VisitRecordEntity msg) {
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
	public void updateRecordByIDNum(VisitRecordEntity msg) {
		if(msg == null){
			return;
		}
		ContentValues cv = putContentValues(msg);
		if(msg.getId() > 0){
			SqliteUtils.getInstance().update(cv, getTableName(), Table.IDCARD_IDNUM + "='" + msg.getIdNum()+"'");
		}else{
			return;
		}
	}

	/**
	 * 更新记录
	 * @param msg
	 */
	public void updateRecordByCheckCode(VisitRecordEntity msg) {
		if(msg == null){
			return;
		}
		ContentValues cv = putContentValues(msg);
		if(msg.getId() > 0){
			SqliteUtils.getInstance().update(cv, getTableName(), Table.IDCARD_IDNUM + "='" + msg.getIdNum()+"'");
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
     * @param idNum
     */
    public VisitRecordEntity getRecord(String idNum) {
		VisitRecordEntity entity = null;
		Cursor csr = SqliteUtils.getInstance().getCursor(getTableName(),Table.IDCARD_IDNUM + "='" + idNum +"'");
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
    public VisitRecordEntity getRecord(long recordId) {
    	VisitRecordEntity entity = null;
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
	public List<VisitRecordEntity> getRecordList(String dateStr,String idnum,String visitorname,String bevisited,String carnum,int index) {
		List<VisitRecordEntity> list = new ArrayList<>();
		StringBuffer whereBf = new StringBuffer(Table.ID).append(" >= 0");
		//日期范围
		if(!StringUtil.isEmpty(dateStr)){
			Date date = DateUtil.StrYYYYMMMDDToDate(dateStr);
			if(date != null){
				GregorianCalendar curdar = new GregorianCalendar(Locale.getDefault());
				curdar.setTimeInMillis(date.getTime());
				GregorianCalendar todayDarStart = new GregorianCalendar(
						curdar.get(GregorianCalendar.YEAR),
						curdar.get(GregorianCalendar.MONTH),
						curdar.get(GregorianCalendar.DAY_OF_MONTH));
				GregorianCalendar todayDarEnd = new GregorianCalendar(
						curdar.get(GregorianCalendar.YEAR),
						curdar.get(GregorianCalendar.MONTH),
						curdar.get(GregorianCalendar.DAY_OF_MONTH)+1);
				whereBf.append(" and ").append(Table.VISIT_TIME).append(" >= ").append(todayDarStart.getTimeInMillis());
				whereBf.append(" and ").append(Table.VISIT_TIME).append(" <= ").append(todayDarEnd.getTimeInMillis());
			}
		}
		if(!StringUtil.isEmpty(idnum)){
			whereBf.append(" and ").append(Table.IDCARD_IDNUM).append(" like '%").append(idnum).append("%'");
		}
		if(!StringUtil.isEmpty(visitorname)){
			whereBf.append(" and ").append(Table.IDCARD_NAME).append(" like '%").append(visitorname).append("%'");
		}
		if(!StringUtil.isEmpty(bevisited)){
			whereBf.append(" and ").append(Table.VISIT_BEVISITED).append(" like '%").append(bevisited).append("%'");
		}
		if(!StringUtil.isEmpty(carnum)){
			whereBf.append(" and ").append(Table.VISIT_CARNUM).append(" like '%").append(carnum).append("%'");
		}
		//排序分页
		whereBf.append(" order by ").append(Table.VISIT_TIME).append(" desc limit ").append(PAGE_SIZE).append(" offset ").append((index-1)*PAGE_SIZE);

		Cursor csr = SqliteUtils.getInstance().getCursor(getTableName(), whereBf.toString());
		if(csr != null) {
			while (csr.moveToNext()){
				list.add(refreshTableEntity(csr));
			}
			csr.close();
		}
		return list;
	}

	public int countVisitors(long startTime,long enTime){
		String where = Table.VISIT_TIME + ">=" + startTime + " and "+Table.VISIT_TIME + "<=" + enTime;
		int nowcount = SqliteUtils.getInstance().count(getTableName(),where);
		return nowcount;
	}

	public int countVisitorsLeave(long startTime,long enTime){
		String where = Table.VISIT_TIME + ">=" + startTime + " and "+Table.VISIT_TIME + "<=" + enTime
				+ " and " + Table.VISIT_LEAVETIME + " > 0";
		int nowcount = SqliteUtils.getInstance().count(getTableName(),where);
		return nowcount;
	}

	public int countVisitorsNotLeave(long startTime,long enTime){
		String where = Table.VISIT_TIME + ">=" + startTime + " and "+Table.VISIT_TIME + "<=" + enTime
				+ " and " + Table.VISIT_LEAVETIME + " <= 0";
		int nowcount = SqliteUtils.getInstance().count(getTableName(),where);
		return nowcount;
	}

}
