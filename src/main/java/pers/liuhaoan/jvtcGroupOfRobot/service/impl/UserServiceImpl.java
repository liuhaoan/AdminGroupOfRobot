package pers.liuhaoan.jvtcGroupOfRobot.service.impl;

import pers.liuhaoan.jvtcGroupOfRobot.dao.UserDao;
import pers.liuhaoan.jvtcGroupOfRobot.dao.impl.UserDaoImpl;
import pers.liuhaoan.jvtcGroupOfRobot.domain.User;
import pers.liuhaoan.jvtcGroupOfRobot.service.UserService;

import java.util.regex.Pattern;

public class UserServiceImpl implements UserService {
    private UserDao userDao = new UserDaoImpl();
    @Override
    public User login(User user) {
        return userDao.findUsernameAndPassword(user.getUsername(), user.getPassword());
    }

    @Override
    public boolean register(User user) {
        return userDao.addUser(user) > 0;
    }

    @Override
    public boolean findUsernameExist(String username) {
        return userDao.findUsername(username) != null;
    }

    /**
     * 判断用户的账号密码合法性，逻辑：
     *  1、      6 <= 长度 <= 15
     *  2、      不能输入特殊字符，只能是 a-z  0-9 和去除空格的可显示字符 十进制asii码4为：33 - 126
     * @param user 需要判断的用户信息
     * @return teue：数据合法        false：数据不合法
     */
    public boolean userLlegal(User user) {
        //是否为null
//        if(user == null || user.getUsername() == null || user.getPassword() == null) return false;

        /*System.out.println(Pattern.matches("^(?=.*[0-9].*)(?=.*[a-z].*).{6,15}$", user.getUsername()));
        System.out.println(Pattern.matches("^(?=.*[0-9].*)(?=.*[a-z].*).{6,15}$", user.getPassword()));
        System.out.println(Pattern.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", user.getEmail()));*/

        /*System.out.println(user != null);
        System.out.println(user.getUsername() != null);
        System.out.println(user != null && user.getUsername() != null);
        System.out.println(user != null && user.getUsername() != null && user.getPassword() != null &&
                Pattern.matches("^(?=.*[0-9].*)(?=.*[a-z].*).{6,15}$", user.getUsername()) &&
                Pattern.matches("^(?=.*[0-9].*)(?=.*[a-z].*).{6,15}$", user.getPassword()) &&
                (user.getEmail() == null || Pattern.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", user.getEmail()))  );*/


        return user != null && user.getUsername() != null && user.getPassword() != null &&
                Pattern.matches("^(?=.*[0-9].*)(?=.*[a-z].*).{6,15}$", user.getUsername()) &&
                Pattern.matches("^(?=.*[0-9].*)(?=.*[a-z].*).{6,15}$", user.getPassword()) &&
                (user.getEmail() == null || Pattern.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", user.getEmail())) &&
                (user.getPasswordNew() == null || Pattern.matches("^(?=.*[0-9].*)(?=.*[a-z].*).{6,15}$", user.getPasswordNew()));


        /*//判断账号长度
        if(!(6 <= user.getUsername().length() && user.getUsername().length() <=15)) return false;
        //判断密码长度
        if(!(6 <= user.getPassword().length() && user.getPassword().length() <=15)) return false;
        //判断字符是否合法
        for (char c : user.getUsername().toCharArray()) {
            if ((int) c < 33 || (int) c > 126)return false;
        }
        for (char c : user.getPassword().toCharArray()) {
            if ((int) c < 33 || (int) c > 126)return false;
        }

        //有邮箱
        if(user.getEmail() != null) {
            if(!(6 <= user.getEmail().length() && user.getEmail().length() <=15)) return false;
            for (char c : user.getEmail().toCharArray()) {
                if ((int) c < 33 || (int) c > 126)return false;
            }
        }
        return true;*/
    }

    @Override
    public boolean userInfoChange(User user, User userNew) {
        //避免安全隐患，把user的username信息给userNew信息的username赋值
        userNew.setUsername(user.getUsername());
        return userDao.userInfoChange(userNew);
    }
}
