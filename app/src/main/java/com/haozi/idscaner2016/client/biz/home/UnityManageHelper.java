package com.haozi.idscaner2016.client.biz.home;

import com.haozi.idscaner2016.client.bean.EntityDitionary;
import com.haozi.idscaner2016.client.bean.client.SystemCodeEntity;
import com.haozi.idscaner2016.client.bean.client.VisitRecordEntity;
import com.haozi.idscaner2016.client.control.DXToast;
import com.haozi.idscaner2016.client.data.sqlite.SystemCodeTable;
import com.haozi.idscaner2016.client.data.sqlite.VisitRecordTable;
import com.haozi.idscaner2016.common.base.BaseObject;
import com.haozi.idscaner2016.common.utils.DateUtil;
import com.haozi.idscaner2016.common.utils.StringUtil;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Haozi on 2016/5/1.
 */
public class UnityManageHelper extends BaseObject {

    /**静态单例初始化*/
    private static final UnityManageHelper INSTANCE = new UnityManageHelper();
    /**单例静态引用*/
    public static UnityManageHelper getInstance() {
        return INSTANCE;
    }

    public List<SystemCodeEntity> getRecordListByType(String name){
        List<SystemCodeEntity> list = SystemCodeTable.getInstance().getRecordListByType(EntityDitionary.SysCodeType.unit.getValue(),name);
        if(list == null){
            return new ArrayList<>();
        }
        return list;
    }

    public String[] getRecordNameArray(){
        String[] list = SystemCodeTable.getInstance().getRecordNameArray(EntityDitionary.SysCodeType.unit.getValue(),null);
        if(list == null){
            return new String[]{};
        }
        return list;
    }

    public void deleteUnit(long sysID){
       SystemCodeTable.getInstance().deleteRecord(sysID);
    }

    public void updateUnit(SystemCodeEntity item){
       SystemCodeTable.getInstance().updateRecordById(item);
    }

    public void addUnit(String unitname){
        SystemCodeEntity entity = new SystemCodeEntity();
        entity.setName(unitname);
        entity.setCode(String.valueOf((int)(1+Math.random()*(100000-1+1))));
        entity.setType(EntityDitionary.SysCodeType.unit.getValue());
        entity.setCreateTime(System.currentTimeMillis());
       SystemCodeTable.getInstance().insertUpdateRecord(entity);
    }
}
