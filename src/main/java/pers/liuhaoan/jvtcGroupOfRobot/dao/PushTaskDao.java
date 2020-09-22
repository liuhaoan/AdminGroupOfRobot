package pers.liuhaoan.jvtcGroupOfRobot.dao;

import pers.liuhaoan.jvtcGroupOfRobot.domain.PushTaskInfo;

import java.util.List;

public interface PushTaskDao {

    /**
     * 查询某个推送群号的任务信息
     * @param groupId 群号
     * @return 返回任务信息
     */
    PushTaskInfo findTask(String groupId);

    /**
     * 查询所有任务的推送群号
     * @return 群号列表
     */
    List<String> findGroupIdAll();

    /**
     * 查询所有任务
     * @return 返回任务集合
     */
    List<PushTaskInfo> findTaskAll();


    /**
     * 新增任务
     * @param pushTaskInfo 任务信息
     * @return 返回新建记录的自增id，失败返回-1
     */
    int addTask(PushTaskInfo pushTaskInfo);



    /**
     * 根据id删除任务
     * @return 成功失败
     */
    boolean delTask(int id);

    /**
     * 根据唯一的授权群号删除任务
     * @return 成功失败
     */
    boolean delTask(String groupId);


    /**
     * 修改任务
     * @param pushTaskInfochange 修改后的信息
     * @return 返回结果
     */
    boolean reTask(PushTaskInfo pushTaskInfochange);


    /**
     * 根据群号查询是否已存在
     * @param pushTaskInfo 任务信息
     * @return 成功失败
     */
    boolean findPushTaskExist(PushTaskInfo pushTaskInfo);

}
