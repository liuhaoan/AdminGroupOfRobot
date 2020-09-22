package pers.liuhaoan.jvtcGroupOfRobot.web.servlet.loginBefore;

import org.apache.commons.beanutils.BeanUtils;
import pers.liuhaoan.jvtcGroupOfRobot.domain.ResultInfo;
import pers.liuhaoan.jvtcGroupOfRobot.domain.User;
import pers.liuhaoan.jvtcGroupOfRobot.service.impl.UserServiceImpl;
import pers.liuhaoan.jvtcGroupOfRobot.web.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@WebServlet(name = "/loginBefore/user/*", value = "/loginBefore/user/*")
public class UserServlet extends BaseServlet {
    private UserServiceImpl userService = new UserServiceImpl();

    /**
     * 登入
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*
         * 用户登入，登入成功后把user信息存到session中
         * 参数：
         *   username:账号
         *   password:密码
         *   checkCode:验证码
         * */
        //取出请求消息中的参数，并且封装成User
        User user = new User();
        try {
            BeanUtils.populate(user, request.getParameterMap());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }


        //判断用户信息合法性、验证码是否正确
        if (userAndcheckCodeCorrect(request, userService, response, user)) return;



        //数据合法，查询数据库账号密码是否正确
        User loginUser = userService.login(user);
        if(loginUser == null) {
            writeValue(new ResultInfo(0, "账号或密码错误"), response);
            return;
        }

        //登入成功，把用户信息加入session
        request.getSession().setAttribute("user", loginUser);
        writeValue(new ResultInfo(1, "登入成功"), response);
    }

    /**
     * 注册
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //封装成user对象
        User user = new User();
        try {
            BeanUtils.populate(user, request.getParameterMap());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        //判断验证码是否正确、用户是否合法
        if (userAndcheckCodeCorrect(request, userService, response, user)) return;


        //判断是否已经有账号注册
        if(userService.findUsernameExist(user.getUsername())) {
            writeValue(new ResultInfo(0, "账号已存在"), response);
            return;
        }

        //添加账号到数据库
        if(!userService.register(user)) {
            writeValue(new ResultInfo(0, "注册失败，请过段时间再试"), response);
        }else {
            writeValue(new ResultInfo(1, "注册成功"), response);
        }
    }



}
