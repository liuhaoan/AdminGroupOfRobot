$(function () {

    //获取账号信息
    $.post("/queryUserInfo", null, function (result) {
        //修改头像和账号显示
        $(".sidenav-header-inner:eq(0)").html(
            "<img src=\"img/avatar-7.jpg\" alt=\"person\" class=\"img-fluid rounded-circle\">" +
            "<h2 class=\"h5\">" + result["user"] + "</h2><span>Web Developer</span>");
    }, "json");


    //获取分页任务信息，并且初始化页面
    $.post("/loginLast/task/queryPageTask", {
        "currentPage": 1,
        "pageSize" : 5
    }, function (result) {
        //初始化
        init(result);
    }, "json");
});


function init(result) {
    if(result["state"] == 0) {
        var toast = $('.toast');
        $(".toast strong").html("提示")
        $(".toast .toast-body").html(result["msg"]);
        toast.toast("show");
        return;
    }
    //把任务列表显示在表格中
    showTeskList(result["list"]);

    //生成导航条
    showPageNav(result);


}//init结束



//修改与删除任务
var tds;
function change (t) {
    tds = $(t).parent().parent().siblings();
}
//保存修改
function changeSave() {
    $.post("/loginLast/task/change", {
        "id": tds.html(),
        "jiaoWuUsername": tds.children("#jiaoWuUsername").val(),
        "jiaoWuPassword": tds.children("#jiaoWuPassword").val(),
        "groupId": tds.children("#groupId").val(),
        "accountId": tds.children("#accountId").val(),
        "push_hour_min": tds.children().children("#timeHour").val() + ":" + tds.children().children("#timeMin").val(),
        "className": tds.children("#className").val(),
        "isPush": tds.children("#isPush").val(),
        "isNowCourse": tds.children("#isNowCourse").val(),
    }, function (result) {
        var toast = $('.toast');
        if(result["state"] == 0) {
            $(".toast strong").html("提示")
            $(".toast .toast-body").html(result["msg"])

            toast.toast("show");
        }else {
            $(".toast strong").html("提示")
            $(".toast .toast-body").html("修改成功，一秒后自动跳转。")

            toast.toast("show");

            toast.on('hidden.bs.toast', function () {
                window.location.reload();
            });
        }
    });
}

//删除任务
function del(t) {
    tds = $(t).parent().parent().siblings();
}
function delSave() {
    $.post("/loginLast/task/del", {
        "id": tds.html()
    }, function (result) {
        var toast = $('.toast');
        if(result["state"] == 0) {
            $(".toast strong").html("提示")
            $(".toast .toast-body").html(result["msg"]);
            toast.toast("show");
        }else {
            $(".toast strong").html("提示")
            $(".toast .toast-body").html("修改成功，一秒后自动跳转。")


            toast.toast("show");
            toast.on('hidden.bs.toast', function () {
                window.location.reload();
            });
        }
    });
}


//把任务列表显示在表格中
function showTeskList(teskList) {
    //获取任务列表遍历
    var id = 0;
    var html = "<tr>\n" +
        "            <td>添加任务</td>\n" +
        "            <td><input id=\"classNameAdd\" type=\"text\" class=\"form-control\" placeholder=\"班级\"></td>\n" +
        "            <td><input id=\"jiaoWuUsernameAdd\" type=\"text\" class=\"form-control\" placeholder=\"教务系统账号\"></td>\n" +
        "            <td><input id=\"jiaoWuPasswordAdd\" type=\"text\" class=\"form-control\" placeholder=\"教务系统密码\"></td>\n" +
        "            <td><input id=\"groupIdAdd\" type=\"text\" class=\"form-control\" placeholder=\"授权群号\"></td>\n" +
        "            <td><input id=\"accountIdAdd\" type=\"text\" class=\"form-control\" placeholder=\"管理员QQ\"></td>\n" +
        "            <td>\n" +
        "              <div class=\"time\">\n" +
        "                <select name=\"\" id=\"timeHourAdd\">\n" +
        "\n" +
        "                </select>\n" +
        "                ：\n" +
        "                <select name=\"\" id=\"timeMinAdd\">\n" +
        "\n" +
        "                </select>\n" +
        "              </div>\n" +
        "            </td>\n" +
        "            <td>\n" +
        "              <select id=\"isPushAdd\">\n" +
        "                <option value=\"1\">是</option>\n" +
        "                <option value=\"0\">否</option>\n" +
        "              </select>\n" +
        "            </td>\n" +
        "            <td>\n" +
        "              <select id=\"isNowCourseAdd\">\n" +
        "                <option value=\"1\">今天</option>\n" +
        "                <option value=\"0\">明天</option>\n" +
        "              </select>\n" +
        "            </td>\n" +
        "            <td><button id=\"add\" type=\"button\" class=\"btn btn-info\">添加任务</button></td>\n" +
        "          </tr>\n" +
        "          <tr>\n" +
        "            <th>任务编号</th>\n" +
        "            <th>班级</th>\n" +
        "            <th>教务系统账号</th>\n" +
        "            <th>教务系统密码</th>\n" +
        "            <th>绑定群号</th>\n" +
        "            <th>管理员QQ</th>\n" +
        "            <th>推送时间</th>\n" +
        "            <th>是否开启推送</th>\n" +
        "            <th>推送哪天的课表</th>\n" +
        "            <th>操作</th>\n" +
        "          </tr>";


    //判断任务是否为空
    if(teskList == null) {
        var toast = $('.toast');
        $(".toast strong").html("提示")
        $(".toast .toast-body").html("没有数据···");
        toast.toast("show");
        return;
    }
    //遍历记录，循环一次表格一行
    teskList.forEach(tesk => {
        id++;
        //遍历当前用户所有任务信息
        var hourHtml = "", minHtml = "", pushTime = tesk["push_hour_min"].split(":"), hour = pushTime[0], min = pushTime[1];

        for(var i = 0; i < 60; i++) {
            var t = i;
            if(t < 10) {
                t = "0" + t;
            }
            if(i <= 24) {
                //设置默认选项
                if(t == hour) {
                    hourHtml += "<option value=\"" + t + "\" selected>" + t + "</option>";
                }else {
                    hourHtml += "<option value=\"" + t + "\">" + t + "</option>";
                }
            }
            if(t == min) {
                minHtml += "<option value=\"" + t + "\" selected>" + t + "</option>";
            }else {
                minHtml += "<option value=\"" + t + "\">" + t + "</option>";
            }
        }

        //生成是否开始推送
        var isPushHtml;
        if(tesk["isPush"] == 0) {
            isPushHtml += "                  <option value=\"1\">是</option>\n";
            isPushHtml += "                  <option selected value=\"0\">否</option>\n";
        }else {
            isPushHtml += "                  <option selected value=\"1\">是</option>\n";
            isPushHtml += "                  <option value=\"0\">否</option>\n";
        }

        //生成推送时间选项卡
        var isNowCourseHtml;
        if(tesk["isNowCourse"] != 0) {
            isNowCourseHtml += "                  <option value=\"0\">明天</option>\n";
            isNowCourseHtml += "                  <option selected value=\"1\">今天</option>\n";
        }else {
            isNowCourseHtml += "                  <option selected value=\"0\">明天</option>\n";
            isNowCourseHtml += "                  <option value=\"1\">今天</option>\n";
        }

        //生成表格中的一项数据
        var htmlTemp = "<tr>\n" +
            "              <td id=\"id\">" + id + "</td>\n" +
            "              <td>\n" +
            "                <input id=\"className\" type=\"text\" class=\"form-control\" value=\"" + tesk["className"] + "\"></td>\n" +
            "              <td>\n" +
            "                <input id=\"jiaoWuUsername\" type=\"text\" class=\"form-control\" value=\"" + tesk["jiaoWuUsername"] + "\"></td>\n" +
            "              <td>\n" +
            "                <input id=\"jiaoWuPassword\" type=\"text\" class=\"form-control\" value=\"" + tesk["jiaoWuPassword"] + "\">\n" +
            "              </td>\n" +
            "              <td>\n" +
            "                <input id=\"groupId\" type=\"text\" class=\"form-control\" value=\"" + tesk["groupId"] + "\">\n" +
            "              </td>\n" +
            "              <td>\n" +
            "                <input id=\"accountId\" type=\"text\" class=\"form-control\" value=\"" + tesk["accountId"] + "\">\n" +
            "              </td>\n" +
            "              <td>\n" +
            "                <div class=\"time\">\n" +
            "                  <select name=\"\" id=\"timeHour\">" + hourHtml +
            "                  </select>\n" +
            "                  ：\n" +
            "                  <select name=\"\" id=\"timeMin\">" + minHtml +
            "                  </select>\n" +
            "                </div>\n" +
            "              </td>\n" +
            "              <td>\n" +
            "                <select name=\"\" id=\"isPush\">\n" + isPushHtml +
            "                </select>\n" +
            "              </td>\n" +
            "              <td>\n" +
            "                <select name=\"\" id=\"isNowCourse\">\n" + isNowCourseHtml +
            "                </select>\n" +
            "              </td>\n" +
            "              <td>\n" +
            "              <div class='btn-group'>\n" +
            "                <button id=\"change\" type=\"button\" class=\"btn btn-info\" data-toggle=\"modal\" data-target=\"#changeModal\" onclick='change(this)'>修改</button>\n" +
            "                <button id=\"remove\" type=\"button\" class=\"btn btn-info\"  data-toggle=\"modal\" data-target=\"#delModal\" onclick='del(this)'>删除</button>\n" +
            "              </div>\n" +
            "              </td>\n" +
            "            </tr>"

        //表格内容显示出来
        html += htmlTemp
    });//遍历显示任务完成
    $("#tableChange").html(html);

    //添加任务处，时间选择卡处理
    var timeMinAddHtml = "", timeHourAddHtml = "";
    for(var i = 0; i <60; i++) {
        var t = i;
        if(t < 10) {
            t = "0" + t;
        }
        timeMinAddHtml += "<option value=\"" + t + "\">" + t + "</option>";
        if(i === 24) {
            timeHourAddHtml = timeMinAddHtml;
        }
    }
    $("#timeHourAdd").html(timeHourAddHtml);
    $("#timeMinAdd").html(timeHourAddHtml);


    //绑定添加任务按钮
    $("#add").click(function () {
        $.post("/loginLast/task/add", {
            "id": -1,
            "jiaoWuUsername": $("#jiaoWuUsernameAdd").val(),
            "jiaoWuPassword": $("#jiaoWuPasswordAdd").val(),
            "groupId": $("#groupIdAdd").val(),
            "accountId": $("#accountIdAdd").val(),
            "push_hour_min": $("#timeHourAdd").val() + ":" + $("#timeMinAdd").val(),
            "className": $("#classNameAdd").val(),
            "isPush": $("#isPushAdd").val(),
            "isNowCourse": $("#isNowCourseAdd").val(),
        }, function (result) {
            var toast = $('.toast');
            if(result["state"] == 0) {
                $(".toast strong").html("提示")
                $(".toast .toast-body").html(result["msg"]);
                toast.toast("show");
            }else {
                $(".toast strong").html("提示")
                $(".toast .toast-body").html("添加成功，一秒后自动跳转。")

                toast.toast("show");
                toast.on('hidden.bs.toast', function () {
                    window.location.reload();
                });
            }
        });
    });
}

//前往目标页码
function goPage(pageIdex) {
    $.post("/loginLast/task/queryPageTask", {
        "currentPage": + pageIdex,
        "pageSize" : 5
    }, function (result) {
        if(result["state"] == 0) {
            var toast = $('.toast');
            $(".toast strong").html("提示")
            $(".toast .toast-body").html(result["msg"]);
            toast.toast("show");
            return;
        }
        //显示任务信息
        showTeskList(result["list"])
        showPageNav(result)
    }, "json");
}

//生成页码导航
function showPageNav(pageBean) {
    //生成页码导航条
    var pageNavHtml = "";
    //当前页码等于1则上一页不能点
    if(pageBean["currentPage"] == 1) {
        pageNavHtml += "<li class=\"page-item disabled\">";
    }else {
        pageNavHtml += "<li class=\"page-item\">"
    }
    //上一页
    pageNavHtml += "<a class=\"page-link\" href=\"#\" aria-label=\"Previous\">\n" +
        "                <span aria-hidden=\"true\">&laquo;</span>\n" +
        "              </a>\n" +
        "            </li>";

    //生成页码
    for(var i = 1; i <= pageBean["totalPage"]; i++) {
        if(pageBean["currentPage"] == i) {
            pageNavHtml += "<li class=\"page-item active\">\n" +
                "              <span class=\"page-link\">" + i + "</span>\n" +
                "            </li>";
        }else {
            pageNavHtml += "<li class=\"page-item\">\n" +
                "              <a class=\"page-link\" href=\"#\">" + i + "</a>\n" +
                "            </li>";
        }

    }

    //下一页，当前是最后一页则不能点下一页
    if(pageBean["currentPage"] === pageBean["totalPage"]) {
        pageNavHtml += "<li class=\"page-item disabled\">";
    }else {
        pageNavHtml += "<li class=\"page-item\">"
    }
    pageNavHtml += "<a class=\"page-link\" href=\"#\" aria-label=\"Next\">\n" +
        "                <span aria-hidden=\"true\">&raquo;</span>\n" +
        "              </a>\n" +
        "            </li>";

    //写出到html文档
    $("#pageNav").html(pageNavHtml);

    //绑定页码导航条每一个a标签
    $("#pageNav a").click(function (obj) {
        var _this = $(obj.target);
        var html = $(_this).html();
        //判断a标签的html内容是否为数字，不是数字代表上下页按钮
        if(isNaN(html)) {
            var pageIndex = parseInt(_this.parent().siblings(".active").children("span").html());
            //跳转下一页
            if(_this.attr("aria-label") === "Previous") {
                //上一页
                goPage(pageIndex - 1);
            }else if(_this.attr("aria-label") === "Next") {
                //下一页
                goPage(pageIndex + 1);
            }
        }else {
            //跳转相应页面
            goPage(parseInt(html));
        }
    });
}