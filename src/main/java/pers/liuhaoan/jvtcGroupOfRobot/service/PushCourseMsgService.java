package pers.liuhaoan.jvtcGroupOfRobot.service;

import pers.liuhaoan.jvtcGroupOfRobot.domain.Course;
import pers.liuhaoan.jvtcGroupOfRobot.service.impl.PushCourseMsgServiceImpl;

import java.util.List;

/**
 * 推送课程信息的服务，私有化了构造方法，只能创建一个对象
 * 初始化步骤：
 * 1、获取redis数据库中的list
 */
public interface PushCourseMsgService {
    //定义一个自己的类，并且不让别人创建本类
    PushCourseMsgService service = PushCourseMsgServiceImpl.service;


    /**
     * 初始化推送服务
     * @return 是否成功初始化
     */
    Boolean init();

    /**
     * 获取服务状态信息
     * @return Str
     */
    String getServiceStautsInfo();

    /**
     * 获取服务器状态
     * @return true运行中，否则没运行
     */
    Boolean getServiceStauts();


    /**
     * 获取推送信息
     * @param pushCourseList 课表列表信息
     * @return 推送字符串信息
     */
    String getPushMsg(List<Course> pushCourseList, boolean isNowCourse);
}
