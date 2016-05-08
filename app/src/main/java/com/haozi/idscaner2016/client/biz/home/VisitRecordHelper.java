package com.haozi.idscaner2016.client.biz.home;

import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.client.data.sqlite.VisitRecordTable;
import com.haozi.idscaner2016.common.base.BaseObject;
import com.haozi.idscaner2016.common.utils.DateUtil;
import com.haozi.idscaner2016.common.utils.StringUtil;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Haozi on 2016/5/1.
 */
public class VisitRecordHelper extends BaseObject {

    /**静态单例初始化*/
    private static final VisitRecordHelper INSTANCE = new VisitRecordHelper();
    /**单例静态引用*/
    public static VisitRecordHelper getInstance() {
        return INSTANCE;
    }

    public String saveVisitInfoAndGiveCheckCode(VisitRecordEntity record){
        long recordId = saveVisitInfo(record);
        String checkCode = DateUtil.convertDateToYMDHMSShort(System.currentTimeMillis())+recordId;
        record.setCheckCode(checkCode);
        VisitRecordTable.getInstance().updateRecordByIDNum(record);
        return checkCode;
    }

    public long saveVisitInfo(VisitRecordEntity record) {
        VisitRecordTable.getInstance().insertUpdateRecord(record);
        VisitRecordEntity entity = VisitRecordTable.getInstance().getRecord(record.getIdNum());
        if(entity != null){
            return entity.getId();
        }
        return -1;
    }

    public void visiterLeave(String idNum){
        VisitRecordEntity entity = VisitRecordTable.getInstance().getRecord(idNum);
        entity.setLeaveTime(System.currentTimeMillis());
        VisitRecordTable.getInstance().updateRecordByIDNum(entity);
        DXToast.show("访客："+entity.getName() + "已于" + entity.getLeaveTimeStr() +"登记离开");
    }

    public String getCheckCode(long recordId){
        String checkCode = "";
        VisitRecordEntity entity = VisitRecordTable.getInstance().getRecord(recordId);
        if(entity == null){
            DXToast.show("生成条形码失败，请重新扫描证件");
            return checkCode;
        }
        checkCode = entity.getCheckCode();
        if(StringUtil.isEmpty(checkCode)){
            checkCode = DateUtil.convertDateToYMDShort(entity.getVisitTime())+recordId;
            entity.setCheckCode(checkCode);
            VisitRecordTable.getInstance().updateRecordById(entity);
        }
        return checkCode;
    }

    public int getVisitorCount(){
        GregorianCalendar curdar = new GregorianCalendar(Locale.getDefault());
        GregorianCalendar todayDar = new GregorianCalendar(
                curdar.get(GregorianCalendar.YEAR),
                curdar.get(GregorianCalendar.MONTH),
                curdar.get(GregorianCalendar.DAY_OF_MONTH));
        GregorianCalendar todayDarEnd = new GregorianCalendar(
                curdar.get(GregorianCalendar.YEAR),
                curdar.get(GregorianCalendar.MONTH),
                curdar.get(GregorianCalendar.DAY_OF_MONTH)+1);
        int vistorsToday = VisitRecordTable.getInstance().countVisitors(todayDar.getTimeInMillis(),todayDarEnd.getTimeInMillis());
        return vistorsToday;
    }

    public int getVisitorLeaveCount(){
        GregorianCalendar curdar = new GregorianCalendar(Locale.getDefault());
        GregorianCalendar todayDar = new GregorianCalendar(
                curdar.get(GregorianCalendar.YEAR),
                curdar.get(GregorianCalendar.MONTH),
                curdar.get(GregorianCalendar.DAY_OF_MONTH));
        GregorianCalendar todayDarEnd = new GregorianCalendar(
                curdar.get(GregorianCalendar.YEAR),
                curdar.get(GregorianCalendar.MONTH),
                curdar.get(GregorianCalendar.DAY_OF_MONTH)+1);
        int vistorsToday = VisitRecordTable.getInstance().countVisitorsLeave(todayDar.getTimeInMillis(),todayDarEnd.getTimeInMillis());
        return vistorsToday;
    }

    public int getVisitorNotLeaveCount(){
        GregorianCalendar curdar = new GregorianCalendar(Locale.getDefault());
        GregorianCalendar todayDar = new GregorianCalendar(
                curdar.get(GregorianCalendar.YEAR),
                curdar.get(GregorianCalendar.MONTH),
                curdar.get(GregorianCalendar.DAY_OF_MONTH));
        GregorianCalendar todayDarEnd = new GregorianCalendar(
                curdar.get(GregorianCalendar.YEAR),
                curdar.get(GregorianCalendar.MONTH),
                curdar.get(GregorianCalendar.DAY_OF_MONTH)+1);
        int vistorsToday = VisitRecordTable.getInstance().countVisitorsNotLeave(todayDar.getTimeInMillis(),todayDarEnd.getTimeInMillis());
        return vistorsToday;
    }

    public VisitRecordEntity getRecordByIdNum(String idNum){
        return VisitRecordTable.getInstance().getRecord(idNum);
    }

    public VisitRecordEntity getRecordByCheckCode(String idNum){
        return VisitRecordTable.getInstance().getRecord(idNum);
    }
}
