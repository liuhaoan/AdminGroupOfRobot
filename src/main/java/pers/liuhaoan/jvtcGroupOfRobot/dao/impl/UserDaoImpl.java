package pers.liuhaoan.jvtcGroupOfRobot.dao.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import pers.liuhaoan.jvtcGroupOfRobot.dao.UserDao;
import pers.liuhaoan.jvtcGroupOfRobot.domain.User;
import pers.liuhaoan.jvtcGroupOfRobot.utils.JdbcUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UserDaoImpl implements UserDao {
    NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(JdbcUtil.getDataSource());
    JdbcTemplate jdbcTemplate = jdbc.getJdbcTemplate();

    @Override
    public User findUsernameAndPassword(String username, String password) {
        String sql = "select * from user where username = ? and password = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), username, password);
        }catch (Exception e) {
            return null;
        }
    }

    @Override
    public User findUsername(String username) {
        String sql = "select * from user where username = ?";
        //表中username字段是非空唯一的，所以一定只能查得到一条记录
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), username);
        }catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<User> findUserAll() {
        String sql = "Select username, password From user;";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public int addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            //用户名和密码不能为null
            System.out.println(user.getEmail());
            if(user.getPassword() == null || user.getUsername() == null) return -1;
            String sql = "insert into user(username, password, email) values(:username, :password, :email)";
            jdbc.update(sql, new BeanPropertySqlParameterSource(user), keyHolder);
        }catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return (int) Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public boolean userInfoChange(User user) {
        String sql = "update user set password=?, email=? where username=?";
        if(user == null || user.getUsername() == null || user.getPasswordNew() == null || user.getEmail() == null) {
            return false;
        }
        try {
            return jdbcTemplate.update(sql, user.getPasswordNew(), user.getEmail(), user.getUsername()) > 0;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
