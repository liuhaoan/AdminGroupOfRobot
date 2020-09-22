package pers.liuhaoan.jvtcGroupOfRobot.web.listener;


import pers.liuhaoan.jvtcGroupOfRobot.service.CQService;
import pers.liuhaoan.jvtcGroupOfRobot.service.PushCourseMsgService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.*;
import java.net.Socket;

@WebListener
public class StartServiceListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //web项目启动，自动启动与酷Q机器人的交互
        System.out.println("context创建。。");
        if(!CQService.CQ.init()) return;

        //初始化推送
        PushCourseMsgService.service.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //web项目销毁，同时也销毁与酷Q机器人的交互
        System.out.println("context销毁。。");
        //CQService.CQ.destTroyed();

    }
}
