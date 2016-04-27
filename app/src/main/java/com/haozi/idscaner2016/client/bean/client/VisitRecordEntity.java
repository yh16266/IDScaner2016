package com.haozi.idscaner2016.client.bean.client;

import com.haozi.idscaner2016.client.bean.BaseEntity;

/**
 * Created by Haozi on 2016/4/28.
 */
public class VisitRecordEntity extends BaseEntity {

    /** 姓名 */
    public String name;
    /** 性别 */
    public String sex;
    /** 国籍 */
    public String nation;
    /** 出生日期 */
    public String birthday;
    /** 住址 */
    public String address;
    /** ID号 */
    public String idNum;
    /** 头像路径 */
    public String photo;

    /** 来访时间 */
    public String visitTime;
    /** 来访单位 */
    public String visitUnit;
    /** 联系方式 */
    public String visitContract;
    /** 车牌号 */
    public String visitCarnum;
    /** 被访人 */
    public String beVisited;
    /** 来访事由 */
    public String visitReson;
    /** 来访签字 */
    public String visitSign;

    /** 离开时间 */
    public String leaveTime;
    /** 离开签字 */
    public String leaveSign;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(String visitTime) {
        this.visitTime = visitTime;
    }

    public String getVisitUnit() {
        return visitUnit;
    }

    public void setVisitUnit(String visitUnit) {
        this.visitUnit = visitUnit;
    }

    public String getVisitContract() {
        return visitContract;
    }

    public void setVisitContract(String visitContract) {
        this.visitContract = visitContract;
    }

    public String getVisitCarnum() {
        return visitCarnum;
    }

    public void setVisitCarnum(String visitCarnum) {
        this.visitCarnum = visitCarnum;
    }

    public String getBeVisited() {
        return beVisited;
    }

    public void setBeVisited(String beVisited) {
        this.beVisited = beVisited;
    }

    public String getVisitReson() {
        return visitReson;
    }

    public void setVisitReson(String visitReson) {
        this.visitReson = visitReson;
    }

    public String getVisitSign() {
        return visitSign;
    }

    public void setVisitSign(String visitSign) {
        this.visitSign = visitSign;
    }

    public String getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }

    public String getLeaveSign() {
        return leaveSign;
    }

    public void setLeaveSign(String leaveSign) {
        this.leaveSign = leaveSign;
    }
}
