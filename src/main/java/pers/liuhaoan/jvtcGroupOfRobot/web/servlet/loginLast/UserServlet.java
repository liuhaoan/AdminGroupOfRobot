package pers.liuhaoan.jvtcGroupOfRobot.web.servlet.loginLast;

import org.apache.commons.beanutils.BeanUtils;
import pers.liuhaoan.jvtcGroupOfRobot.domain.ResultInfo;
import pers.liuhaoan.jvtcGroupOfRobot.domain.User;
import pers.liuhaoan.jvtcGroupOfRobot.service.impl.UserServiceImpl;
import pers.liuhaoan.jvtcGroupOfRobot.web.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

@WebServlet(name = "/loginLast/user/*", value = "/loginLast/user/*")
public class UserServlet extends BaseServlet {

    /**
     * 退出登入
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().removeAttribute("user");
        response.sendRedirect("/index.html");
    }

    /**
     *  修改密码
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void passwordChange(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserServiceImpl userService = new UserServiceImpl();

        /*
         * 修改密码
         *
         * 获取的参数：
         *   password：原密码
         *   passwordNew：新密码
         *   email：修改后的邮箱
         *
         * 返回的参数：
         *   state：状态码，非0即真
         *   msg：反馈的消息
         * */

        //把获取的参数封装成user对象
        User userNew = new User();
        try {
            BeanUtils.populate(userNew, request.getParameterMap());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        //获取已登入的账号信息
        User user = (User) request.getSession().getAttribute("user");


        if(userNew == null || userNew.getUsername() == null ||
                userNew.getPassword() == null || userNew.getPasswordNew() == null || userNew.getEmail() == null) {
            writeValue(new ResultInfo(0, "请正确填写信息"), response);
            return;
        }

        //检查修改参数的合法性
        if(userAndcheckCodeCorrect(request, userService, response, userNew)) return;

        //判断原密码是否正确
        if(!userNew.getPassword().equals(user.getPassword())) {
            writeValue(new ResultInfo(0, "原密码输入错误"), response);
            return;
        }

        //原密码不能与新密码相同
        if(userNew.getPassword().equals(userNew.getPasswordNew())) {
            writeValue(new ResultInfo(0, "原密码不能与新密码相同"), response);;
            return;
        }


        //修改数据库中的用户信息
        userService.userInfoChange(user, userNew);
        writeValue(new ResultInfo(1, "修改成功"), response);

        session.removeAttribute("user");
    }
}
