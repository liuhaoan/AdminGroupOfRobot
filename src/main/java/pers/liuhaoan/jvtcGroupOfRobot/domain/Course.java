package pers.liuhaoan.jvtcGroupOfRobot.domain;

public class Course {
    private Integer sort;       //课程排序：第几节
    private Integer week;       //课程所在的星期
    private String time;        //课程时间：08:05-09:45
    private String info;        //课程信息
    private String courseClass; //课程所属班级

    public Course() {
    }

    public Course(Integer sort, Integer week, String time, String info, String courseClass) {
        this.sort = sort;
        this.week = week;
        this.time = time;
        this.info = info;
        this.courseClass = courseClass;
    }

    @Override
    public String toString() {
        return "Course{" +
                "sort=" + sort +
                ", week=" + week +
                ", time='" + time + '\'' +
                ", info='" + info + '\'' +
                ", courseClass='" + courseClass + '\'' +
                '}';
    }

    public Integer getSort() {
        return sort;
    }

    public Integer getWeek() {
        return week;
    }

    public String getTime() {
        return time;
    }

    public String getInfo() {
        return info;
    }

    public String getCourseClass() {
        return courseClass;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setCourseClass(String courseClass) {
        this.courseClass = courseClass;
    }
}
