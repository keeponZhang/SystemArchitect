package com.darren.architect_day33.simple1.bean;

/**
 * Created by hcDarren on 2017/12/16.
 */

public class UserInfo {
    public String userName;
    public String userSex;

    @Override
    public String toString() {
        return "UserInfo{" +
                "userName='" + userName + '\'' +
                ", userPwd='" + userSex + '\'' +
                '}';
    }
}
