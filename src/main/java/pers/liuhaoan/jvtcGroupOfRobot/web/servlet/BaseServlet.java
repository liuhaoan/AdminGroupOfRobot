package pers.liuhaoan.jvtcGroupOfRobot.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;
import pers.liuhaoan.jvtcGroupOfRobot.domain.ResultInfo;
import pers.liuhaoan.jvtcGroupOfRobot.domain.User;
import pers.liuhaoan.jvtcGroupOfRobot.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI =req.getRequestURI();
        String methodName = requestURI.substring(requestURI.lastIndexOf("/") + 1);

        //通过反射调用子类的方法
        try {
            Method method = this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            method.invoke(this, req, resp);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把对象转成json传给客户端
     * @param obj 对象
     * @param response resp
     * @throws IOException 错误
     */
    public void writeValue(Object obj,HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        objectMapper.writeValue(response.getOutputStream(), obj);
    }

    /**
     *  用户是否合法、验证码是否正确
     * @param userService service对象
     * @param user user信息
     * @return 成功失败
     */
    public boolean userAndcheckCodeCorrect(HttpServletRequest request, UserServiceImpl userService, HttpServletResponse response, User user) throws IOException {
        if(!userService.userLlegal(user)) {
            writeValue(new ResultInfo(0, "用户信息不合法，账号和密码必须为：6到15位的小写字母加数字组合"), response);
            return true;
        }

        //判断验证码是否正确
        String checkCode = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
        if(checkCode == null) {
            writeValue(new ResultInfo(0, "请刷新验证码"), response);
            return true;
        }


        request.getSession().removeAttribute("CHECKCODE_SERVER");
        if(!checkCode.equalsIgnoreCase(user.getCheckCode())) {
            writeValue(new ResultInfo(0, "验证码错误"), response);
            return true;
        }


        //判断账号和密码是否一致
        if(user.getUsername().equals(user.getPassword())) {
            writeValue(new ResultInfo(0, "账号和密码不能一致"), response);
            return true;
        }

        //判断是否已登入
        if(request.getSession().getAttribute("user") != null) {
            writeValue(new ResultInfo(0, "您已登入，请勿重复登入"), response);
            return true;
        }


        return false;
    }
}
