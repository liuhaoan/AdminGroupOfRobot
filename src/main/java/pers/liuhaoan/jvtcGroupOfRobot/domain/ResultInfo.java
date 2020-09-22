package pers.liuhaoan.jvtcGroupOfRobot.domain;

public class ResultInfo {
    /**
     * 客户端请求后返回的结果消息
     */

    private int state;          //状态码，非0即真
    private String msg;         //反馈的信息

    public ResultInfo() {
    }

    public ResultInfo(int state, String msg) {
        this.state = state;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResultInfo{" +
                "state=" + state +
                ", msg='" + msg + '\'' +
                '}';
    }

    public int getState() {
        return state;
    }

    public String getMsg() {
        return msg;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
