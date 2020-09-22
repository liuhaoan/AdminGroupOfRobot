package pers.liuhaoan.jvtcGroupOfRobot.dao.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import pers.liuhaoan.jvtcGroupOfRobot.dao.PushTaskDao;
import pers.liuhaoan.jvtcGroupOfRobot.domain.PushTaskInfo;
import pers.liuhaoan.jvtcGroupOfRobot.utils.JdbcUtil;

import java.util.List;
import java.util.Objects;

public class PushTaskDaoImpl implements PushTaskDao {
    private NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(JdbcUtil.getDataSource());
    JdbcTemplate jdbcTemplate = jdbc.getJdbcTemplate();

    public PushTaskDaoImpl() {

    }

    @Override
    public PushTaskInfo findTask(String groupId) {
        String sql = "select * from pushTesk where groupId = ?";

        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<PushTaskInfo>(PushTaskInfo.class), groupId);
    }

    @Override
    public List<String> findGroupIdAll() {
        String sql = "select groupId from pushTesk";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public List<PushTaskInfo> findTaskAll() {
        String sql = "select * from pushTesk";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(PushTaskInfo.class));
    }

    @Override
    public int addTask(PushTaskInfo pushTaskInfo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            //添加任务信息到任务信息表，记录id号（pushTesk）
            String sql = "insert into pushTesk(jiaoWuUsername, jiaoWuPassword, groupId, accountId, push_hour_min, className, isPush, isNowCourse) " +
                    "values(:jiaoWuUsername, :jiaoWuPassword, :groupId, :accountId, :push_hour_min, :className, :isPush, :isNowCourse)";
            jdbc.update(sql, new BeanPropertySqlParameterSource(pushTaskInfo), keyHolder);
        }catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return (int) Objects.requireNonNull(keyHolder.getKey()).longValue();
    }


    @Override
    public boolean delTask(int id) {

        try {
            String sql = "delete from pushTesk where id = ?";
            return jdbcTemplate.update(sql, id) > 0;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delTask(String groupId) {

        try {
            String sql = "delete from pushTesk where groupId = ?";
            return jdbcTemplate.update(sql, groupId) > 0;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean reTask(PushTaskInfo pushTaskInfochange) {
        String sql = "update pushTesk set jiaoWuUsername = :jiaoWuUsername, " +
                "jiaoWuPassword = :jiaoWuPassword, groupId = :groupId, accountId = :accountId, " +
                "push_hour_min = :push_hour_min, className = :className, isPush = :isPush, " +
                "isNowCourse = :isNowCourse where id = :id";

        try {
            return jdbc.update(sql, new BeanPropertySqlParameterSource(pushTaskInfochange)) > 0;
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean findPushTaskExist(PushTaskInfo pushTaskInfo) {
        String sql = "select * from pushTesk where groupId = ?";
        try {
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<PushTaskInfo>(PushTaskInfo.class), pushTaskInfo.getGroupId()).size() > 0;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
