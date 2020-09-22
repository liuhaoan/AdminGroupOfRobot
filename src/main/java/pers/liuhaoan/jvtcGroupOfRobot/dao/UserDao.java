package pers.liuhaoan.jvtcGroupOfRobot.dao;

import pers.liuhaoan.jvtcGroupOfRobot.domain.User;

import java.util.List;

public interface UserDao {

    /**
     * 查询用户和密码
     * @param username 用户
     * @param password 密码
     * @return 返回用户信息
     */
    User findUsernameAndPassword(String username, String password);

    /**
     * 查询用户信息，成功返回用户具体信息，否则返回null
     * @param username 需要查询的用户    username
     * @return 用户具体信息
     */
    User findUsername(String username);

    /**
     * 查询所有的用户信息
     */
    List<User> findUserAll();

    /**
     * 新增用户
     * @param user 用户信息
     * @return 返回自增id，-1为创建失败
     */
    int addUser(User user);


    /**
     * 修改mysql中的用户信息
     * @param user 用户信息
     * @return 成功失败
     */
    boolean userInfoChange(User user);
}
