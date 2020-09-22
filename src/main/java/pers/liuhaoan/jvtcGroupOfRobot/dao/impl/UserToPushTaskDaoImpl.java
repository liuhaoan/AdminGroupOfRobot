package pers.liuhaoan.jvtcGroupOfRobot.dao.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import pers.liuhaoan.jvtcGroupOfRobot.dao.UserToPushTaskDao;
import pers.liuhaoan.jvtcGroupOfRobot.domain.PushTaskInfo;
import pers.liuhaoan.jvtcGroupOfRobot.domain.User;
import pers.liuhaoan.jvtcGroupOfRobot.utils.JdbcUtil;

import java.util.List;

public class UserToPushTaskDaoImpl implements UserToPushTaskDao {
    private NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(JdbcUtil.getDataSource());
    JdbcTemplate jdbcTemplate = jdbc.getJdbcTemplate();

    public UserToPushTaskDaoImpl() {

    }

    @Override
    public List<PushTaskInfo> findUserAllTask(User user) {
        String sql = "SELECT p.`id`, p.`jiaoWuUsername`, p.`jiaoWuPassword`, p.`groupId`, p.`accountId`, p.`push_hour_min`, p.`className`, p.`isPush`, p.`isNowCourse` FROM userToPushTesk u, pushTesk p WHERE u.`username` = ? AND u.`pushTeskId` = p.`id`";
        List<PushTaskInfo> result = null;
        try {
            result = jdbcTemplate.query(sql, new Object[] {user.getUsername()}, new BeanPropertyRowMapper<>(PushTaskInfo.class));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result == null ? null : result.isEmpty() ? null : result;
    }

    @Override
    public List<PushTaskInfo> findUserTaskLimit(User user, int index, int count) {
        String sql = "SELECT p.`id`, p.`jiaoWuUsername`, p.`jiaoWuPassword`, p.`groupId`, p.`accountId`, p.`push_hour_min`, p.`className`, p.`isPush`, p.`isNowCourse` FROM userToPushTesk u, pushTesk p WHERE u.`username` = ? AND u.`pushTeskId` = p.`id` limit ?, ?";
        List<PushTaskInfo> result = null;
        try {
            result = jdbcTemplate.query(sql, new Object[] {user.getUsername(), index, count},
                    new BeanPropertyRowMapper<>(PushTaskInfo.class));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result == null ? null : result.isEmpty() ? null : result;
    }

    @Override
    public int findUserAllTaskTotalCount(User user) {
        String sql = "select count(id) from userToPushTesk where username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, user.getUsername());
        }catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean addUserToPushTesk(int id, String username) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            //添加用户信息和任务信息表（userToPushTesk）
            String sql = "insert into userToPushTesk(username, pushTeskId) values(?, ?)";
            return jdbcTemplate.update(sql, username, id) > 0;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
