package pers.liuhaoan.jvtcGroupOfRobot.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pers.liuhaoan.jvtcGroupOfRobot.dao.impl.PushTaskDaoImpl;
import pers.liuhaoan.jvtcGroupOfRobot.domain.CQServiceInteractionDoMain;
import pers.liuhaoan.jvtcGroupOfRobot.domain.Course;
import pers.liuhaoan.jvtcGroupOfRobot.domain.PushTaskInfo;
import pers.liuhaoan.jvtcGroupOfRobot.enums.CQKeyEnum;
import pers.liuhaoan.jvtcGroupOfRobot.enums.CQTypeEnum;
import pers.liuhaoan.jvtcGroupOfRobot.service.CQService;
import pers.liuhaoan.jvtcGroupOfRobot.service.PushCourseMsgService;
import pers.liuhaoan.jvtcGroupOfRobot.utils.JedisUtil;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class CQServiceImpl implements CQService {

    private OutputStreamWriter outputStreamWriter;
    private BufferedReader bufferedReader;
    private Socket socket;
    private Boolean serviceStatus = false;
    private String serviceStatusInfo;

    public static final CQServiceImpl CQ = new CQServiceImpl();


    private CQServiceImpl() {

    }

    @Override
    public synchronized void sendFriendMsg(String friendId, String msg) {
        CQServiceInteractionDoMain jsonMsg = new CQServiceInteractionDoMain();
        jsonMsg.setKey(CQKeyEnum.jvtcGroupOfRobot);
        jsonMsg.setType(CQTypeEnum.sendFriendMsg);
        jsonMsg.setFriendId(friendId);
        jsonMsg.setMsg(msg);

        try {
            outputStreamWriter.write(Objects.requireNonNull(objectToJson(jsonMsg)));
            outputStreamWriter.flush();
            Thread.sleep(300);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void sendGroupMsg(String groupId, String msg) {
        CQServiceInteractionDoMain jsonMsg = new CQServiceInteractionDoMain();
        jsonMsg.setKey(CQKeyEnum.jvtcGroupOfRobot);
        jsonMsg.setType(CQTypeEnum.sendGroupMsg);
        jsonMsg.setGroupId(groupId);
        jsonMsg.setMsg(msg);

        String str = Objects.requireNonNull(objectToJson(jsonMsg));
        System.out.println(str);
        try {
            outputStreamWriter.write(str);
            outputStreamWriter.flush();
            Thread.sleep(300);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addFriend(String fromAccount, String flag) {
        System.out.println("收到好友申请：" + fromAccount + ":" + flag);
    }

    @Override
    public void addGroup(String fromAccount, String fromGroup, String flag) {
        System.out.println("收到加群消息：" + fromAccount + ":" + flag);
    }

    @Override
    public void friendMsg(String fromAccount, String msg) {
        System.out.println("收到私聊消息：" + fromAccount + ":" + msg);
    }

    @Override
    public void groupMsg(String fromGroup, String msg) {
        System.out.println("收到群消息：" + fromGroup + ":" + msg);
        Jedis jedis = JedisUtil.getJedis();
        switch (msg) {
            case "明天课表": {
                //根据群号获取数据库中的推送任务信息
                PushTaskInfo pushTaskInfo = new PushTaskDaoImpl().findTask(fromGroup);
                //查询出这个班级的推送任务
                List<Course> courseTomorrow = new JvtcJiaoWuServiceImpl(pushTaskInfo).getCourseTomorrow();
                String pushMsg = PushCourseMsgService.service.getPushMsg(courseTomorrow, false);
                //推送消息
                CQ.sendGroupMsg(fromGroup, pushMsg);
                break;
            }
            case "今天课表": {
                //根据群号获取数据库中的推送任务信息
                System.out.println(fromGroup);
                PushTaskInfo pushTaskInfo = new PushTaskDaoImpl().findTask(fromGroup);
                //查询出这个班级的推送任务
                List<Course> courseTomorrow = new JvtcJiaoWuServiceImpl(pushTaskInfo).getCourseNow();
                String pushMsg = PushCourseMsgService.service.getPushMsg(courseTomorrow, true);
                //推送消息
                CQ.sendGroupMsg(fromGroup, pushMsg);
                break;
            }
        }
    }

    @Override
     public String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object jsonToObject(String json, Class type) {
        try {
            return new ObjectMapper().readValue(json, type);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void destTroyed() {
        try {
            if(bufferedReader != null) {
                bufferedReader.close();
            }if(outputStreamWriter != null) {
                outputStreamWriter.close();
            }if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean init() {
        try {
            //获取配置文件中酷Q服务的host和port
            Properties properties = new Properties();
            properties.load(CQServiceImpl.class.getClassLoader().getResourceAsStream("global.properties"));
            String host = properties.getProperty("cqServiceHost");
            int port = Integer.parseInt(properties.getProperty("cqServicePort"));

            //连接酷Q机器人框架，并获取到与它交互的输入输出流
            socket = new Socket(host, port);
            //酷Q用的时gbk编码
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), "gbk");
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "gbk"));
        } catch (IOException e) {
            e.printStackTrace();
            serviceStatusInfo = "酷Q机器人服务端连接失败，初始化失败";
            System.out.println(serviceStatusInfo);
            serviceStatus = false;
            return serviceStatus;
        }

        //创建监听消息的线程
        Thread startInteraction =  new Thread(() -> {
            while (true) {
                try {
                    String jsonInfo = "";
                    try {
                        jsonInfo = bufferedReader.readLine();
                        if("listen".equals(jsonInfo)) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY年MM月dd日 HH:mm:ss");
                            System.out.println(simpleDateFormat.format(new Date()) + jsonInfo);
                            continue;
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                        //io流已关闭，说明web程序已销毁
                        serviceStatus = false;
                        System.out.println("与酷Q的连接已断开");
                        return;
                    }
                    CQServiceInteractionDoMain cqInfo = new ObjectMapper().readValue(jsonInfo, CQServiceInteractionDoMain.class);
                    //没添加任务的群不响应消息
                    if(!new TaskManagerSreivceImpl().findGroupIdToTesk(Optional.ofNullable(cqInfo.getFromGroup()).orElse("0"))) {
                        continue;
                    }
                    Thread t;
                    switch (cqInfo.getType()) {
                        case addGroup : t = new Thread(() -> addGroup(cqInfo.getFromAccount(), cqInfo.getFromGroup(), cqInfo.getFlag()));t.setDaemon(true);t.start(); break;
                        case addFriend : t = new Thread(() -> addFriend(cqInfo.getFromAccount(), cqInfo.getFlag()));t.setDaemon(true);t.start(); break;
                        case friendMsg : t = new Thread(() -> friendMsg(cqInfo.getFromAccount(), cqInfo.getMsg()));t.setDaemon(true);t.start();break;
                        case groupMsg : t = new Thread(() -> groupMsg(cqInfo.getFromGroup(), cqInfo.getMsg()));t.setDaemon(true);t.start();break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        startInteraction.setDaemon(true);
        startInteraction.start();
        serviceStatus = true;
        System.out.println("酷Q机器人服务端启动成功");
        serviceStatusInfo += "酷Q机器人服务端启动成功";
        return serviceStatus;
    }

    @Override
    public Boolean getServiceStatus() {
        return serviceStatus;
    }


}
