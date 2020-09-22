package pers.liuhaoan.jvtcGroupOfRobot.domain;

import java.util.List;

public class User {
    private String username;
    private String password;
    private String passwordNew;
    private String checkCode;
    private String email;

    public User() {
    }

    public User(String username, String password, String passwordNew, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.passwordNew = passwordNew;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", checkCode='" + checkCode + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public void setPasswordNew(String passwordNew) {
        this.passwordNew = passwordNew;
    }

    public String getPasswordNew() {
        return passwordNew;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
