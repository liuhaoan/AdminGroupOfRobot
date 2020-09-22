package pers.liuhaoan.jvtcGroupOfRobot.service;

import pers.liuhaoan.jvtcGroupOfRobot.service.impl.CQServiceImpl;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;


/**
 * 与酷Q机器人交互的服务，私有化了构造方法，只能创建一个对象
 */
public interface CQService {
    //用来保存与酷Q机器人交互的常量
    CQService CQ = CQServiceImpl.CQ;


    /**
     * 获取服务器状态
     * @return True：正常运行中   False：服务未启动
     */
    Boolean getServiceStatus();

    /**
     * 销毁与机器人的交互
     */
    void destTroyed();

    /**
     * 初始化与酷Q机器人的交互
     */
    Boolean init();

    /**
     * 用来发送私聊消息
     * @param friendId QQ号
     * @param msg 消息
     */
    void sendFriendMsg(String friendId, String msg);

    /**
     * 用来发送群消息
     * @param groupId 群号
     * @param msg 消息
     */
     void sendGroupMsg(String groupId, String msg);

    /**
     * 收到好友请求
     * @param fromAccount 来源账户（要加你的QQ号）
     * @param flag 这条申请的标识
     */
    void addFriend(String fromAccount, String flag);

    /**
     * 收到群邀请
     * @param fromAccount 来源账户（邀请你的人的QQ号）
     * @param fromGroup 邀请你进的群
     * @param flag 这条邀请的标识
     */
    void addGroup(String fromAccount, String fromGroup, String flag);

    /**
     * 好友消息
     * @param fromAccount 来源QQ号
     * @param msg 消息文本
     */
    void friendMsg(String fromAccount, String msg);

    /**
     * 群消息
     * @param fromAccount 群号
     * @param msg 消息文本
     */
    void groupMsg(String fromAccount, String msg);

    /**
     * 封装了一下jackson，对象转Json文本数据
     * @param object 需要转的对象
     * @return 返回json
     */
    String objectToJson(Object object);

    /**
     * 封装了一下jackson，json文本数据转对象
     * @param json json数据
     * @return 返回对象
     */
    Object jsonToObject(String json, Class type);
}
