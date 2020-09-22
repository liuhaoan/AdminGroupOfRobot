
$(function () {



    // init feather icons
    feather.replace();

    // init tooltip & popovers
    $('[data-toggle="tooltip"]').tooltip();
    $('[data-toggle="popover"]').popover();

    //page scroll
    $('a.page-scroll').bind('click', function (event) {
        var $anchor = $(this);
        $('html, body').stop().animate({
            scrollTop: $($anchor.attr('href')).offset().top
        }, 1000);
        event.preventDefault();
    });

    //toggle scroll menu
    $(window).scroll(function () {
        var scroll = $(window).scrollTop();
        //adjust menu background
        if (scroll >= 100) {
            $('.sticky-navigation').addClass('bg-gradient');
            $('.stick-parallex').addClass('bg-primary');
        } else {
            $('.sticky-navigation').removeClass('bg-gradient');
            $('.stick-parallex').removeClass('bg-primary');
        }

        // adjust scroll to top
        if (scroll >= 600) {
            $('.scroll-top').addClass('active');
        } else {
            $('.scroll-top').removeClass('active');
        }
        return false;
    });

    // scroll top top
    $('.scroll-top').click(function () {
        $('html, body').stop().animate({
            scrollTop: 0
        }, 1000);
    });

    // init slick slider
    $('.slick-slider').slick({
        slidesToShow: 2,
        slidesToScroll: 1,
        dots: true,
        arrows: false,
        responsive: [
            {
                breakpoint: 768,
                settings: {
                    slidesToShow: 1,
                }
            }
        ]
    });

    $('.slick-users').slick({
        slidesToShow: 1,
        slidesToScroll: 1,
        autoplay: true,
        autoplaySpeed: 2000,
        dots: true,
        arrows: false
    });

    // resize slick slider on pill change
    $('a[data-toggle="pill"]').on('shown.bs.tab', function (e) {
        $('.slick-slider').each(function () {
            $(this).slick('setPosition');
        });
    })

    /**Theme switcher - DEMO PURPOSE ONLY s*/
    $('.switcher-trigger').click(function () {
        $('.switcher-wrap').toggleClass('active');
    });
    $('.color-switcher ul li').click(function () {
        var color = $(this).attr('data-color');
        $('#theme-color').attr("href", "css/" + color + ".css");
        $('.color-switcher ul li').removeClass('active');
        $(this).addClass('active');
    });

    //页面加载完毕查询是否已登入用户，已登入则修改页面并且列出用户信息
    $.post("/queryUserInfo", null, function (result) {
        if(result["state"] !== 0) {

            //已经登入，改变首页样式，显示用户信息
            h = $(".card");
            h.html("<div class=\"card-body pt-5\" style=\"text-align: center;\">" +
                        "<div class=\"form-group col-md-12 userLcon\"></div>" +
                        "<div class=\"form-group col-md-6\">" +
                            "<a class=\"btn btn-info\" href=\"/backstage\" role=\"button\">个人主页</a>" +
                        "</div>" +
                        "<div class=\"form-group col-md-6\">" +
                            "<a class=\"btn btn-info\" href=\"/backstage/console.html\" role=\"button\">控制面板</a>" +
                        "</div>" +
                        "<div class=\"form-group col-md-12\">" +
                            "<a class=\"btn btn-info\" href=\"/loginLast/user/logout\" role=\"button\">退出登入</a>" +
                        "</div>" +
                    "</div>");

            //导航栏去除登入注册，显示账户
            $("#navbarCollapse li:eq(1)").html("<a class=\"nav-link page-scroll\" href=\"/backstage\">" + result["user"] + "</a>");
            $("#navbarCollapse li:eq(2)").html("<a class=\"nav-link page-scroll\" href=\"/loginLast/user/logout\">退出登入</a>");

        }
    }, "json");

        //登入与注册的转换
    $(".registerBut").click(function() {
        $(".login").css("display", "none");
        $(".register").css("display", "inherit");
        getCheckCode();


        $(".login").animate({ opacity:'0'},"show");
        $(".register").animate({opacity:'1'},3000);
        $("#title").html("注册");
    });

    $(".loginBut").click(function() {
        $(".login").css("display", "inherit");
        $(".register").css("display", "none");
        getCheckCode();

        $(".register").animate({ opacity:'0'},"show");
        $(".login").animate({opacity:'1'},3000);
        $("#title").html("登入");
    });

    //生成验证码
    getCheckCode();
    function getCheckCode() {
        if($(".login").css("display") == "none") {
            img = $("#checkCodeImg1");
        }else {
            img = $("#checkCodeImg");
        }
        img.attr("src", "/checkCode?time=" + new Date().getTime());
    }

    //绑定验证码点击
    $("#checkCodeImg1").click(function () {
        getCheckCode();
    });
    $("#checkCodeImg").click(function () {
        getCheckCode();
    });

    //绑定登入按钮
    $("#login1").click(function () {
        //检查用户输入
        username = $(".card-body > .login input:eq(0)").val();
        password = $(".card-body > .login input:eq(1)").val();
        checkCode = $(".card-body > .login input:eq(2)").val();
        if(username == null || username === "") {
            alert("请输入账号！");
            return;
        }if(password == null || password === "") {
            alert("请输入密码！");
            return;
        }if(checkCode == null || checkCode === "") {
            alert("请输入验证码！");
            return;
        }
        pattern = /^(?=.*[0-9].*)(?=.*[a-z].*).{6,15}$/
        if(!pattern.test(username)) {
            alert("请输入正确的账号！");
            return;
        }
        if(!pattern.test(password)) {
            alert("请输入正确的密码！");
            return;
        }

        $.post("/loginBefore/user/login", {
            "username": $("#username").val(),
            "password": $("#password").val(),
            "checkCode": $("#checkCode").val()
        }, function (result) {
            if(result["state"] !== 0) {
                alert("登入成功");
                window.location.replace("/index.html")
            }else {
                alert(result["msg"])
                getCheckCode();
            }
        }, "json");
    });

    //绑定注册按钮
    $("#register1").click(function () {
        //检查用户输入
        username = $(".card-body > .register input:eq(0)").val();
        password = $(".card-body > .register input:eq(1)").val();
        password1 = $(".card-body > .register input:eq(2)").val();
        email = $(".card-body > .register input:eq(3)").val();
        checkCode = $(".card-body > .register input:eq(4)").val();
        if(username == null || username === "") {
            alert("请输入账号！");
            return;
        }if(password == null || password === "") {
            alert("请输入密码！");
            return;
        }if(password1 == null || password1 === "") {
            alert("请确认密码！");
            return;
        }if(email == null || email === "") {
            alert("请输入邮箱！");
            return;
        }if(checkCode == null || checkCode === "") {
            alert("请输入验证码！");
            return;
        }
        pattern = /^(?=.*[0-9].*)(?=.*[a-z].*).{6,15}$/;
        pattern1 = /\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/;
        if(!pattern.test(username)) {
            alert("请输入正确的账号！");
            return;
        }if(!pattern.test(password)) {
            alert("请输入正确的密码！");
            return;
        }if(!pattern.test(password1)) {
            alert("两次输入密码不一致！");
            return;
        }if(!pattern1.test(email)) {
            alert("邮箱格式不正确！");
            return;
        }if(username === password) {
            alert("账号和密码不能一致！");
            return;
        }


        $.post("/loginBefore/user/register",
            {
                "username": $("#usernameReg").val(),
                "password": $("#passwordReg").val(),
                "email": $("#email").val(),
                "checkCode": $("#checkCodeReg").val(),
            },
            function (result) {
                if(result["state"] !== 0) {
                    alert("注册成功");
                    window.location.replace("/index.html")
                }else {
                    alert(result["msg"])
                    getCheckCode();
                }
        }, "json");
    });

});

