package pers.liuhaoan.jvtcGroupOfRobot.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pers.liuhaoan.jvtcGroupOfRobot.domain.Course;
import pers.liuhaoan.jvtcGroupOfRobot.domain.PushTaskInfo;
import pers.liuhaoan.jvtcGroupOfRobot.enums.WeekEnum;
import pers.liuhaoan.jvtcGroupOfRobot.service.JvtcJiaoWuService;
import pers.liuhaoan.jvtcGroupOfRobot.utils.HttpUtil;
import pers.liuhaoan.jvtcGroupOfRobot.utils.JedisUtil;
import redis.clients.jedis.Jedis;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JvtcJiaoWuServiceImpl implements JvtcJiaoWuService {
    private PushTaskInfo pushTaskInfo;

//    private Map<String, String> header;

    public JvtcJiaoWuServiceImpl(PushTaskInfo pushTaskInfo) {
        if(pushTaskInfo.isNull()) {
            System.out.println("推送信息不完整");
            return;
        }
        this.pushTaskInfo = pushTaskInfo;
        //定义header头部
//        header = new HashMap<>();
//        header.put("Accept", "text/html, application/xhtml+xml, image/jxr, */*");
//        header.put("Origin", "http://jiaowu.jvtc.jx.cn");
//        header.put("Referer", "http://jiaowu.jvtc.jx.cn/jsxsd/");
//        header.put("Accept-Language", "zh-CN");
//        header.put("PushTaskInfo-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 10.0; WOW64; Trident/7.0)");
//        header.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//        header.put("Accept-Encoding", "gzip, deflate");
//        header.put("Content-Length", "94");
//        header.put("Host", "jiaowu.jvtc.jx.cn");
//        header.put("Proxy-Connection", "Keep-Alive");
//        header.put("Pragma", "no-cache");
    }

    @Override
    public List<Course> getCourseDayToWeek(WeekEnum weekEnum) {
        List<Course> resultList = new ArrayList<>();
        Stream<Course> courseStream = getThisWeekCourse(false).stream().filter((w) -> w.getWeek().equals(weekEnum.show()));
        courseStream.forEach(resultList::add);
        return resultList;
    }

    @Override
    public List<Course> getCourseTomorrow() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);

        int week = c.get(Calendar.DAY_OF_WEEK) - 1;
        List<Course> resultList = new ArrayList<>();
        getThisWeekCourse(true).stream().filter((w) -> w.getWeek().equals(week)).forEach(resultList::add);
        return resultList;
    }

    @Override
    public List<Course> getCourseNow() {
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        List<Course> resultList = new ArrayList<>();
        //获取本周所有课表，并且获取当天的课程集合
        getThisWeekCourse(false).stream().filter((w) -> w.getWeek().equals(week)).forEach(resultList::add);
        return resultList;
    }


    @Override//获取课表的主方法，其他方法都在此基础上加强
    public List<Course> getThisWeekCourse(boolean nextDay) {
        Jedis jedis = JedisUtil.getJedis();
        String str;
        List<Course> resultList;

        //判断从redis中获取还是在教务管理系统里获取
        if((str = jedis.get(pushTaskInfo.getGroupId())) != null) {
            System.out.println("获取redis数据库课表");
            //redis缓存了有那么就直接json转对象
            resultList = Optional.ofNullable(jsonToListCourse(str)).orElse(new ArrayList<>());
        }else {
            System.out.println("获取教务管理系统课表");
            resultList = getJvtcCourse(nextDay);
            //把班级课表结果的json数据缓存到redis数据库中
            //加入数据库
            jedis.set(pushTaskInfo.getGroupId(), courseListToJson(resultList));
            //获取当天24点的时间戳，并且设置数据有效时间
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 24);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);

            //测试一分钟后过期
//            c.add(Calendar.MINUTE, 1);
            jedis.expireAt(pushTaskInfo.getGroupId(), c.getTimeInMillis() / 1000);
        }
        jedis.close();
        //课程排序
        resultList.sort(Comparator.comparingInt(Course::getSort));

        return resultList;
    }








    //私有方法



    /**
     * 获取教务管理系统的课表
     * @param nextDay 是否获取明天课表，true为明天，一般星期天的时候用
     * @return List<Course>类型的课表集
     */
    private List<Course> getJvtcCourse(boolean nextDay) {
        //登入并返回cookies
        String cookies = login();
        if(cookies == null || cookies.equals("")) return new ArrayList<>();

        //生成data  rq=2019-12-08
        Calendar calendar = Calendar.getInstance();
        if(nextDay) calendar.add(Calendar.DAY_OF_MONTH, 1);//加一天
        String data = "rq=" + calendar.get(Calendar.YEAR) + "-"
                + String.format("%2s", (calendar.get(Calendar.MONTH) + 1)).replace(" ", "0") + "-"
                + String.format("%2s", calendar.get(Calendar.DAY_OF_MONTH)).replace(" ", "0");

//        HttpUtil http = new HttpUtil.Builder("http://jiaowu.jvtc.jx.cn/jsxsd/framework/xsMain.jsp").setCookies(cookies).setMethod("get").build();
        //获取本星期所有课程的html源码
        String courseHtml  = new HttpUtil.Builder("http://jiaowu.jvtc.jx.cn/jsxsd/framework/main_index_loadkb.jsp")
                .setCookies(cookies).setMethod("post").setData(data).build().getHtmlText();
        //查询到一个班的课表结果，并且把html代码转为Course对象
        return Optional.ofNullable(parseHtmlCourse(courseHtml)).orElse(new ArrayList<>());
    }

    /**
     * 把List<Course>类型的json文本转回此类型
     * @param courseJson json文本
     * @return 返回List<Course>
     */
    private List<Course> jsonToListCourse(String courseJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Course> courseList = new ArrayList<>();
        try {
            //先转为list
            List list = Optional.ofNullable(objectMapper.readValue(courseJson, List.class)).orElse(new ArrayList());

            //再转为list<Course>
            list.forEach(o -> {
                try {
                    Course course = objectMapper.readValue(o.toString(), Course.class);
                    courseList.add(course);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courseList;
    }


    /**
     * List<Course> 转 json
     * @param courseList List<Course>对象
     * @return json
     */
    private String courseListToJson(List<Course> courseList) {
        //把课程结果转为JSON
        String resultListJson = null;
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> stringList = new ArrayList<>();

        //先把list<Course> 转为 List<String>
        courseList.stream().map(l -> {
            try {
                return objectMapper.writeValueAsString(l);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }).forEach(stringList::add);

        //List<String> 转 String
        try {
            resultListJson = objectMapper.writeValueAsString(stringList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return resultListJson;
    }

    /**
     * 解析html文本的课程表
     * @param htmlCourse html文本课程表
     * @return 返回所有课程的集合
     */
    private List<Course> parseHtmlCourse(String htmlCourse) {
        Matcher matcher = Pattern.compile("<tbody>(.*?)</tbody>").matcher(htmlCourse);
        List<Course> resultList = new ArrayList<>();
        //没有获取到课表
        if(!matcher.find())return null;
        //运行到这里，去除了除课表以外的html代码
        String str = matcher.group();

        //获取一个星期中每节课，它可以匹配6个结果出来，因为每天最多6节课，而每个结果代表星期一到日的某节课的课程
        Matcher matcher1 = Pattern.compile("<tr>(.*?)</tr>").matcher(str);
        for (int i = 1; matcher1.find(); i++) {
            //获取到一个星期的每节课，如下： i为课程序号，也就是第几节课
            //<tr>		    <td>第一大节<br/>08:05-09:45</td>.................
            //<tr>		    <td>第二大节<br/>10:20-12:00</td>.................
            //<tr>		    <td>第三大节<br/>14:05-15:45</td>.................
            //<tr>		    <td>第四大节<br/>16:00-17:35</td>.................
            //<tr>		    <td>第五大节<br/>17:45-19:20</td>.................
            //<tr>		    <td>第六大节<br/>19:30-21:10</td>.................
            String str1 = matcher1.group();

            Matcher matcher2 = Pattern.compile("<td>(.*?)</td>").matcher(str1);
            //获取每节课中星期一到星期日的课程
            String courseTime = "";//课程时间
            for(int j = 0; matcher2.find(); j++) {
                String str2 = matcher2.group(1);

                //第一个是课程序号和时间，这里把时间记录一下就可以不再操作了
                if(j == 0) {
                    courseTime = str2.split("<br/>")[1];
                    continue;
                }


                //获取每个星期某节课的课程
                Matcher matcher3 = Pattern.compile("<p title = '(.*?)'").matcher(str2);
                while (matcher3.find()) {
                    String str4 = matcher3.group(1).replace("<br/>", "\r\n");
//                    System.out.println(str4 + "\r\n");
                    Course course = new Course(i, j, courseTime, str4, pushTaskInfo.getClassName());
                    resultList.add(course);
                }
            }
        }
        return resultList;
    }

    /**
     * 登入教务管理系统
     * @return 返回cookies
     */
    private String login() {
        //生成请求数据
        String encode = getEncode(pushTaskInfo.getJiaoWuUsername()) + "%25%25%25" +
                getEncode(pushTaskInfo.getJiaoWuPassword());

        String data = "userAccount=" + pushTaskInfo.getJiaoWuUsername()
                + "&userPassword=" + pushTaskInfo.getJiaoWuUsername()
                + "&encoded=" + encode;

        //先获取cookie
        String cookies = new HttpUtil.Builder("http://jiaowu.jvtc.jx.cn/jsxsd/").setMethod("get").build().getCookies();
        //登入教育系统
        HttpUtil post = new HttpUtil.Builder("http://jiaowu.jvtc.jx.cn/jsxsd/xk/LoginToXk").setMethod("post").setData(data).setCookies(cookies).setIsReturn(false).build();
        return cookies;
    }

    /**
     * 获取登入教务管理系统所需要的encode
     * @return 返回encode
     */
    private String getEncode(String data) {
        //获取脚本管理对象
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");

        //加载脚本
        try {
            engine.eval(new InputStreamReader(Objects.requireNonNull(JvtcJiaoWuServiceImpl.class.getClassLoader().getResourceAsStream("jvtcEncode.js"))));

            //转为Invocable执行方法
            Invocable invocable = (Invocable) engine;
            return (String) invocable.invokeFunction("encodeInp", data);

        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException ignored) {
            System.out.println("js脚本方法名错误");
        }

        return null;
    }
}
