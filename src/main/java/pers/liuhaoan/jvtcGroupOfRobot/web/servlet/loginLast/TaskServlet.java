package pers.liuhaoan.jvtcGroupOfRobot.web.servlet.loginLast;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;
import pers.liuhaoan.jvtcGroupOfRobot.domain.PageBean;
import pers.liuhaoan.jvtcGroupOfRobot.domain.PushTaskInfo;
import pers.liuhaoan.jvtcGroupOfRobot.domain.ResultInfo;
import pers.liuhaoan.jvtcGroupOfRobot.domain.User;
import pers.liuhaoan.jvtcGroupOfRobot.service.impl.TaskManagerSreivceImpl;
import pers.liuhaoan.jvtcGroupOfRobot.web.servlet.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@WebServlet(name = "/loginLast/task/*", value = "/loginLast/task/*")
public class TaskServlet extends BaseServlet {
    private TaskManagerSreivceImpl teskManagerSreivce = new TaskManagerSreivceImpl();

    /**
     * 添加推送任务
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取前端传过来的信息封装成对象
        PushTaskInfo pushTaskInfoNew = getPushTaskInfo(request);
        if (pushTaskInfoNew == null) {
            writeValue(new ResultInfo(0, "获取任务信息失败，请刷新网页"), response);
            return;
        }

        //检查任务信息的完整性
        if (checkTaskInfo(response, pushTaskInfoNew)) return;

        //查询新建任务群号是否已存在
        if(teskManagerSreivce.findGroupIdExist(pushTaskInfoNew)) {
            writeValue(new ResultInfo(0, "此群号已存在，请换一个试试把~~~"), response);
            return;
        }


        //添加任务
        if(teskManagerSreivce.addPushTesk((User)request.getSession().getAttribute("user"), pushTaskInfoNew)) {
            writeValue(new ResultInfo(1, "添加成功"), response);
        }else {
            writeValue(new ResultInfo(0, "添加失败"), response);
        }
    }

    /**
     * 修改推送任务
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void change(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //获取前端传过来的信息
        PushTaskInfo pushTaskInfoChange = getPushTaskInfo(request);
        //获取session中的任务列表
        List<PushTaskInfo> teskList = getPushTaskInfos(request);
        if(teskList == null || pushTaskInfoChange == null) {
            writeValue(new ResultInfo(0, "获取任务信息失败，请刷新网页"), response);
            return;
        }


        //检查任务信息的完整性
        if (checkTaskInfo(response, pushTaskInfoChange)) return;

        //根据编号在user对象中获取相应的任务信息（修改前任务信息）
        PushTaskInfo pushTaskInfo = teskList.get(pushTaskInfoChange.getId() - 1);

        //调用service对象修改
        if(teskManagerSreivce.reTesk(pushTaskInfo, pushTaskInfoChange)) {
            writeValue(new ResultInfo(1, "修改完成"), response);
        }else {
            writeValue(new ResultInfo(0, "修改失败"), response);
        }
    }

    /**
     * 删除推送任务
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void del(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //获取前端传过来的信息
        PushTaskInfo pushTaskInfoTemp = getPushTaskInfo(request);
        //获取session中的任务列表
        List<PushTaskInfo> teskList = getPushTaskInfos(request);
        if(teskList == null || pushTaskInfoTemp == null) {
            writeValue(new ResultInfo(0, "获取任务信息失败，请刷新网页"), response);
            return;
        }

        //判断是否包含需要删除的id
        if(pushTaskInfoTemp.getId() == 0) {
            writeValue(new ResultInfo(0, "请选择需要删除的选项"), response);
            return;
        }

        //通过id - 1 获取user对象中的推送任务信息，用这个原始任务信息删除
        PushTaskInfo pushTaskInfo = teskList.get(pushTaskInfoTemp.getId() - 1);

        //调用service删除任务
        if(teskManagerSreivce.delTesk(pushTaskInfo)) {
            writeValue(new ResultInfo(1, "删除成功"), response);
        }else {
            writeValue(new ResultInfo(0, "删除失败"), response);
        }
    }

    /**
     * 查询分页任务信息
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void queryPageTask(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取任务列表
        String pushTeskListJson = null;
        ObjectMapper objectMapper = new ObjectMapper();
        PageBean<PushTaskInfo> pushTaskInfoPageBeanTemp = new PageBean<>();
        User user = (User) request.getSession().getAttribute("user");

        //把参数封装成PageBean对象
        try {
            BeanUtils.populate(pushTaskInfoPageBeanTemp, request.getParameterMap());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            writeValue(new ResultInfo(0, "您的分页信息有误，请刷新网页"), response);
            return;
        }

        //查询数据库中当前用户的分页任务信息
        PageBean<PushTaskInfo> pushTaskInfoPageBean = teskManagerSreivce.findPushTask(user, pushTaskInfoPageBeanTemp);
        if(pushTaskInfoPageBean == null || pushTaskInfoPageBean.getList() == null) {
            writeValue(new ResultInfo(0, "获取分页信息失败，请刷新网页"), response);
            return;
        }

        //把分页信息保存到session
        request.getSession().setAttribute("PAGE_BEAN_TASK_LIST", pushTaskInfoPageBean);

        //把分页信息传给用户
        writeValue(pushTaskInfoPageBean, response);

    }




    //-------------------------私有方法------------------------//




    /**
     * 获取已保存在session中的任务列表集合
     * @param request
     * @return
     */
    private List<PushTaskInfo> getPushTaskInfos(HttpServletRequest request) {
        HttpSession session = request.getSession();
        PageBean<PushTaskInfo> page_bean_task_list = (PageBean<PushTaskInfo>) session.getAttribute("PAGE_BEAN_TASK_LIST");
        return page_bean_task_list.getList();
    }

    /**
     * 检查任务信息是否合法
     * @param response
     * @param pushTaskInfoChange
     * @return
     * @throws IOException
     */
    private boolean checkTaskInfo(HttpServletResponse response, PushTaskInfo pushTaskInfoChange) throws IOException {
        if(pushTaskInfoChange.getId() == 0) {
            writeValue(new ResultInfo(0, "请选择一个任务操作"), response);
            return true;
        }
        if(pushTaskInfoChange.getGroupId() == null || pushTaskInfoChange.getGroupId().equals("")) {
            writeValue(new ResultInfo(0, "请输入推送群号"), response);
            return true;
        }
        if(pushTaskInfoChange.getJiaoWuUsername() == null || pushTaskInfoChange.getJiaoWuUsername().equals("")) {
            writeValue(new ResultInfo(0, "请输入教务系统账号"), response);
            return true;
        }
        if(pushTaskInfoChange.getJiaoWuPassword() == null || pushTaskInfoChange.getJiaoWuPassword().equals("")) {
            writeValue(new ResultInfo(0, "请入教务系统密码"), response);
            return true;
        }
        if(pushTaskInfoChange.getIsNowCourse() == null) {
            writeValue(new ResultInfo(0, "请选择推送时间"), response);
            return true;
        }
        if(pushTaskInfoChange.getClassName() == null || pushTaskInfoChange.getClassName().equals("")) {
            writeValue(new ResultInfo(0, "请输入班级"), response);
            return true;
        }
        if(pushTaskInfoChange.getIsPush() == null) {
            writeValue(new ResultInfo(0, "请选择是否推送"), response);
            return true;
        }
        if(pushTaskInfoChange.getAccountId() == null || pushTaskInfoChange.getAccountId().equals("")) {
            writeValue(new ResultInfo(0, "请输入管理员QQ"), response);
            return true;
        }
        if(pushTaskInfoChange.getPush_hour_min() == null || pushTaskInfoChange.getPush_hour_min().equals("")) {
            writeValue(new ResultInfo(0, "请输入推送时间"), response);
            return true;
        }
        return false;
    }

    /**
     * 封装前端传过来的任务信息
     * @param request
     * @return
     * @throws IOException
     */
    private PushTaskInfo getPushTaskInfo(HttpServletRequest request) throws IOException {

        //把前端传过来的推送信息封装成PushTeskInfo对象（修改的信息）
        PushTaskInfo pushTaskInfoChange = new PushTaskInfo();
        try {
            BeanUtils.populate(pushTaskInfoChange, request.getParameterMap());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return pushTaskInfoChange;
    }
}
