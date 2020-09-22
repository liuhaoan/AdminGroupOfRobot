package pers.liuhaoan.jvtcGroupOfRobot.service;

import pers.liuhaoan.jvtcGroupOfRobot.domain.PageBean;
import pers.liuhaoan.jvtcGroupOfRobot.domain.PushTaskInfo;
import pers.liuhaoan.jvtcGroupOfRobot.domain.User;

import java.util.List;


/**
 * 任务管理器
 * 主要用它来调用dao层操作数据库，把任务信息存放在数据库中，实现任务的管理
 */
public interface TaskManagerSreivce {
    String PUSH_TESK_INFO = "pushTeskInfo";


    /**
     * 判断某个群号是否是任务中推送的群号
     * @return true是
     */
    boolean findGroupIdToTesk(String groupId);

    /**
     * 修改任务，先删Redis中的任务，然后修改数据库中的数据
     * @param pushTaskInfo 改变前的完整任务信息
     * @param pushTaskInfoChange 需要改变的信息，不改变为null
     */
    boolean reTesk(PushTaskInfo pushTaskInfo, PushTaskInfo pushTaskInfoChange);

    /**
     * 删除任务
     * @param pushTaskInfo 任务信息
     */
    boolean delTesk(PushTaskInfo pushTaskInfo);

    /**
     * 把任务信息添加到数据库
     * @param user 用户信息
     * @param pushTaskInfo 推送任务信息
     */
    Boolean addTeskToMysql(User user, PushTaskInfo pushTaskInfo);

    /**
     * 把任务信息添加到Redis
     * @param pushTaskInfo
     */
     boolean addTeskToRedis(PushTaskInfo pushTaskInfo);

    /**
     * 添加任务，整合了上面两个方法
     * @param user user信息
     * @param pushTaskInfo 推送信息
     * @return 成功失败
     */
     boolean addPushTesk(User user, PushTaskInfo pushTaskInfo);


    /**
     * 查询一个用户创建的所有推送任务
     * @return 返回任务信息列表
     */
    List<PushTaskInfo> findPushTask(User user);

    /**
     * 以分页的形式查询某个分页所有任务
     * @param pageBean 分页对象
     * @return 返回任务信息列表
     */
    PageBean<PushTaskInfo> findPushTask(User user, PageBean<PushTaskInfo> pageBean);

    /**
     * 根据授权群号查询此群是否已存在
     * @param pushTaskInfo 任务信息
     * @return 成功失败
     */
    boolean findGroupIdExist(PushTaskInfo pushTaskInfo);
}
