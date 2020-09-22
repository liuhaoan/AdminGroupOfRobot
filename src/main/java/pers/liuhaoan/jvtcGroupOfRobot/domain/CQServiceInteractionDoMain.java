package pers.liuhaoan.jvtcGroupOfRobot.domain;

import pers.liuhaoan.jvtcGroupOfRobot.enums.CQKeyEnum;
import pers.liuhaoan.jvtcGroupOfRobot.enums.CQTypeEnum;

public class CQServiceInteractionDoMain {
    private CQKeyEnum key;
    private CQTypeEnum type;
    private String groupId;
    private String friendId;
    private String msg;
    private String fromAccount;
    private String fromGroup;
    private String flag;

    @Override
    public String toString() {
        return "CQServiceInteractionDoMain{" +
                "key=" + key +
                ", type=" + type +
                ", groupId='" + groupId + '\'' +
                ", friendId='" + friendId + '\'' +
                ", msg='" + msg + '\'' +
                ", fromAccount='" + fromAccount + '\'' +
                ", fromGroup='" + fromGroup + '\'' +
                ", flag='" + flag + '\'' +
                '}';
    }


    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setKey(CQKeyEnum key) {
        this.key = key;
    }

    public void setType(CQTypeEnum type) {
        this.type = type;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public void setFromGroup(String fromGroup) {
        this.fromGroup = fromGroup;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public CQKeyEnum getKey() {
        return key;
    }

    public CQTypeEnum getType() {
        return type;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getMsg() {
        return msg;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getFromGroup() {
        return fromGroup;
    }

    public String getFlag() {
        return flag;
    }
}
