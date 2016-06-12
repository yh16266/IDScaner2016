package com.haozi.idscaner2016.client.biz.home;

import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.client.data.sqlite.VisitRecordTable;
import com.haozi.idscaner2016.common.base.BaseObject;
import com.haozi.idscaner2016.common.utils.DateUtil;
import com.haozi.idscaner2016.common.utils.StringUtil;
import com.haozi.idscaner2016.constants.IConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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
        VisitRecordEntity entity = VisitRecordTable.getInstance().getRecord(record.getIdNum(),record.visitTime);
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

    public VisitRecordEntity getRecordNotLeave(String idNum){
        return VisitRecordTable.getInstance().getRecordNotLeave(idNum);
    }

    public VisitRecordEntity getRecordByCheckCode(String checkCode){
        return VisitRecordTable.getInstance().getRecordByCheckCode(checkCode);
    }

    public VisitRecordEntity getRecordByIdNum(String mIdNum) {
        return VisitRecordTable.getInstance().getRecord(mIdNum);
    }

    /**
     * 追加文件：使用FileOutputStream，在构造FileOutputStream时，把第二个参数设为true
     * @param fileName
     * @param content
     */
    public void writeStrToFile(String fileName, String content) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName, true)));
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void outputRecord(List<VisitRecordEntity> list){
        BufferedWriter out = null;
        try {
            String ymdStr = DateUtil.convertDateToYMDShort(System.currentTimeMillis());
            File file = new File(IConstants.PROJECT_DIR + File.separator);
            if(!file.exists()) {
                file.mkdirs();
            }
            String signFilePath = IConstants.PROJECT_DIR + File.separator + ymdStr +"_output.txt";
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(signFilePath, true), "gbk"));
            for(VisitRecordEntity item:list){
                if(item == null){
                    continue;
                }
                String content = getRecrodStr(item);
                out.write(content);
                out.write("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getRecrodStr(VisitRecordEntity recordEntity){
        StringBuffer rst = new StringBuffer();
        rst.append(recordEntity.getName()).append(",");
        rst.append(recordEntity.getIdNum()).append(",");
        rst.append(DateUtil.convertDateYYYYMMddHHmm(recordEntity.getVisitTime())).append(",");
        rst.append(recordEntity.getVisitUnit()).append(",");
        rst.append(recordEntity.getVisitContract()).append(",");
        rst.append(recordEntity.getVisitCarnum()).append(",");
        rst.append(recordEntity.getBeVisited()).append(",");
        rst.append(recordEntity.getVisitReson()).append("");
        return rst.toString();
    }
}
