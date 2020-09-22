package pers.liuhaoan.jvtcGroupOfRobot.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import pers.liuhaoan.jvtcGroupOfRobot.domain.PushTaskInfo;
import pers.liuhaoan.jvtcGroupOfRobot.domain.User;
import pers.liuhaoan.jvtcGroupOfRobot.service.impl.TaskManagerSreivceImpl;
import pers.liuhaoan.jvtcGroupOfRobot.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "/queryUserInfo", value = "/queryUserInfo")
public class QueryUserInfoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
        * 查询用户信息，用来给前端判断是否已登入
        *
        * 返回json格式：
        *   state：状态码（非0即真）
        *   user：用户名
        *   email：用户邮箱
        * */

        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        User user = (User) request.getSession().getAttribute("user");

        //没有用户信息（没有登入）
        if(user == null) {
            writer.println("{\"state\": 0}");
        }else {

            //为了消除安全隐患，不直接把user对象序列化传出去，否则会暴露密码
            String sb = "{\"state\": 1, \"user\": \"" + user.getUsername() +
                    "\", \"email\": \"" + user.getEmail() + "\"}";
            writer.println(sb);
        }



    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
