$(function () {
    //获取账号
    $.post("/queryUserInfo", null, function (result) {
        $(".userInfo > form input:eq(0)").attr("value", result["user"])
        $(".userInfo > form input:eq(3)").attr("value", result["email"])
        //修改头像和账号显示
        $(".sidenav-header-inner:eq(0)").html(
            "<img src=\"img/avatar-7.jpg\" alt=\"person\" class=\"img-fluid rounded-circle\">" +
            "<h2 class=\"h5\">" + result["user"] + "</h2><span>Web Developer</span>");
    }, "json");


    //绑定修改按钮
    $("#change").click(function () {
        $.post("/loginLast/user/passwordChange", {
            "username": $(".userInfo > form input:eq(0)").val(),
            "password": $(".userInfo > form input:eq(1)").val(),
            "passwordNew": $(".userInfo > form input:eq(2)").val(),
            "email": $(".userInfo > form input:eq(3)").val(),
            "checkCode": $(".userInfo > form input:eq(4)").val()
        }, function (result) {
            var toast = $('.toast');
            if(result["state"] === 0) {
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
        }, "json")
    });

    //生成验证码
    getCheckCode();
    function getCheckCode() {
        $(".userInfo > form img").attr("src", "/checkCode?time=" + new Date().getTime());
    }

    //绑定验证码点击
    $(".userInfo > form img").click(function () {
        getCheckCode();
    });
});