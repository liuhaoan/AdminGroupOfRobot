package pers.liuhaoan.jvtcGroupOfRobot.service;

import pers.liuhaoan.jvtcGroupOfRobot.domain.Course;
import pers.liuhaoan.jvtcGroupOfRobot.enums.WeekEnum;

import java.util.List;

/**
 * 学校官网教务管理系统操作类
 */
public interface JvtcJiaoWuService {

    /**
     * 获取一星期中某天的课程
     * @param weekEnum 星期一到日的枚举
     * @return 返回当天课程
     */
    List<Course> getCourseDayToWeek(WeekEnum weekEnum);

    /**
     * 获取明天课表
     * @return 返回明天课表集合
     */
    List<Course> getCourseTomorrow();

    /**
     * 获取当前课表（今天课表）
     * @return 返回课表集合
     */
    List<Course> getCourseNow();

    /**
     * 获取当前的课表
     * @param nextDay 是否是明天的课表
     * @return 返回本周课程的列表
     */
    List<Course> getThisWeekCourse(boolean nextDay);

}
