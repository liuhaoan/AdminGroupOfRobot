package pers.liuhaoan.jvtcGroupOfRobot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import pers.liuhaoan.jvtcGroupOfRobot.dao.PushTaskDao;
import pers.liuhaoan.jvtcGroupOfRobot.dao.impl.PushTaskDaoImpl;
import pers.liuhaoan.jvtcGroupOfRobot.domain.Course;
import pers.liuhaoan.jvtcGroupOfRobot.domain.PushTaskInfo;
import pers.liuhaoan.jvtcGroupOfRobot.service.CQService;
import pers.liuhaoan.jvtcGroupOfRobot.service.JvtcJiaoWuService;
import pers.liuhaoan.jvtcGroupOfRobot.service.PushCourseMsgService;
import pers.liuhaoan.jvtcGroupOfRobot.service.TaskManagerSreivce;
import pers.liuhaoan.jvtcGroupOfRobot.utils.JedisUtil;
import redis.clients.jedis.Jedis;


import java.io.IOException;
import java.util.*;

public class PushCourseMsgServiceImpl implements PushCourseMsgService {

    public static final PushCourseMsgServiceImpl service = new PushCourseMsgServiceImpl();
    private Properties properties;

    //服务状态信息
    private String serviceStautsInfo;
    //服务器状态
    private Boolean serviceStauts = false;
    private List<PushTaskInfo> pushTaskInfoList;

 {
     properties = new Properties();
     try {
         properties.load(PushCourseMsgServiceImpl.class.getClassLoader().getResourceAsStream("global.properties"));
     } catch (IOException e) {
         e.printStackTrace();
     }
 }

    /**
     * 初始化推送，从mysql数据库中获取用户列表，然后把每个用户信息缓存到redis数据库，并且进行排序
     * 最后初始化推送，从redis获取到用时间排好序的用户信息，依次创建定时任务
     */
    private PushCourseMsgServiceImpl() {

    }

    public String getServiceStautsInfo() {
        return serviceStautsInfo;
    }
    public Boolean getServiceStauts() {
        return serviceStauts;
    }

    @Override
    public String getPushMsg(List<Course> pushCourseList, boolean isNowCourse) {
        //遍历课程
        String[] num = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] weekk = {"日", "一", "二", "三", "四", "五", "六"};
        StringBuilder sb = new StringBuilder();
        Calendar c = Calendar.getInstance();
        if(isNowCourse) {
            sb.append("今天：");
        }else {
            sb.append("明天：");
        }
        sb.append(c.get(Calendar.MONTH) + 1).append("月")
                .append(c.get(Calendar.DAY_OF_MONTH)).append("日   星期").append(weekk[c.get(Calendar.DAY_OF_WEEK) - 1])
                .append("\r\n--------------------------------------------\r\n\r\n课程如下：");

        pushCourseList.forEach(course -> {
            sb.append("第").append(num[course.getSort()]).append("大节：").append(course.getTime()).append("\r\n")
                    .append(course.getInfo()).append("\r\n\r\n--------------------------------------------\r\n\r\n\r\n");
        });
        return sb.toString();
    }

    /**
     * 初始化Redis数据库
     */
    private Boolean initRedis() {
        try {
            //查询出所有的推送信息
            PushTaskDao pushTaskDao = new PushTaskDaoImpl();
            List<PushTaskInfo> tempList = new ArrayList<>();
            //过滤不开启推送的任务
            pushTaskDao.findTaskAll().stream().filter(PushTaskInfo::getIsPush).forEach(tempList::add);
            pushTaskInfoList = tempList;
            //user列表转json列表
            List<String> userJsonList = userToJson(pushTaskInfoList);

            //把所有用户信息缓存到redis,redis会自动进行排序
            Jedis jedis = JedisUtil.getJedis();
            for(int i = 0; i < pushTaskInfoList.size(); i++) {
                //添加到数据库，推送信息的更改由操作Redis数据库完成，所以不用设置存活时间

                String s = userJsonList.get(i);
                jedis.zadd(TaskManagerSreivce.PUSH_TESK_INFO, pushTaskInfoList.get(i).getSortKey(), s);

            }
            jedis.close();
        }catch (Exception e) {
            e.printStackTrace();
            serviceStautsInfo = "Redis缓存服务出错，可能是mysql服务出错或者Redis服务出错";
            System.out.println(serviceStautsInfo);
            serviceStauts = false;
            return serviceStauts;
        }
        serviceStautsInfo = "Redis缓存服务启动成功";
        System.out.println(serviceStautsInfo);
        serviceStauts = true;
        return serviceStauts;
    }

    /**
     * 初始化推送任务的线程，一分钟获取一个当前时间下该推送的任务，一天有多少分钟就循环查询多少次
     * 相对于创建Timer定时任务来完成功能，虽然推送任务少的时候消耗资源相对多一些，因为每分钟都要查一次Redis数据库
     * 不过一旦任务量大起来，成百上千抑或是千万亿，这个逻辑就可以节省很多资源，因为它不需要每个任务都创建一个对象
     */
    private void initThread() {
        Thread thread = new Thread(() -> {
            //json与对象互相转换的对象
            ObjectMapper objectMapper = new ObjectMapper();
            Jedis jedis = JedisUtil.getJedis();



            Timer timer = new Timer();
            Calendar c = Calendar.getInstance();

            StringBuilder timeLast = new StringBuilder();
            timeLast.append(c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE) - 1);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Calendar c = Calendar.getInstance();
                    int timeNow = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);
                    if(Integer.valueOf(timeLast.toString()).equals(timeNow)) return;     //当前时间推送过则不进行推送

                    timeLast.delete(0, timeLast.length());
                    timeLast.append(timeNow);



                    //获取所有score相同的任务
                    Set<String> strings = jedis.zrangeByScore(TaskManagerSreivce.PUSH_TESK_INFO, timeNow, timeNow, 0, -1);
                    //推送所有相同时间下的任务
                    strings.forEach(s -> {
                        try {
                            System.out.println(s);
                            //获取到对象信息
                            PushTaskInfo pushTaskInfo = objectMapper.readValue(s, PushTaskInfo.class);

                            //只推送开启了推送的任务
                            if(!pushTaskInfo.getIsPush()) {
                                return;
                            }

                            System.out.println(pushTaskInfo.getClassName() + " : 消息推送");
                            //System.out.println("推送信息：" + pushTaskInfo);
                            //推送QQ消息
                            String str = getPushMsg(pushTaskInfo);
                            System.out.println(str);
                            CQService.CQ.sendGroupMsg(pushTaskInfo.getGroupId(), str);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }, 0, 30 * 1000); //30秒检测一次当前时间

        });
        thread.setDaemon(true);
        thread.start();
    }


    /**
     * 获取推送的文本信息
     * @return 待推送的文本信息
     */
    private String getPushMsg(PushTaskInfo pushTaskInfo) {
        //获取课表对象
        JvtcJiaoWuService jvtcJiaoWuService = new JvtcJiaoWuServiceImpl(pushTaskInfo);
        //待推送课表合集
        List<Course> courseList;
        //是否当天课表  true：当天   false：次日
        if(pushTaskInfo.getIsNowCourse()) {
            courseList = jvtcJiaoWuService.getCourseNow();
        }else {
            courseList = jvtcJiaoWuService.getCourseTomorrow();
        }

        return getPushMsg(courseList, pushTaskInfo.getIsNowCourse());
    }

    /**
     * PushTeskInfo列表转Json列表
     * @param pushTaskInfoList user对象
     * @return 返回
     */
    private List<String> userToJson(List<PushTaskInfo> pushTaskInfoList) {
        //遍历处理所有用户
        List<String> userJsonList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        //把user对象转为json，并且加入json集合
        pushTaskInfoList.stream().map(u -> {
            try {
                //user转json
                return objectMapper.writeValueAsString(u);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).forEach(userJsonList::add);
        return userJsonList;
    }

    @Override
    public Boolean init() {
        //初始化Redis数据
        if(!initRedis())return false;
        System.out.println("初始化Redis成功");

        //初始化任务线程
        initThread();
        serviceStautsInfo += "\r\n推送服务启动成功";
        System.out.println(serviceStautsInfo);
        serviceStauts = true;
        return serviceStauts;
    }


}
