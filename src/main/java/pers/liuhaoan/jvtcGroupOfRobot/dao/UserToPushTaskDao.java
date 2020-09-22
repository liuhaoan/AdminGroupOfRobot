package pers.liuhaoan.jvtcGroupOfRobot.dao;

import pers.liuhaoan.jvtcGroupOfRobot.domain.PushTaskInfo;
import pers.liuhaoan.jvtcGroupOfRobot.domain.User;

import java.util.List;



public interface UserToPushTaskDao {
    /**
     * 查询某个用户的所有任务
     * @param user 用户信息
     * @return 返回任务列表
     */
    List<PushTaskInfo> findUserAllTask(User user);


    /**
     * 查询某个用户总任务数
     * @param user user信息
     * @return 总记录数
     */
    int findUserAllTaskTotalCount(User user);


    /**
     * 根据用户信息查询某个偏移量的所有任务
     * @param user user信息
     * @param index 偏移
     * @param count 取出的数量
     * @return 任务信息集合
     */
    List<PushTaskInfo> findUserTaskLimit(User user, int index, int count);

    /**
     * 添加某个用户与某个任务的关联
     * @param id 任务信息的id号
     * @param username 用户信息
     * @return 返回自增id， -1表示失败
     */
    boolean addUserToPushTesk(int id, String username);
}
