package pers.liuhaoan.jvtcGroupOfRobot.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import pers.liuhaoan.jvtcGroupOfRobot.dao.PushTaskDao;
import pers.liuhaoan.jvtcGroupOfRobot.dao.impl.PushTaskDaoImpl;
import pers.liuhaoan.jvtcGroupOfRobot.dao.impl.UserToPushTaskDaoImpl;
import pers.liuhaoan.jvtcGroupOfRobot.domain.PageBean;
import pers.liuhaoan.jvtcGroupOfRobot.domain.PushTaskInfo;
import pers.liuhaoan.jvtcGroupOfRobot.domain.User;
import pers.liuhaoan.jvtcGroupOfRobot.service.TaskManagerSreivce;
import pers.liuhaoan.jvtcGroupOfRobot.utils.JedisUtil;
import redis.clients.jedis.Jedis;

import java.util.List;

public class TaskManagerSreivceImpl implements TaskManagerSreivce {
    private PushTaskDao pushTaskDao = new PushTaskDaoImpl();
    private UserToPushTaskDaoImpl userToPushTeskDao= new UserToPushTaskDaoImpl();
    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public boolean addPushTesk(User user, PushTaskInfo pushTaskInfo) {
        //添加进Redis数据库中，如果没开导致出错则不计入操作成败中
        addTeskToRedis(pushTaskInfo);
        return addTeskToMysql(user, pushTaskInfo);
    }

    @Override
    public List<PushTaskInfo> findPushTask(User user) {
        return userToPushTeskDao.findUserAllTask(user);
    }

    @Override
    public PageBean<PushTaskInfo> findPushTask(User user, PageBean<PushTaskInfo> pageBean) {
        //查询记录总数
        int totalCount = userToPushTeskDao.findUserAllTaskTotalCount(user);

        //总页数
        int totalPage = (totalCount / pageBean.getPageSize());
        if(totalCount % pageBean.getPageSize() != 0) totalPage++;

        //当前页码
        int currentPage = pageBean.getCurrentPage();

        //当前页码大于总页数则按总页数计算， 小于1则按1计算
        if(currentPage > totalPage) {
            currentPage = totalPage;
        }else if(currentPage < 1) {
            currentPage = 1;
        }

        //根据记录总数与分页信息计算出偏移
        int index = (currentPage * pageBean.getPageSize()) - pageBean.getPageSize();

        //根据偏移查询此用户下相应偏移量的数据
        List<PushTaskInfo> userTaskLimitList = userToPushTeskDao.findUserTaskLimit(user, index, pageBean.getPageSize());

        //完善分页信息
        pageBean.setTotalCount(totalCount);
        pageBean.setTotalPage(totalPage);
        pageBean.setList(userTaskLimitList);
        return pageBean;
    }

    @Override
    public boolean findGroupIdExist(PushTaskInfo pushTaskInfo) {
        return pushTaskDao.findPushTaskExist(pushTaskInfo);
    }

    @Override
    public boolean findGroupIdToTesk(String groupId) {
        //redis中存在就不读取数据库
        String key = "groupIdList";
        Jedis jedis = JedisUtil.getJedis();
        if(jedis.sismember(key, groupId)) {
            return true;
        }

        //读取数据库
        List<String> groupIdAll = pushTaskDao.findGroupIdAll();

        //存进redis缓存
        groupIdAll.forEach(s -> jedis.sadd(key, s));
        jedis.close();
        return groupIdAll.contains(groupId);//判断是否存在
    }

    @Override
    public boolean reTesk(PushTaskInfo pushTaskInfo, PushTaskInfo pushTaskInfoChange) {
        if(pushTaskInfoChange.getId() == 0) {
            return false;
        }if(pushTaskInfoChange.getGroupId() == null || pushTaskInfoChange.getGroupId().equals("")) {
            return false;
        }if(pushTaskInfoChange.getJiaoWuUsername() == null || pushTaskInfoChange.getJiaoWuUsername().equals("")) {
            return false;
        }if(pushTaskInfoChange.getJiaoWuPassword() == null || pushTaskInfoChange.getJiaoWuPassword().equals("")) {
            return false;
        }if(pushTaskInfoChange.getIsNowCourse() == null) {
            return false;
        }if(pushTaskInfoChange.getClassName() == null || pushTaskInfoChange.getClassName().equals("")) {
            return false;
        }if(pushTaskInfoChange.getIsPush() == null ) {
            return false;
        }if(pushTaskInfoChange.getAccountId() == null || pushTaskInfoChange.getAccountId().equals("")) {
            return false;
        }if(pushTaskInfoChange.getPush_hour_min() == null || pushTaskInfoChange.getPush_hour_min().equals("")) {
            return false;
        }

        //把前端编号替换成需要修改的任务在mysql中的id
        pushTaskInfoChange.setId(pushTaskInfo.getId());

        //删除Redis中修改前的数据
        try {
            Jedis jedis = JedisUtil.getJedis();
            if(jedis != null) {
                jedis.zrem(PUSH_TESK_INFO, objectMapper.writeValueAsString(pushTaskInfo));


                //判断是否开启推送，开启推送则加入redis缓存
                if(pushTaskInfoChange.getIsPush()) {
                    //在Redis中添加新的任务
                    String s = objectMapper.writeValueAsString(pushTaskInfoChange);
                    jedis.zadd(PUSH_TESK_INFO, pushTaskInfoChange.getSortKey(), s);
                }

                jedis.close();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //修改mysql数据库中的任务
        //pushTaskDao.reTask(pushTaskInfoChange);
        return  pushTaskDao.reTask(pushTaskInfoChange);
    }

    @Override
    public boolean delTesk(PushTaskInfo pushTaskInfo) {
        //删除Redis中的任务
        try {
            Jedis jedis = JedisUtil.getJedis();
            if(jedis != null) {
                jedis.zrem(PUSH_TESK_INFO, objectMapper.writeValueAsString(pushTaskInfo));
                jedis.close();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //删除mysql中的任务
        return pushTaskDao.delTask(pushTaskInfo.getGroupId());
    }

    @Override
    @Transactional//事务管理
    public Boolean addTeskToMysql(User user, PushTaskInfo pushTaskInfo) {
        //添加进mysql数据库中
        //添加任务并记录自增id
        int pushTeskId = pushTaskDao.addTask(pushTaskInfo);
        if(pushTeskId == -1) {
            return false;
        }
        //添加用户与任务的关联，添加关联失败则删除之前创建好的推送信息
        return userToPushTeskDao.addUserToPushTesk(pushTeskId, user.getUsername()) || !pushTaskDao.delTask(pushTeskId);
    }

    @Override
    public boolean addTeskToRedis(PushTaskInfo pushTaskInfo) {
        //添加进Redis中
        try {
            //把任务存进redis中
            String s = objectMapper.writeValueAsString(pushTaskInfo);
            Jedis jedis = JedisUtil.getJedis();
            if(jedis != null) {
                jedis.zadd(PUSH_TESK_INFO, pushTaskInfo.getSortKey(), s);
                jedis.close();
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
