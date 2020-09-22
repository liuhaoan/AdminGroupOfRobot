package pers.liuhaoan.jvtcGroupOfRobot.domain;

import javax.servlet.annotation.WebFilter;

public class PushTaskInfo implements Cloneable{
    private String jiaoWuUsername;
    private String jiaoWuPassword;
    private String groupId;
    private String accountId;
    private String push_hour_min;
    private String className;
    private Boolean isPush;
    private Boolean isNowCourse;//是否推送今天课表，是：今天课表     否：明天课表
    private int id;//修改删除mysql中数据用

    private int sortKey;
    private int hour;
    private int minute;


    public PushTaskInfo() {
    }

    public PushTaskInfo(String jiaoWuUsername, String jiaoWuPassword, String groupId, String accountId, String push_hour_min, String className, Boolean isPush, Boolean isNowCourse, int id) {
        this.jiaoWuUsername = jiaoWuUsername;
        this.jiaoWuPassword = jiaoWuPassword;
        this.groupId = groupId;
        this.accountId = accountId;
        setPush_hour_min(push_hour_min);
        this.className = className;
        this.isPush = isPush;
        this.isNowCourse = isNowCourse;
        this.id = id;


    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "PushTaskInfo{" +
                "jiaoWuUsername='" + jiaoWuUsername + '\'' +
                ", jiaoWuPassword='" + jiaoWuPassword + '\'' +
                ", groupId='" + groupId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", push_hour_min='" + push_hour_min + '\'' +
                ", className='" + className + '\'' +
                ", isPush=" + isPush +
                ", isNowCourse=" + isNowCourse +
                ", id=" + id +
                ", sortKey=" + sortKey +
                ", hour=" + hour +
                ", minute=" + minute +
                '}';
    }

    //判断在mysql数据库中有的字段值是否为空，只要一个值为空，则表示这条信息无效
    public boolean isNull() {
        if (jiaoWuUsername == null)return true;
        if (jiaoWuPassword == null)return true;
        if (groupId == null)return true;
        if (accountId == null)return true;
        if (push_hour_min == null)return true;
        if (className == null)return true;
        if (isNowCourse == null)return true;
        if (isPush == null)return true;
       return false;
    }

    //在json与对象的互转时，有isNull方法时，对象转json会产生一个名为null的参数，加一个空的setNull方法用于json转对象不报错
    public void setNull(boolean isNull) {}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getJiaoWuUsername() {
        return jiaoWuUsername;
    }

    public String getJiaoWuPassword() {
        return jiaoWuPassword;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getPush_hour_min() {
        return push_hour_min;
    }

    public String getClassName() {
        return className;
    }

    public Boolean getIsPush() {
        return isPush;
    }

    public Boolean getIsNowCourse() {
        return isNowCourse;
    }

    public int getSortKey() {
        return sortKey;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setJiaoWuUsername(String jiaoWuUsername) {
        this.jiaoWuUsername = jiaoWuUsername;
    }

    public void setJiaoWuPassword(String jiaoWuPassword) {
        this.jiaoWuPassword = jiaoWuPassword;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setPush_hour_min(String push_hour_min) {
        //生成一个排序的数值
        String[] str = push_hour_min.split(":");
        if(str.length != 2) {
            hour = 0;
            minute = 0;
            sortKey = 0;
            return;
        }
        this.push_hour_min = push_hour_min;
        hour = Integer.parseInt(str[0]);
        minute = Integer.parseInt(str[1]);
        sortKey = (hour * 100) + minute;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setIsPush(Boolean push) {
        isPush = push;
    }

    public void setIsNowCourse(Boolean nowCourse) {
        isNowCourse = nowCourse;
    }
}
