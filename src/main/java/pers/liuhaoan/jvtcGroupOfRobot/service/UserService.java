package pers.liuhaoan.jvtcGroupOfRobot.service;

import pers.liuhaoan.jvtcGroupOfRobot.domain.User;

public interface UserService {

    /**
     * 登入
     * @param user 登入信息
     * @return 成功返回user对象，否则null
     */
    User login(User user);


    /**
     * 注册
     * @param user 用户信息
     * @return 成功true
     */
    boolean register(User user);


    /**
     * 查询账号是否已存在
     * @return true存在
     */
    boolean findUsernameExist(String username);


    /**
     * 判断用户的账号密码合法性，逻辑：
     *  1、      6 <= 长度 <= 15
     *  2、      不能输入特殊字符，只能是 a-z  0-9 和去除空格的可显示字符 十进制asii码4为：33 - 126
     * @param user 需要判断的用户信息
     * @return teue：数据合法        false：数据不合法
     */
    boolean userLlegal(User user);


    /**
     * 修改用户信息（用户提供的新用户信息可能产生安全隐患，所以不取其中的账号）
     * @param user 旧的用户信息
     * @param userNew 新的用户信息
     * @return
     */
    boolean userInfoChange(User user, User userNew);
}
